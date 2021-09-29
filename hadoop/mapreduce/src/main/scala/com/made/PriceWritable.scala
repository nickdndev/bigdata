package com.made

import java.io.{DataInput, DataOutput}

import org.apache.hadoop.io.{DoubleWritable, IntWritable, Writable}

class PriceWritable(count: Int, mean: Double, variance: Double) extends Writable {
  def this() = {
    this(0, 0.0, 0.0)
  }

  val countWritable: IntWritable       = new IntWritable(count)
  val meanWritable: DoubleWritable     = new DoubleWritable(mean)
  val varianceWritable: DoubleWritable = new DoubleWritable(variance)

  override def write(dataOutput: DataOutput): Unit = {
    countWritable.write(dataOutput)
    meanWritable.write(dataOutput)
    varianceWritable.write(dataOutput)
  }

  override def readFields(dataInput: DataInput): Unit = {
    countWritable.readFields(dataInput)
    meanWritable.readFields(dataInput)
    varianceWritable.readFields(dataInput)
  }

  override def toString: String =
    s"Count: ${countWritable.get()}, Mean: ${meanWritable.get()}, Variance: ${varianceWritable.get()}"
}
