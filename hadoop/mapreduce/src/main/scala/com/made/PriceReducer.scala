package com.made

import java.lang
import java.lang.Math.pow

import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Reducer

import scala.jdk.CollectionConverters._

class PriceReducer extends Reducer[Text, PriceWritable, Text, PriceWritable] {

  override def reduce(key: Text, values: lang.Iterable[PriceWritable], context: Reducer[Text, PriceWritable, Text, PriceWritable]#Context): Unit = {
    val (count, mean, variance) = values
      .asScala
      .foldLeft((0, 0.0, 0.0)) {
        case ((prevCount, prevMean, prevVariance), price) =>
          val newCount    = price.countWritable.get()
          val newMean     = price.meanWritable.get()
          val newVariance = price.varianceWritable.get()

          val countUpdated = prevCount + newCount

          val varianceUpdated = (newVariance * newCount + prevCount * prevVariance) / (prevCount + newCount) +
            newCount * prevCount * pow((prevMean - newMean) / (prevCount + newCount), 2)

          val meanUpdated = (newMean * newCount + prevMean * prevCount) / (prevCount + newCount)

          (countUpdated, meanUpdated, varianceUpdated)
      }

    context.write(key, new PriceWritable(count, mean, variance))
  }

}
