package pl.msiatkowski.so

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FutureCheck extends AnyFlatSpec with Matchers {

  "A simple" should "test" in {

    val map = new mutable.HashMap[Int, Int]()

    assert(map.isEmpty)

    lazy val lazyF = Future {
      map.put(1, 1)
    }

    assert(map.isEmpty)
    Thread.sleep(1000)
    assert(map.isEmpty)

    for {
      x <- lazyF
    } yield x match {
      case _ => assert(map.size == 1)
    }

    val nonLazyF = Future {
      map.put(2, 2)
    }

    Thread.sleep(1000)
    assert(map.size == 2)

    for {
      x <- nonLazyF
    } yield x match {
      case _ => assert(map.size == 2)
    }
  }

}