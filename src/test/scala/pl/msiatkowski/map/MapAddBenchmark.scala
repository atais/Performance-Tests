package pl.msiatkowski.map

import java.util
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapAddBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "add" in {
      benchmark(hashMapS, parallel = false)({
        () => new util.HashMap[Int, Int]()
      })({
        (r, m, t) => t ++= (0 until r).map(i => () => m.put(i, i))
      })({
        (r, m, t) =>
          t.clear()
          m.clear()
      })

      benchmark(concurrentHashMapS, parallel = false)({
        () => new ConcurrentHashMap[Int, Int]()
      })({
        (r, m, t) => t ++= (0 until r).map(i => () => m.put(i, i))
      })({
        (r, m, t) =>
          t.clear()
          m.clear()
      })

      benchmark(sharedHashMapS, parallel = true)({
        () => new ConcurrentHashMap[Int, Int]()
      })({
        (r, m, t) => t ++= (0 until r).map(i => () => m.put(i, i))
      })({
        (r, m, t) =>
          t.clear()
          m.clear()
      })

      benchmark(differentHashMapS, parallel = true)({
        () => (0 until cores).map(_ => new util.HashMap[Int, Int]())
      })({
        (r, m, t) =>
          t ++= (0 until cores).flatMap { t =>
            (0 + t until r by cores).map(i => () => m(t).put(i, i))
          }
      })({
        (r, m, t) =>
          t.clear()
          m.foreach(_.clear())
      })
    }
  }
}
