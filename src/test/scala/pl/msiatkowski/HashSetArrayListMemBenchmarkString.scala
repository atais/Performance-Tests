package pl.msiatkowski

import java.util

import org.apache.commons.lang3.RandomStringUtils
import org.scalameter.api._

/**
  * Created by msiatkowski on 21.07.16.
  */
object HashSetArrayListMemBenchmarkString extends Bench.LocalTime {

  override def measurer = new Executor.Measurer.MemoryFootprint

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 10,
    exec.jvmflags -> List("-Xms2g", "-Xmx8g")
  )

  val size = 10000

  implicit def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, 3 * size)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  performance of "HashSet vs ArrayList" config opts in {
    val list = new util.ArrayList[String]()
    val set = new util.HashSet[String]()

    val listS: String = "ArrayList"
    val setS: String = "HashSet"

    measure method "size" in {
      testSingle(listS, i => list.add(RandomStringUtils.randomAlphabetic(10)))
      testSingle(setS, i => set.add(RandomStringUtils.randomAlphabetic(10)))
    }
  }

  private def testSingle[T](title: String, method: Int => T)(implicit gen: Gen[Int]) = {
    using(gen) curve title in { r =>
      (0 until r).map(i => method(i))
    }
  }

}
