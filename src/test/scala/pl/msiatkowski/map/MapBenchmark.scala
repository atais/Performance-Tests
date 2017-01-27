package pl.msiatkowski.map

import org.scalameter.api._

/**
  * Created by msiatkowski on 21.07.16.
  */
class MapBenchmark extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 1000
  )

  val size = 100000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, size)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  val hashMapS: String = "HashMap"
  val concurrentHashMapS: String = "ConcurrentHashMap"
  val differentHashMapS: String = "Different HashMaps"
  val sharedHashMapS: String = "Shared ConcurrentHashMap"

}
