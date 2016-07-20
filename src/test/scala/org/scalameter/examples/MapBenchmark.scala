package org.scalameter.examples

import java.util
import java.util.concurrent.{Callable, ConcurrentHashMap, Executors}

import org.scalameter.api._

import scala.collection.JavaConverters._

object MapBenchmark extends Bench.ForkedTime {

  override val reporter = ChartReporter[Double](ChartFactory.XYLine())

  val opts = Context(
    exec.benchRuns -> 500,
    exec.jvmflags -> List("-Xms1G", "-Xmx12G", "-d64")
  )

  val sizes = Gen.range("size")(1000000, 10000000, 3000000)
  val cores = Runtime.getRuntime.availableProcessors()

  performance of "Different Map implementations" config opts in {
    val singleMap = new util.HashMap[Int, Int]()
    val concurrentMap = new ConcurrentHashMap[Int, Int]()
    val differentMaps = (0 until cores).map(_ => new util.HashMap[Int, Int]())
    val sharedMap = new ConcurrentHashMap[Int, Int]()

    measure method "add" in {
      testSingle(sizes, "HashMap", i => singleMap.put(i, i))
      testSingle(sizes, "ConcurrentHashMap", i => concurrentMap.put(i, i))

      testConcurrent(sizes, "Different HashMaps", cores, (t, i) => differentMaps(t).put(i, i))
      testConcurrent(sizes, "Shared ConcurrentHashMap", cores, (_, i) => sharedMap.put(i, i))
    }

    measure method "get" in {
      testSingle(sizes, "HashMap", i => singleMap.get(i))
      testSingle(sizes, "ConcurrentHashMap", i => concurrentMap.get(i))

      testConcurrent(sizes, "Different HashMaps", cores, (t, i) => differentMaps(t).get(i))
      testConcurrent(sizes, "Shared ConcurrentHashMap", cores, (_, i) => sharedMap.get(i))
    }

    measure method "remove" in {
      testSingle(sizes, "HashMap", i => singleMap.remove(i))
      testSingle(sizes, "ConcurrentHashMap", i => concurrentMap.remove(i))

      testConcurrent(sizes, "Different HashMaps", cores, (t, i) => differentMaps(t).remove(i))
      testConcurrent(sizes, "Shared ConcurrentHashMap", cores, (_, i) => sharedMap.remove(i))
    }
  }

  private def testSingle(gen: Gen[Int], title: String, method: Int => Int) = {
    using(gen) curve title in { r =>
      (0 until r).map(i => method(i))
    }
  }

  private def testConcurrent(gen: Gen[Int], title: String, cores: Int, method: (Int, Int) => Int) = {
    using(gen) curve title in { r =>
      val pool = Executors.newFixedThreadPool(cores)
      val tasks = (0 until cores).map { t =>
        new Callable[Unit] {
          override def call() = (0 + t until r by cores).map(i => method(t, i))
        }
      }
      pool.invokeAll(tasks.asJava)
      pool.shutdown()
    }
  }

}
