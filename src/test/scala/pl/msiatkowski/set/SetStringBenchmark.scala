package pl.msiatkowski.set

import java.util

import org.apache.commons.lang3.RandomStringUtils
import org.scalameter.Reporter.Composite
import org.scalameter.api._

import scala.util.Random

/**
  * Created by msiatkowski on 27.01.17.
  */
object SetStringBenchmark extends Bench.ForkedTime {

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.OverlapIntervals()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def reporter: Reporter[Double] = Composite(
    LoggingReporter(),
    RegressionReporter(tester, historian)
  )

  val opts = Context(
    exec.benchRuns -> 100
  )

  val size = 100000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, size)

  performance of "Different Sets with Strings" config opts in {

    measure method "contains existing" in {

      using(for {r <- sizes} yield {
        val m = new util.HashSet[String]()
        (0 until r).foreach(i => m.add(i.toString))
        val f = (0 until r).map(_.toString).map(i => () => m.contains(i))
        (r, m, Random.shuffle(f))
      }) curve "HashSet[String]" in {
        case (r, m, f) => f.foreach(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new util.TreeSet[String]()
        (0 until r).foreach(i => m.add(i.toString))
        val f = (0 until r).map(_.toString).map(i => () => m.contains(i))
        (r, m, Random.shuffle(f))
      }) curve "TreeSet[String]" in {
        case (r, m, f) => f.foreach(_ ())
      }
    }

    measure method "contains not existing" in {

      using(for {r <- sizes} yield {
        val m = new util.HashSet[String]()
        (0 until r).foreach(i => m.add(i.toString))
        val f = (0 until r).map(_ => RandomStringUtils.random(5)).map(i => () => m.contains(i))
        (r, m, f)
      }) curve "HashSet[String]" in {
        case (r, m, f) => f.foreach(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new util.TreeSet[String]()
        (0 until r).foreach(i => m.add(i.toString))
        val f = (0 until r).map(_ => RandomStringUtils.random(5)).map(i => () => m.contains(i))
        (r, m, f)
      }) curve "TreeSet[String]" in {
        case (r, m, f) => f.foreach(_ ())
      }
    }
  }
}
