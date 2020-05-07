package com.github.atais

import java.util
import java.util.Comparator

import org.scalameter.Reporter.Composite
import org.scalameter.api._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object SortingBenchmark extends Bench.ForkedTime {

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.OverlapIntervals()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def reporter: Reporter[Double] = Composite(
    LoggingReporter(),
    RegressionReporter(tester, historian)
  )

  val opts = Context(
    exec.benchRuns -> 50000
  )

  val topEls = 5
  val size = 1000
  val java = Gen.range("size")(size, 10 * size, 3 * size)
    .map(i => (0 until i).foldLeft(new util.ArrayList[Int]()) { (acc, el) => acc.add(el); acc })
  val scala = Gen.range("size")(size, 10 * size, 3 * size)
    .map(i => (0 until i).foldLeft(new mutable.ArrayBuffer[Int]()) { (acc, el) => acc += (el) })

  val c = new Comparator[Int] {
    override def compare(o1: Int, o2: Int): Int = o1.compareTo(o2)
  }

  performance of "different approaches" config opts in {
    measure method "ArrayList" in {
      using(java) in { r =>
        r.sort(c)
        val top = new ArrayBuffer[Int](topEls)
        (0 until topEls).foreach(i => top += r.get(i))
        top
      }
    }

    measure method "ArrayBuffer" in {
      using(scala) in { r =>
        r.sorted.take(topEls)
      }
    }
  }

}
