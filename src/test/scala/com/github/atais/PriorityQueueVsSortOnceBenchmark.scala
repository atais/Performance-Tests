package com.github.atais

import org.apache.commons.lang3.RandomStringUtils
import org.scalameter.Reporter.Composite
import org.scalameter.api._

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object PriorityQueueVsSortOnceBenchmark extends Bench.ForkedTime {

  def tester: RegressionReporter.Tester =
    RegressionReporter.Tester.OverlapIntervals()

  def historian: RegressionReporter.Historian =
    RegressionReporter.Historian.ExponentialBackoff()

  override def reporter: Reporter[Double] = Composite(
    LoggingReporter(),
    RegressionReporter(tester, historian)
  )

  val opts = Context(
    exec.benchRuns -> 10000
  )

  val topElems = 5
  val size = 2
  val elems = Gen.range("size")(size, 10 * size, 2 * size)
    .map(i => (0 until i).map(v => Container(RandomStringUtils.randomAlphabetic(5), i)))

  performance of "different approaches" config opts in {
    measure method "queue" in {
      using(elems) in { r =>
        val q = new FixedSizePriorityQueue[Container](topElems)
        r.foreach(e => q += e)
        q.dequeueAll
      }
    }

    measure method "array buffer" in {
      using(elems) in { r =>
        val ab = new ArrayBuffer[Container]()
        r.foreach(e => ab += e)
        if (ab.length <= topElems) {
          ab
        } else {
          ab.sorted.take(topElems)
        }
      }
    }

  }

  implicit val ContainerOrdering: Ordering[Container] =
    Ordering.by((e: Container) => e.b).reverse

  case class Container(s: String, b: Int)

  class FixedSizePriorityQueue[T](maxSize: Int)(implicit ordering: Ordering[T]) extends mutable.Traversable[T] {
    val queue = new mutable.PriorityQueue[T]()
    queue.sizeHint(maxSize + 1)

    def +=(elem: T): Any = {
      queue.enqueue(elem)
      if (queue.size > maxSize) {
        queue.dequeue()
      }
    }

    def foreach[U](f: T => U): Unit = queue.foreach(f)

    def dequeueAll: Seq[T] = queue.dequeueAll

  }

}
