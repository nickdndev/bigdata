package com.made
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.apache.hadoop.util.GenericOptionsParser;

object JobRunner {
  def main(args: Array[String]): Int = {
    val conf      = new Configuration()
    val otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs
    if (otherArgs.length != 2) {
      println("Usage: Count price Airbnb <in> <out>")
      return 2
    }

    val job       = Job.getInstance(conf, "Count price Airbnb")

    job.setJarByClass(classOf[PriceMapper])
    job.setMapperClass(classOf[PriceMapper])
    job.setCombinerClass(classOf[PriceReducer])
    job.setReducerClass(classOf[PriceReducer])

    job.setOutputKeyClass(classOf[Text])
    job.setOutputValueClass(classOf[PriceWritable])

    FileInputFormat.addInputPath(job, new Path(args(0)))
    FileOutputFormat.setOutputPath(job, new Path((args(1))))

    if (job.waitForCompletion(true)) 0 else 1
  }
}
