package pl.msiatkowski.map

import java.util
import java.util.concurrent.ConcurrentHashMap

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapRemoveBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {

    measure method "remove" in {
      using(for {r <- sizes} yield {
        val m = new util.HashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.remove(i))
        (r, m, f)
      }) curve hashMapS setUp {
        case (r, m, _) => (0 until r).foreach(i => m.put(i, i))
      } in {
        case (_, _, f) => f.map(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.remove(i))
        (r, m, f)
      }) curve concurrentHashMapS setUp {
        case (r, m, _) => (0 until r).foreach(i => m.put(i, i))
      } in {
        case (_, _, f) => f.map(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new ConcurrentHashMap[Int, Int]()
        val f = (0 until r).map(i => () => m.remove(i)).par
        (r, m, f)
      }) curve sharedHashMapS setUp {
        case (r, m, _) => (0 until r).foreach(i => m.put(i, i))
      } in {
        case (_, _, f) => f.map(_ ())
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
            (0 + t until r by cores).map(i => () => m(t).put(i, i)))
      } in {
        case (_, _, f) => f.map(_.map(_ ()))
      }
    }
  }
}
