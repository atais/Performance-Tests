package pl.msiatkowski

import java.util

import org.scalameter.api._

/**
  * Created by msiatkowski on 21.07.16.
  */
object HashSetArrayListBenchmark extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 500,
    exec.jvmflags -> List("-Xms2g", "-Xmx8g")
  )

  val size = 10000

  implicit def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, 3 * size)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  performance of "HashSet vs ArrayList" config opts in {
    val list = new util.ArrayList[Int]()
    val set = new util.HashSet[Int]()

    val listS: String = "ArrayList"
    val setS: String = "HashSet"

    measure method "add" in {
      testSingle(listS, i => list.add(i))
      testSingle(setS, i => set.add(i))
    }

    measure method "remove" in {
      testSingle(listS, i => list.remove(Integer.valueOf(i)))
      testSingle(setS, i => set.remove(i))
    }
  }

  private def testSingle[T](title: String, method: Int => T)(implicit gen: Gen[Int]) = {
    using(gen) curve title in { r =>
      (0 until r).map(i => method(i))
    }
  }

}
