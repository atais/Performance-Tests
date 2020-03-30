package com.github.atais.map

import java.util
import java.util.concurrent.ConcurrentHashMap

import scala.collection.GenSeq

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapAddBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "add" in {

      def bench[T](r: Int, m: util.AbstractMap[T, T], f: GenSeq[() => T]) = {
        //        assert(m.size == 0, s"""${m.size} map size is not equal 0"""")
        val res = f.map(_ ())
        //        assert(m.size == r, s"""${m.size} map size is not equal $r"""")
        res
      }

      using(for {r <- sizes} yield {
        val m = new util.HashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.put(i, i))
        (r, m, f)
      }) curve hashMapS tearDown {
        case (_, m, _) => m.clear()
      } in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.put(i, i))
        (r, m, f)
      }) curve concurrentHashMapS tearDown {
        case (_, m, _) => m.clear()
      } in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.put(i, i)).par
        (r, m, f)
      }) curve sharedHashMapS tearDown {
        case (_, m, _) => m.clear()
      } in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val t = (0 until cores).map(t => {
          val m = new util.HashMap[Int, Int]()
          val f = (0 + t until r by cores).map(i => () => m.put(i, i))
          (m, f)
        })
        (r, t.map(_._1), t.map(_._2).par)
      }) curve differentHashMapS tearDown {
        case (_, m, _) => m.foreach(_.clear())
      } in {
        case (r, m, f) =>
          //          assert(m.map(_.size).sum == 0, s"""${m.map(_.size).sum} map size is not equal 0"""")
          val res = f.map(_.map(_ ()))
          //          assert(m.map(_.size).sum == r, s"""${m.map(_.size).sum} map size is not equal $r"""")
          res

      }
    }
  }
}
