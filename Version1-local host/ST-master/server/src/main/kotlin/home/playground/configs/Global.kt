package home.playground.configs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import home.playground.models.Tweet
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.Duration
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.twitter.TwitterUtils
import twitter4j.TwitterFactory
import twitter4j.auth.Authorization
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import java.sql.Timestamp

/**
 * Created by cuong on 11/15/15.
 */
object Global
{
    private var twitterAuth: Authorization? = null

    private var sqlContext: SQLContext? = null
    private var javaSparkContext: JavaSparkContext? = null
    private var streamingContext: JavaStreamingContext? = null

    private var tweetRdd: JavaRDD<Tweet>? = null

    val gson: Gson = GsonBuilder().create()

    fun configureTweetRdd(): JavaRDD<Tweet>
    {
        if (tweetRdd == null) {
            val sc = JavaSparkContext(createSparkConf())

            val rdd = sc.textFile("/Users/cuong/Downloads/spark-1.5.1/examples/tweet_bank/*")

            tweetRdd = rdd.map { gson.fromJson(it, Tweet::class.java) }
            tweetRdd!!.cache()
            tweetRdd!!.count()
        }

        return tweetRdd as JavaRDD<Tweet>
    }

    fun configureSqlContext(): SQLContext {
        if (sqlContext == null) {
            configureSparkContext()

            sqlContext = SQLContext(streamingContext?.sparkContext());

            val tweetRDD = javaSparkContext!!.textFile("/Users/cuong/Downloads/spark-1.5.1/examples/tweet_bank/*")
                    .map { Global.gson.fromJson(it, Tweet::class.java) }

            val dfTweet = sqlContext!!.createDataFrame(tweetRDD, Tweet::class.java)
            dfTweet.registerTempTable("tweets")
            sqlContext!!.cacheTable("tweets")
        }

        return sqlContext!!
    }

    fun configureSparkContext(): JavaSparkContext {
        Logger.getLogger("org").level = Level.OFF
        Logger.getLogger("akka").level = Level.OFF
        Logger.getLogger("twitter4j").level = Level.OFF

        if (javaSparkContext == null) {
            val conf = createSparkConf()

            streamingContext = JavaStreamingContext(conf, Duration(1000))
            Thread{ collectTweet(streamingContext!!, configureTwitterAuth()) }.start()

            javaSparkContext = streamingContext!!.sparkContext()
        }

        return javaSparkContext!!
    }

    fun createSparkConf(): SparkConf {
        val conf = SparkConf().setMaster("local[*]")
                .setAppName("SparkSQL")
                .set("spark.executor.memory", "1g")
                .set("spark.default.parallelism", "8")
                .set("spark.driver.allowMultipleContexts", "true")

        return conf
    }

    fun configureTwitterAuth(): Authorization
    {
        if (twitterAuth == null) {
            val authConfig = ConfigurationBuilder()
                    .setOAuthConsumerKey("QKoRQCFQ8EoJUAugCZ5ZDCFZI")
                    .setOAuthConsumerSecret("cIhK0hnyX4eZb8JakQUSYzueYJRhyyE8v7Yfitg5VhLDcm7rrT")
                    .setOAuthAccessToken("2732661193-kcBEZEZr1JD8R5FUoTn69QqVXSy3xBxovehKSQk")
                    .setOAuthAccessTokenSecret("4E6gRkzJugA8x8o2IWLKFGDYmO5reEWRXPgDi5UwEmWLv")
                .build()

            val authFactory = TwitterFactory(authConfig)

            twitterAuth = authFactory.getInstance(OAuthAuthorization(authConfig)).authorization
        }

        return twitterAuth!!
    }

    fun collectTweet(streamingContext: JavaStreamingContext, twitterAuth: Authorization)
    {
        val tweetStream = TwitterUtils.createStream(streamingContext, twitterAuth)

        tweetStream.foreachRDD { statusRDD, time ->
            val tweetRDD = statusRDD
                    .filter { it.lang.equals("en") && it.hashtagEntities.isNotEmpty() }
                    .map {
                        val tweet = Tweet(text = it.text, profileImageUrl = it.user.profileImageURL, createdDate = Timestamp(it.createdAt.time), hashTag = it.hashtagEntities.first().text)
//                      faceAnalysis(tweet)
                        gson.toJson(tweet)
                    }

            tweetRDD.saveAsTextFile("/Users/cuong/Downloads/spark-1.5.1/examples/tweet_bank/${time.milliseconds()}")

            null
        }

        streamingContext.start()
        streamingContext.awaitTermination()
    }
}