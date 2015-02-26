package de.frosner.gsdmm

import breeze.stats.distributions.Multinomial

import scala.collection.mutable

import breeze.linalg._

object GSDMMRunner extends App {

  type DocumentId = Int
  type ClusterId = Int
  type Count = Int
  type Word = String

  val documents = Array(
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c"),
    List("a", "b", "b"),
    List("a", "a", "a"),
    List("b", "a", "b"),
    List("a", "a"),
    List("c")
  )

  val numClusters = 3
  val numIterations = 5

  val gsdmm = Gsdmm(documents, numClusters = 3, numIterations = 5)

}
