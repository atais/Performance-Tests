package com.github.atais.map

import java.util
import java.util.concurrent.ConcurrentHashMap

import scala.collection.GenSeq

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapGetBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "get" in {

      def bench[T](r: Int, m: util.AbstractMap[T, T], f: GenSeq[() => T]) = {
        //        assert(m.size == r, s"""${m.size} map size is not equal $r"""")
        val res = f.map(_ ())
        //        assert(res.size == r, s"""${res.size} result size is not equal $r"""")
        res
      }

      using(for {r <- sizes} yield {
        val map = new util.HashMap[Int, Int]()
        val tasks = (0 until r).map(i => {
          map.put(i, i)
          () => map.get(i)
        })
        (r, map, tasks)
      }) curve hashMapS in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val map = new ConcurrentHashMap[Int, Int]()
        val tasks = (0 until r).map(i => {
          map.put(i, i)
          () => map.get(i)
        })
        (r, map, tasks)
      }) curve concurrentHashMapS in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val map = new ConcurrentHashMap[Int, Int]()
        val tasks = (0 until r).map(i => {
          map.put(i, i)
          () => map.get(i)
        })
        (r, map, tasks.par)
      }) curve sharedHashMapS in {
        case (r, m, f) => bench(r, m, f)
      }

      using(for {r <- sizes} yield {
        val t = (0 until cores).map { t =>
          val m = new util.HashMap[Int, Int]()
          val tasks = (0 + t until r by cores).map(i => {
            m.put(i, i)
            () => m.get(i)
          })
          (m, tasks)
        }
        (r, t.map(_._1), t.map(_._2).par)
      }) curve differentHashMapS in {
        case (r, m, t) =>
          //          assert(m.map(_.size).sum == r, s"""${m.map(_.size).sum} map size is not equal $r"""")
          val res = t.map(_.map(_ ()))
          //          assert(res.map(_.size).sum == r, s"""${res.size} result size is not equal $r"""")
          res
      }
    }
  }
}
