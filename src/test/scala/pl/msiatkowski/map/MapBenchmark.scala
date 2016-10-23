package pl.msiatkowski.map

import org.scalameter.api._
import org.scalameter.picklers.noPickler._

import scala.collection.mutable.ArrayBuffer

/**
  * Created by msiatkowski on 21.07.16.
  */
class MapBenchmark extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 1000
  )

  val size = 100000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, 3 * size)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  val hashMapS: String = "HashMap"
  val concurrentHashMapS: String = "ConcurrentHashMap"
  val differentHashMapS: String = "Different HashMaps"
  val sharedHashMapS: String = "Shared ConcurrentHashMap"

  type TaskList = ArrayBuffer[() => Int]

  def benchmark[T](title: String, parallel: Boolean)
                  (map: () => T)
                  (setUp: (Int, T, TaskList) => Any)
                  (tearDown: (Int, T, TaskList) => Any) = {
    using(Gen.crossProduct(
      sizes,
      Gen.single("map")(map()),
      Gen.single("array")(new TaskList())
    )) curve title setUp {
      case (r, m, t) => setUp(r, m, t)
    } tearDown {
      case (r, m, t) => tearDown(r, m, t)
    } in {
      case (r, m, t) =>
        val tp = parallel match {
          case true => t.par
          case false => t
        }
        tp.map(_.apply())
        m
    }
  }

}
