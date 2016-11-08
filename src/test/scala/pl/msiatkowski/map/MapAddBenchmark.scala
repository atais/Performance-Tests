package pl.msiatkowski.map

import java.util
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapAddBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {
    measure method "add" in {
      using(for {r <- sizes} yield {
        val m = new util.HashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.put(i, i))
        (m, f)
      }) curve hashMapS tearDown {
        case (m, _) => m.clear()
      } in {
        case (_, f) => f.map(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.put(i, i))
        (m, f)
      }) curve concurrentHashMapS tearDown {
        case (m, _) => m.clear()
      } in {
        case (_, f) => f.map(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.put(i, i)).par
        (m, f)
      }) curve sharedHashMapS tearDown {
        case (m, _) => m.clear()
      } in {
        case (_, f) => f.map(_ ())
      }

      using(for {r <- sizes} yield {
        val t = (0 until cores).map(t => {
          val m = new util.HashMap[Int, Int]()
          val f = (0 + t until r by cores).map(i => () => m.put(i, i))
          (m, f)
        })
        (t.map(_._1), t.map(_._2).par)
      }) curve differentHashMapS tearDown {
        case (m, _) => m.foreach(_.clear())
      } in {
        case (_, f) => f.map(_.map(_ ()))
      }
    }
  }
}
