package com.github.atais.map

import java.util
import java.util.concurrent.ConcurrentHashMap

import scala.collection.GenSeq

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapRemoveBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "remove" in {

      def bench[T](r: Int, m: util.AbstractMap[T, T], f: GenSeq[() => T]) = {
        //        assert(m.size == r, s"""${m.size} start map size is not equal $r"""")
        val res = f.map(_ ())
        //        assert(m.isEmpty, s"""${m.size} end map size is not equal 0"""")
        res
      }

      using(for {r <- sizes} yield {
        val m = new util.HashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.remove(i))
        (r, m, f)
      }) curve hashMapS setUp {
        case (r, m, _) => (0 until r).foreach(i => m.put(i, i))
      } in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.remove(i))
        (r, m, f)
      }) curve concurrentHashMapS setUp {
        case (r, m, _) => (0 until r).foreach(i => m.put(i, i))
      } in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.remove(i)).par
        (r, m, f)
      }) curve sharedHashMapS setUp {
        case (r, m, _) => (0 until r).foreach(i => m.put(i, i))
      } in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val t = (0 until cores).map(t => {
          val m = new util.HashMap[Int, Int]()
          val f = (0 + t until r by cores).map(i => () => m.remove(i))
          (m, f)
        })
        (r, t.map(_._1), t.map(_._2).par)
      }) curve differentHashMapS setUp {
        case (r, m, _) =>
          (0 until cores).foreach(t =>
            (0 + t until r by cores).foreach(i => m(t).put(i, i)))
      } in {
        case (r, m, f) =>
          //          assert(m.map(_.size).sum == r, s"""${m.map(_.size).sum} is not equal $r"""")
          val res = f.map(_.map(_ ()))
          //          assert(m.map(_.size).sum == 0, s"""${m.map(_.size).sum} is not equal 0"""")
          res
      }
    }
  }
}
