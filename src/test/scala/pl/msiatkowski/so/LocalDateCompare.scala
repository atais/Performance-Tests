package pl.msiatkowski.so

import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LocalDateCompare extends AnyFlatSpec with Matchers {

  "A simple" should "test" in {

    val dateFrom = "01 Jan, 2016"
    val dateTill = "03 Jan, 2016"

    val date1 = "01 Jan, 2016"
    val date2 = "02 Jan, 2016"
    val date3 = "03 Jan, 2016"

    assert(isDateBetweenRange(dateFrom, dateTill, date1))
    assert(isDateBetweenRange(dateFrom, dateTill, date2))
    assert(isDateBetweenRange(dateFrom, dateTill, date3))
  }

  implicit class SLocalDate(val time: LocalDate) {
    def isBeforeEq(other: ChronoLocalDate) = !time.isAfter(other)
    def isAfterEq(other: ChronoLocalDate) = !time.isBefore(other)
  }

  def getLocalDate(date: String): LocalDate = {
    LocalDate.parse(date, DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH))
  }

  def isDateBetweenRange(from: String, till: String, date: String): Boolean = {
    val fromDate = getLocalDate(from)
    val tillDate = getLocalDate(till)
    val myDate = getLocalDate(date)
    myDate.isBeforeEq(tillDate) && myDate.isAfterEq(fromDate)
  }

}