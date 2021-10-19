package com.vk.made.tools.instances

import java.io.{File, PrintWriter}

import breeze.linalg.{Axis, DenseMatrix, DenseVector, csvread}

private[tools] trait CsvInstance {
  implicit class DenseVectorCsvExt(val dv: DenseVector[Double]) {

    def toFile(path: String): Unit = {
      val writer = new PrintWriter(new File(path))
      dv.foreach(d => writer.println(d))
      writer.close()
    }
  }

  implicit class MatrixCsvExt(val path: String) {

    def fromFile(targetColumn: Int, headerRow: Int = 1): (DenseMatrix[Double], DenseVector[Double]) = {
      val data: DenseMatrix[Double] = csvread(new File(path), skipLines = headerRow)
      val X                         = data.delete(targetColumn, Axis._1)
      val Y                         = data(::, targetColumn)
      (X, Y)
    }
  }
}
