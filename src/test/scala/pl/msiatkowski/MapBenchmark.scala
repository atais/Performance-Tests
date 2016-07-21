package pl.msiatkowski

import java.util
import java.util.concurrent.{Callable, ConcurrentHashMap, Executors}

import org.scalameter.api._

/**
  * Created by msiatkowski on 21.07.16.
  */
object MapBenchmark extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 500,
    exec.jvmflags -> List("-Xms1G", "-Xmx8G", "-d64")
  )

  implicit def sizes: Gen[Int] = Gen.range("size")(100000, 1000000, 300000)

  implicit def cores: Int = Runtime.getRuntime.availableProcessors()

  performance of "Different Map implementations" config opts in {
    val singleMap = new util.HashMap[Int, Int]()
    val concurrentMap = new ConcurrentHashMap[Int, Int]()
    val differentMaps = (0 until cores).map(_ => new util.HashMap[Int, Int]())
    val sharedMap = new ConcurrentHashMap[Int, Int]()

    val hashMapS: String = "HashMap"
    val concurrentHashMapS: String = "ConcurrentHashMap"
    val differentHashMapS: String = "Different HashMaps"
    val sharedHashMapS: String = "Shared ConcurrentHashMap"

    measure method "add" in {
      testSingle(hashMapS, i => singleMap.put(i, i))
      testSingle(concurrentHashMapS, i => concurrentMap.put(i, i))

      testConcurrent(differentHashMapS, (t, i) => differentMaps(t).put(i, i))
      testConcurrent(sharedHashMapS, (_, i) => sharedMap.put(i, i))
    }

    measure method "get" in {
      testSingle(hashMapS, i => singleMap.get(i))
      testSingle(concurrentHashMapS, i => concurrentMap.get(i))

      testConcurrent(differentHashMapS, (t, i) => differentMaps(t).get(i))
      testConcurrent(sharedHashMapS, (_, i) => sharedMap.get(i))
    }

    measure method "remove" in {
      testSingle(hashMapS, i => singleMap.remove(i))
      testSingle(concurrentHashMapS, i => concurrentMap.remove(i))

      testConcurrent(differentHashMapS, (t, i) => differentMaps(t).remove(i))
      testConcurrent(sharedHashMapS, (_, i) => sharedMap.remove(i))
    }
  }

  private def testSingle(title: String, method: Int => Int)(implicit gen: Gen[Int]) = {
    using(gen) curve title in { r =>
      (0 until r).map(i => method(i))
    }
  }

  private def testConcurrent(title: String, method: (Int, Int) => Int)(implicit gen: Gen[Int], cores: Int) = {
    using(gen) curve title in { r =>
      val pool = Executors.newFixedThreadPool(cores)
      val tasks = (0 until cores).map { t =>
        new Callable[Unit] {
          override def call() = (0 + t until r by cores).map(i => method(t, i))
        }
      }
      import scala.collection.JavaConverters._
      pool.invokeAll(tasks.asJava)
      pool.shutdown()
    }
  }

}
