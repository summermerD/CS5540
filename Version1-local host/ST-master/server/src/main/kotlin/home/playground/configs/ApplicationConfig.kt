package home.playground.configs

import home.playground.models.Tweet
import org.apache.spark.api.java.JavaRDD

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by cuong on 11/14/15.
 */
@Configuration
open class ApplicationConfig
{
    companion object {
        private val LOGGER = LoggerFactory.getLogger(ApplicationConfig::class.java)
    }

    @Bean
    open public fun tweetRdd(): JavaRDD<Tweet>
    {
        return Global.configureTweetRdd()
    }
}