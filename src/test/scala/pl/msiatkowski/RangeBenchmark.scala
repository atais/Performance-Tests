package pl.msiatkowski

import org.scalameter.Reporter.Composite
import org.scalameter.api._

object RangeBenchmark extends Bench.ForkedTime {

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.OverlapIntervals()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def reporter: Reporter[Double] = Composite(
    LoggingReporter(),
    RegressionReporter(tester, historian)
  )

  val size = 100000
  val sizes = Gen.range("size")(size, 10 * size, size)

  val cores = Runtime.getRuntime.availableProcessors()

  performance of "Range" in {
    measure method "map" in {
      using(sizes) curve "1 thread" in { r =>
        (0 until r).map(_ + 1)
      }

      using(sizes) curve cores + " threads" in { r =>
        (0 until cores).par.map { t =>
          (0 + t until r by cores).map(_ + 1)
        }
      }
    }
  }
}
