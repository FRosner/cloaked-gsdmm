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
  val alpha = 1d
  val beta = 1d
  val mz = mutable.HashMap.empty[ClusterId, Count]
  val nz = mutable.HashMap.empty[ClusterId, Count]
  val nzw = mutable.HashMap.empty[(ClusterId, Word), Count]
  val zd = mutable.HashMap.empty[DocumentId, ClusterId]
  val ndw = mutable.HashMap.empty[(DocumentId, Word), Count]

  val uniqueWordTypes = documents.flatten.distinct
  val v = uniqueWordTypes.size
  for (clusterId <- 0 to numClusters - 1) {
    mz(clusterId) = 0
    nz(clusterId) = 0
    uniqueWordTypes.foreach(wordType => {
      nzw((clusterId, wordType)) = 0
    })
  }

  for (wordType <- uniqueWordTypes; documentId <- 0 to documents.size - 1) {
    ndw((documentId, wordType)) = 0
  }

  val mult = Multinomial(DenseVector(List.fill(numClusters)(1d/numClusters).toArray))
  for (documentId <- 0 to documents.size - 1) {
    val sampledCluster = mult.draw()
    val currentDocument = documents(documentId)
    zd(documentId) = sampledCluster
    mz(sampledCluster) += 1
    nz(sampledCluster) += currentDocument.size
    for (word <- currentDocument) {
      nzw(sampledCluster, word) += 1
      ndw((documentId, word)) += 1
    }
  }

  def sampleNewCluster(mz_d: mutable.HashMap[ClusterId, Count],
                       nz_d: mutable.HashMap[ClusterId, Count],
                       nzw_d: mutable.HashMap[(ClusterId, Word), Count],
                       documentId: Int): ClusterId = {
    val pz = new Array[Double](numClusters)
    val currentDocument = documents(documentId)
    for (clusterId <- 0 to numClusters - 1) {
      pz(clusterId) = ((mz_d(clusterId) + alpha) / (documents.length - 1 + numClusters * alpha)) *
        ((for (word <- currentDocument; j <- 1 to ndw((documentId, word))) yield nzw_d((clusterId, word)) + beta + j - 1).reduce(_ * _) /
          ((1 to currentDocument.size).map(i => nz_d(clusterId) + v * beta + i - 1).reduce(_ * _)))
    }
    println(s"""  Document $documentId: ${pz.mkString(" ")}""")
    val pzSum = pz.sum
    val normalizedPz = pz.map(_ / pzSum)
    val mult = Multinomial(DenseVector(normalizedPz.toArray))
    mult.draw()
  }

  for (iteration <- 1 to numIterations) {
    for (documentId <- 0 to documents.size - 1) {
      val currentCluster = zd(documentId)
      val currentDocument = documents(documentId)
      mz(currentCluster) -= 1
      nz(currentCluster) -= currentDocument.size
      for (word <- currentDocument) {
        nzw((currentCluster, word)) -= 1
      }
      val newCluster = sampleNewCluster(nz_d = nz, mz_d = mz, nzw_d = nzw, documentId = documentId)
      zd(documentId) = newCluster
      mz(newCluster) += 1
      nz(newCluster) += currentDocument.size
      for (word <- currentDocument) {
        nzw((currentCluster, word)) += 1
      }
    }
    println(s"Iteration $iteration: $zd")
  }

}
