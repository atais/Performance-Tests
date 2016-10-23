package pl.msiatkowski.map

import java.util
import java.util.concurrent._

import org.scalameter.api._
import org.scalameter.picklers.noPickler._

import scala.collection.mutable.ArrayBuffer


/**
  * Created by msiatkowski on 21.07.16.
  */
object MapGetBenchmarkLV extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 500
  )

  val size = 100000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, 3 * size)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  val hashMapS: String = "HashMap"
  val concurrentHashMapS: String = "ConcurrentHashMap"
  val differentHashMapS: String = "Different HashMaps"
  val sharedHashMapS: String = "Shared ConcurrentHashMap"

  performance of "Different Map implementations" config opts in {
    measure method "add" in {

      using(Gen.crossProduct(sizes,
        Gen.single("")(new util.HashMap[Int, Int]()))
      ) curve hashMapS tearDown {
        case (r, map) => map.clear()
      } in { case (r, map) =>
        (0 until r).map(i => {
          map.put(i, i)
        })
      }

      using(Gen.crossProduct(sizes,
        Gen.single("")(new ConcurrentHashMap[Int, Int]()))
      ) curve concurrentHashMapS tearDown {
        case (r, map) => map.clear()
      } in { case (r, map) =>
        (0 until r).map(i => {
          map.put(i, i)
        })
      }

      using(Gen.crossProduct(
        sizes,
        Gen.single("map")(new ConcurrentHashMap[Int, Int]()),
        Gen.single("array")(new ArrayBuffer[() => Int]())
      )) curve sharedHashMapS setUp {
        case (r, map, tasks) =>
          val t = (0 until cores).flatMap { t =>
            (0 + t until r by cores).map(i => () => map.put(i, i))
          }
          tasks ++= t
      } tearDown {
        case (r, map, tasks) =>
          map.clear()
          tasks.clear()
      } in {
        case (r, map, tasks) =>
          tasks.par.map(_.apply)
      }

      using(Gen.crossProduct(
        sizes,
        Gen.single("map")((0 until cores).map(_ => new util.HashMap[Int, Int]())),
        Gen.single("array")(new ArrayBuffer[() => Int]())
      )) curve differentHashMapS setUp {
        case (r, maps, tasks) =>
          val t = (0 until cores).flatMap { t =>
            (0 + t until r by cores).map(i => () => maps(t).put(i, i))
          }
          tasks ++= t
      } tearDown {
        case (r, maps, tasks) =>
          maps.foreach(_.clear())
          tasks.clear()
      } in {
        case (r, map, tasks) =>
          tasks.par.map(_.apply)
      }

    }
  }
}
