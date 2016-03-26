package home.playground.models

import java.io.Serializable
import java.util.*

/**
 * Created by cuong on 11/15/15.
 */

data class SentimentStatistic(
    var total    : Int = 10,
    var sentiment: String = "Neutral",
    var freq     : Map<String, Map<String, Int>> = mapOf()
) : Serializable