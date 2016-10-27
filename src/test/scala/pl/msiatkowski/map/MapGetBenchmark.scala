package pl.msiatkowski.map

import java.util
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapGetBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "get" in {
      using(for {r <- sizes} yield {
        val map = new util.HashMap[Int, Int]()
        (0 until r).map(i => {
          map.put(i, i)
          () => map.get(i)
        })
      }) curve hashMapS in {
        t => t.foreach(_())
      }

      using(for {r <- sizes} yield {
        val map = new ConcurrentHashMap[Int, Int]()
        (0 until r).map(i => {
          map.put(i, i)
          () => map.get(i)
        })
      }) curve concurrentHashMapS in {
        t => t.foreach(_())
      }

      using(for {r <- sizes} yield {
        val map = new ConcurrentHashMap[Int, Int]()
        (0 until r).map(i => {
          map.put(i, i)
          () => map.get(i)
        }).par
      }) curve sharedHashMapS in {
        t => t.foreach(_())
      }

      using(for {r <- sizes} yield {
        (0 until cores).flatMap { t =>
          val map = new util.HashMap[Int, Int]()
          (0 + t until r by cores).map(i => {
            map.put(i, i)
            () => map.get(i)
          })
        }.par
      }) curve differentHashMapS in {
        t => t.foreach(_())
      }
    }
  }
}
