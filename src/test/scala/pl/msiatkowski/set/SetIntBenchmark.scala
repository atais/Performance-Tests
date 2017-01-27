package pl.msiatkowski.set

import java.util

import org.scalameter.api._

import scala.util.Random

/**
  * Created by msiatkowski on 27.01.17.
  */
object SetIntBenchmark extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 100
  )

  val size = 100000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, size)

  performance of "Different Sets with Ints" config opts in {

    measure method "contains existing" in {

      using(for {r <- sizes} yield {
        val m = new util.HashSet[Int]()
        (0 until r).foreach(i => m.add(i))
        val f = (0 until r).map(i => () => m.contains(i))
        (r, m, Random.shuffle(f))
      }) curve "HashSet[Int]" in {
        case (r, m, f) => f.foreach(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new util.TreeSet[Int]()
        (0 until r).foreach(i => m.add(i))
        val f = (0 until r).map(i => () => m.contains(i))
        (r, m, Random.shuffle(f))
      }) curve "TreeSet[Int]" in {
        case (r, m, f) => f.foreach(_ ())
      }
    }

    measure method "contains not existing" in {

      using(for {r <- sizes} yield {
        val m = new util.HashSet[Int]()
        (0 until r).foreach(i => m.add(i))
        val f = (0 until r).map(_ => Random.nextInt(r) + r).map(i => () => m.contains(i))
        (r, m, f)
      }) curve "HashSet[Int]" in {
        case (r, m, f) => f.foreach(_ ())
      }

      using(for {r <- sizes} yield {
        val m = new util.TreeSet[Int]()
        (0 until r).foreach(i => m.add(i))
        val f = (0 until r).map(_ => Random.nextInt(r) + r).map(i => () => m.contains(i))
        (r, m, f)
      }) curve "TreeSet[Int]" in {
        case (r, m, f) => f.foreach(_ ())
      }
    }
  }
}
