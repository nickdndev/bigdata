package com.vk.made.tools.data

import java.io.{File, PrintWriter}

import breeze.linalg.{Axis, DenseMatrix, DenseVector, csvread}

object DataTool {

  def readCsv(filePath: String, targetColumn: Int, headerRow: Int = 1): (DenseMatrix[Double], DenseVector[Double]) = {
    val data: DenseMatrix[Double] = csvread(new File(filePath), skipLines = headerRow)
    val X                         = data.delete(targetColumn, Axis._1)
    val Y                         = data(::, targetColumn)
    return (X, Y)
  }

  def writeCsv(path: String, data: DenseVector[Double]): Unit = {
    val writer = new PrintWriter(new File(path))
    data.foreach(d => writer.println(d))
    writer.close()
  }
}
