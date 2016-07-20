package org.scalameter.examples

import java.util.concurrent.{Callable, Executors}

import org.scalameter.api._

object RangeBenchmark extends Bench.ForkedTime {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  val cores = Runtime.getRuntime.availableProcessors()
  val pool = Executors.newFixedThreadPool(cores)

  performance of "Range" in {
    measure method "map" in {
      using(ranges) in { r =>
        r.map(_ + 1)
      }
    }

    measure method "parallel map" in {
      using(ranges) in { r =>
        val tasks = (0 to cores).map { _ =>
          new Callable[Unit] {
            override def call() = (0 to r.last / cores).map(_ + 1)
          }
        }
        import collection.JavaConverters._
        pool.invokeAll(tasks.asJava)
      }
    }
  }
}
