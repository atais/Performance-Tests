package pl.msiatkowski

import java.util

import org.apache.commons.lang3.RandomStringUtils
import org.scalameter.api._

/**
  * Created by msiatkowski on 21.07.16.
  */
object HashSetArrayListMemBenchmark extends Bench.ForkedTime {

  override def measurer = new Executor.Measurer.MemoryFootprint

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 100,
    exec.jvmflags -> List("-Xms2g", "-Xmx8g")
  )

  val size = 10000

  def sizes: Gen[Int] = Gen.range("size")(size, 10 * size, 3 * size)

  performance of "HashSet vs ArrayList" config opts in {

    val listS: String = "ArrayList"
    val setS: String = "HashSet"

    measure method "Int" in {
      using(sizes) curve listS in { i =>
        val c = new util.ArrayList[Int]()
        (0 until i).map(t => c.add(t))
        c
      }

      using(sizes) curve setS in { i =>
        val c = new util.HashSet[Int]()
        (0 until i).map(t => c.add(t))
        c
      }
    }

    measure method "String 10 chars" in {
      using(sizes) curve listS in { i =>
        val c = new util.ArrayList[String]()
        (0 until i).map(_ => c.add(RandomStringUtils.random(10)))
        c
      }

      using(sizes) curve setS in { i =>
        val c = new util.HashSet[String]()
        (0 until i).map(_ => c.add(RandomStringUtils.random(10)))
        c
      }
    }

    measure method "String 50 chars" in {
      using(sizes) curve listS in { i =>
        val c = new util.ArrayList[String]()
        (0 until i).map(_ => c.add(RandomStringUtils.random(50)))
        c
      }

      using(sizes) curve setS in { i =>
        val c = new util.HashSet[String]()
        (0 until i).map(_ => c.add(RandomStringUtils.random(50)))
        c
      }
    }
  }
}
