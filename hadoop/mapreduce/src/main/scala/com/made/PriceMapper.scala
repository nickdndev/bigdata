package com.made

import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Mapper

class PriceMapper extends Mapper[Object, Text, Text, PriceWritable] {

  val priceKey              = new Text("price_airbnb")
  val PRICE_INDEX_FROM_TAIL = 7

  override def map(key: Object, value: Text, context: Mapper[Object, Text, Text, PriceWritable]#Context): Unit = {

    val items: Array[String] = value
      .toString
      .split(",")

    items.length match {
      case len if len > PRICE_INDEX_FROM_TAIL =>
        items(len - PRICE_INDEX_FROM_TAIL)
          .toDoubleOption
          .foreach { price =>
            context.write(priceKey, new PriceWritable(1, price, 0))
          }
      case _                                  =>
    }
  }
}
