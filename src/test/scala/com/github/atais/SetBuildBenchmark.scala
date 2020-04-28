package com.github.atais

import org.scalameter.Reporter.Composite
import org.scalameter.api._

import scala.collection.mutable

object SetBuildBenchmark extends Bench.ForkedTime {

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.OverlapIntervals()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def reporter: Reporter[Double] = Composite(
    LoggingReporter(),
    RegressionReporter(tester, historian)
  )

  val opts = Context(
    exec.benchRuns -> 500,
  )

  val size = 100
  val sizes = Gen.range("size")(size, 10 * size, size)

  performance of "building set" config opts in {
    measure method "immutable" in {
      using(sizes) in { r =>
        (0 until r).toSet
      }
    }

    measure method "mutable" in {
      using(sizes) in { r =>
        val set = new mutable.HashSet[Int]()
        (0 until r).foreach(set.add)
        set
      }
    }

    measure method "mutable fold" in {
      using(sizes) in { r =>
        (0 until r).foldLeft(mutable.Set.empty[Int]) { (s, e) => s += e }
      }
    }

    measure method "java set" in {
      using(sizes) in { r =>
        val set = new java.util.HashSet[Int]()
        (0 until r).foreach(set.add)
        set
      }
    }
  }

}
