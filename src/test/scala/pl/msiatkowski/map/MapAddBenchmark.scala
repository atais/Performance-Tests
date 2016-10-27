package pl.msiatkowski.map

import java.util
import java.util.concurrent.ConcurrentHashMap

import org.scalameter.Gen
import org.scalameter.picklers.noPickler._

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapAddBenchmark extends MapBenchmark {

  performance of "Different Map implementations" config opts in {
    measure method "add" in {
      using(Gen.crossProduct(
        Gen.single("map")(new util.HashMap[Int, Int]()),
        for {r <- sizes} yield (m: util.HashMap[Int, Int]) => (0 until r).map(i => m.put(i, i))
      )) curve hashMapS tearDown {
        case (m, t) => m.clear()
      } in {
        case (m, t) => t(m)
      }

      using(Gen.crossProduct(
        Gen.single("map")(new ConcurrentHashMap[Int, Int]()),
        for {r <- sizes} yield (m: ConcurrentHashMap[Int, Int]) => (0 until r).map(i => m.put(i, i))
      )) curve concurrentHashMapS tearDown {
        case (m, t) => m.clear()
      } in {
        case (m, t) => t(m)
      }

      using(Gen.crossProduct(
        Gen.single("map")(new ConcurrentHashMap[Int, Int]()),
        for {r <- sizes} yield (m: ConcurrentHashMap[Int, Int]) => (0 until r).map(i => m.put(i, i)).par
      )) curve sharedHashMapS tearDown {
        case (m, t) => m.clear()
      } in {
        case (m, t) => t(m)
      }

      using(Gen.crossProduct(
        Gen.single("map")((0 until cores).map(_ => new util.HashMap[Int, Int]())),
        for {r <- sizes} yield (m: Seq[util.HashMap[Int, Int]]) =>
          (0 until cores).flatMap(t => (0 + t until r by cores).map(i =>
            m(t).put(i, i))).par
      )) curve differentHashMapS tearDown {
        case (m, t) => m.foreach(_.clear())
      } in {
        case (m, t) =>
          t(m)
      }
    }
  }
}
