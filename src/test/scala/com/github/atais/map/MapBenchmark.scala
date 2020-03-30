package com.github.atais.map

import org.scalameter.Reporter.Composite
import org.scalameter.api._

/**
  * Created by msiatkowski on 21.07.16.
  */
class MapBenchmark extends Bench.ForkedTime {

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.OverlapIntervals()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def reporter: Reporter[Double] = Composite(
    LoggingReporter(),
    RegressionReporter(tester, historian)
  )

  val opts = Context(
    exec.benchRuns -> 1000
  )

  val size = 1000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, size)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  val hashMapS: String = "HashMap"
  val concurrentHashMapS: String = "ConcurrentHashMap"
  val differentHashMapS: String = "Different HashMaps"
  val sharedHashMapS: String = "Shared ConcurrentHashMap"

}
