package org.scalameter.examples

import java.util.concurrent.{Callable, Executors, TimeUnit}

import org.scalameter.api._
import collection.JavaConverters._

object RangeBenchmark extends Bench.LocalTime {
  val sizes = Gen.range("size")(300000, 1500000, 300000)

  val ranges = for {
    size <- sizes
  } yield 0 until size

  val cores = Runtime.getRuntime.availableProcessors()

  performance of "Range" in {
    measure method "map" in {
      using(ranges) in { r =>
        r.map(_ + 1)
      }
    }

    measure method "parallel map" in {
      using(ranges) in { r =>
        val pool = Executors.newFixedThreadPool(cores)
        val tasks = (0 until cores).map { t =>
          new Callable[Unit] {
            override def call() = (0 + t until r.last by cores).map(_ + 1)
          }
        }
        pool.invokeAll(tasks.asJava, 1, TimeUnit.MINUTES)
        pool.shutdown()
      }
    }
  }
}
