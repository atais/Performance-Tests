package pl.msiatkowski

import java.util

import org.scalameter.Gen.Collections
import org.scalameter.api._


/**
  * Created by msiatkowski on 21.07.16.
  */
object MapBenchmarkError extends Bench.ForkedTime with Collections {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 100
  )

  val size = 100000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, 3 * size)

  def emptyMap: Gen[util.HashMap[Int, Int]] = Gen.single(hashMapS)(new util.HashMap[Int, Int]())

  def empty = Gen.crossProduct(sizes, emptyMap)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  val hashMapS: String = "HashMap"

  performance of "Different Map implementations" config opts in {
    measure method "add" in {
      using(empty) curve hashMapS in { case (r, map) =>
        (0 until r).map(i => {
          val r = map.put(i, i)
          r
        })
      }
    }
  }
}
