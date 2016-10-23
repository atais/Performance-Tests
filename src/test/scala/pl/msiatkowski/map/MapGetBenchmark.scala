package pl.msiatkowski.map

import java.util
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapGetBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "get" in {
      benchmark(hashMapS, parallel = false)({
        () => new util.HashMap[Int, Int]()
      })({
        (r, m, t) => {
          if (m.isEmpty) (0 until r).map(i => m.put(i, i))
          t ++= (0 until r).map(i => () => m.get(i))
        }
      })({
        (r, m, t) =>
          t.clear()
      })

      benchmark(concurrentHashMapS, parallel = false)({
        () => new ConcurrentHashMap[Int, Int]()
      })({
        (r, m, t) => {
          if (m.isEmpty) (0 until r).map(i => m.put(i, i))
          t ++= (0 until r).map(i => () => m.get(i))
        }
      })({
        (r, m, t) =>
          t.clear()
      })

      benchmark(sharedHashMapS, parallel = true)({
        () => new ConcurrentHashMap[Int, Int]()
      })({
        (r, m, t) => {
          if (m.isEmpty) (0 until r).map(i => m.put(i, i))
          t ++= (0 until r).map(i => () => m.get(i))
        }
      })({
        (r, m, t) =>
          t.clear()
      })

      benchmark(differentHashMapS, parallel = true)({
        () => (0 until cores).map(_ => new util.HashMap[Int, Int]())
      })({
        (r, m, t) => {
          if (m.exists(_.isEmpty)) {
            (0 until cores).map { t =>
              m(t).clear()
              (0 + t until r by cores).map(i => m(t).put(i, i))
            }
          }

          t ++= (0 until cores).flatMap { t =>
            (0 + t until r by cores).map(i => () => m(t).get(i))
          }
        }
      })({
        (r, m, t) =>
          t.clear()
      })
    }
  }
}
