package home.playground.resources

import home.playground.models.*
import home.playground.services.FaceAnalysisService
import home.playground.services.SentimentAnalysis
import org.apache.spark.api.java.JavaRDD
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import scala.Tuple2
import java.io.Serializable
import java.sql.Timestamp
import java.util.*
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


/**
 * Created by cuong on 11/14/15.
 */
@Controller
class TweetResource
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(TweetResource::class.java)
    }

    private val fas = FaceAnalysisService()

    private val sas = SentimentAnalysis()

    @Autowired
    private var tweetRdd: JavaRDD<Tweet>? = null

    @RequestMapping(value="/")
    public fun dashboard(): String?
    {
        LOGGER.info("Get dashboard page")

        return "views/dashboard/page.html"
    }

    @ResponseBody
    @RequestMapping(value="/top")
    @Produces(MediaType.APPLICATION_JSON)
    public fun top(): List<HashTagStatistic>
    {
        LOGGER.info("Return top 10 hashtags")

        return tweetRdd!!.groupBy { it.hashTag }
            .mapValues { it.count() }
            .top(10, object : Comparator<Tuple2<String, Int>>, Serializable {
                override fun compare(lhs: Tuple2<String, Int>?, rhs: Tuple2<String, Int>?): Int {
                    return lhs?._2!! - rhs?._2!!
                }
            })
            .map { HashTagStatistic(hashTag = it._1, count = it._2) }
    }

    @ResponseBody
    @RequestMapping(value="/analyze/{hashTag}")
    @Produces(MediaType.APPLICATION_JSON)
    public fun analyze(@PathVariable("hashTag") hashTag: String): List<SentimentStatistic>
    {
        LOGGER.info("Sentiment analysis of $hashTag")

        return tweetRdd!!.filter {it.hashTag == hashTag}
            .groupBy { it.sentiment }
            .mapValues {
                val groups = arrayOf("child", "adult18to24", "adult25to34", "adult35to44", "adult45to54", "adult55to64", "adultOver64")

                var rst = it.groupBy { resolveAgeGroup(it.age as Int) }
                              .mapValues {
                                  it.value.partition { it.gender == "male" }
                                          .let { mapOf("male" to it.first.size, "female" to it.second.size) }
                              }


                for (ag in groups) {
                    if (!rst.containsKey(ag)) {
                        rst = rst.plus(ag to mapOf("male" to 0, "female" to 0))
                    }
                }

                rst
            }
            .map {
                SentimentStatistic(
                    freq = it._2,
                    sentiment = it._1,
                    total = it._2.values.fold(0) { total, ageGroup -> total + ageGroup.values.sum() }
                )
            }
            .collect()
    }

    @ResponseBody
    @RequestMapping(value="/timeline")
    @Produces(MediaType.APPLICATION_JSON)
    public fun analyze(@RequestParam("hashTag") hashTag: String, @RequestParam("start") start: Long, @RequestParam("end") end: Long): List<SentimentStatistic>
    {
        LOGGER.info("Sentiment analysis of $hashTag betweet $start and $end")

        return tweetRdd!!.filter { it.hashTag == hashTag && (Timestamp(start) < it.createdDate && it.createdDate < Timestamp(end)) }
                .groupBy { it.sentiment }
                .mapValues {
                    val groups = arrayOf("child", "adult18to24", "adult25to34", "adult35to44", "adult45to54", "adult55to64", "adultOver64")

                    var rst = it.groupBy { resolveAgeGroup(it.age as Int) }
                            .mapValues {
                                it.value.partition { it.gender == "male" }
                                        .let { mapOf("male" to it.first.size, "female" to it.second.size) }
                            }


                    for (ag in groups) {
                        if (!rst.containsKey(ag)) {
                            rst = rst.plus(ag to mapOf("male" to 0, "female" to 0))
                        }
                    }

                    rst
                }
                .map {
                    SentimentStatistic(
                        freq = it._2,
                        sentiment = it._1,
                        total = it._2.values.fold(0) { total, ageGroup -> total + ageGroup.values.sum() }
                    )
                }
                .collect()
    }

    @ResponseBody
    @RequestMapping(value="/timeline/{hashTag}")
    public fun hist(@PathVariable("hashTag") hashTag: String): Map<String, List<Array<Long>>>
    {
        LOGGER.info("Sentiment timeline of $hashTag")

        return tweetRdd!!.filter {it.hashTag == hashTag}
                .groupBy {it.sentiment}
                .mapValues { it.groupBy { it.createdDate.time }.map { arrayOf(it.key, it.value.count().toLong()) } }
                .collectAsMap()
    }

    @ResponseBody
    @RequestMapping(value="/recognize")
    @Produces(MediaType.APPLICATION_JSON)
    public fun recognize(@RequestParam("imgUrl") imgUrl: String): FaceRecognization
    {
        return fas.faceAnalysis(imgUrl)
    }

    @ResponseBody
    @RequestMapping(value="/sentiment", method = arrayOf(RequestMethod.POST))
    @Produces(MediaType.APPLICATION_JSON)
    public fun sentiment(@RequestParam("keytext") text: String): String
    {
        LOGGER.info(text)

        return sas.sentimentAnalysis(text)
    }
}