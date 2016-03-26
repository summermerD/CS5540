package home.playground.models

import java.io.Serializable

/**
 * Created by cuong on 11/15/15.
 */
data class HashTagStatistic(
    val count  : Int = 0,
    val hashTag: String = ""
) : Serializable