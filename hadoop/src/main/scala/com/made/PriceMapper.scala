package com.made

import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper

class PriceMapper extends Mapper[Object, Text, Text, PriceWritable] {

  val priceKey = new Text()

  override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, PriceWritable]#Context): Unit =
    value
      .toString
      .split(",")
      .last
      .toDoubleOption
      .foreach { price =>
        context.write(priceKey, new PriceWritable(1, price, 0))
      }
}
