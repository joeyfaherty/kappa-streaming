package atomicscala

import org.scalatest.FunSuite

/**
  * Atomic scala book exercises
  */
// TODO: Use Flatspec also for 10 is 20 should fail
class ForLoopsTest extends FunSuite {

  test("inclusive/exclusive range") {
    val r = Range(0, 10)

    for (i <- r) {
      println(i)
      // prints 0 -> 9
    }

    for (i <- r.inclusive) {
      println(i)
      // prints 0 -> 10
    }
  }

  test("for loop sum") {
    val r = Range(0, 10)

    var variable = 0
    for (i <- r.inclusive) {
      variable += i
    }
    println(variable)
  }

  test("sum even numbers") {
    val r = Range(0, 10)

    var variable = 0
    for (i <- r.inclusive) {
      if (i % 2 == 0) {
        variable += i
      }
    }
    println(variable)
  }

  test("sum odd & even numbers") {
    val r = Range(0, 10)

    var evenResult = 0
    var oddResult = 0
    for (i <- r.inclusive) {
      if (i % 2 == 0) {
        evenResult += i
      } else {
        oddResult += i
      }
    }
    println("evenResult = " + evenResult)
    println("oddResult = " + oddResult)
  }

  test("use to instead. to is inclusive") {
    var evenResult = 0
    var oddResult = 0
    for (i <- 0 to 10) {
      if (i % 2 == 0) {
        evenResult += i
      } else {
        oddResult += i
      }
    }
    println("evenResult = " + evenResult)
    println("oddResult = " + oddResult)
  }

  test("use until instead. until is exclusive") {
    var evenResult = 0
    var oddResult = 0
    for (i <- 0 until 11) {
      if (i % 2 == 0) {
        evenResult += i
      } else {
        oddResult += i
      }
    }
    println("evenResult = " + evenResult)
    println("oddResult = " + oddResult)
  }

}
