package home.playground.models

import java.io.Serializable
import java.sql.Timestamp
import java.util.*

/**
 * Created by cuong on 11/14/15.
 */
fun resolveAgeGroup(age: Int): String
{
    if (age < 18) {
            return "child"
    } else if (age <= 24) {
            return "adult18to24"
    } else if (age <= 34) {
            return "adult25to34"
    } else if (age <= 44) {
            return "adult35to44"
    } else if (age <= 54) {
            return "adult45to54"
    } else if (age <= 64) {
            return "adult55to64"
    } else {
            return "adultOver64"
    }
}

data class Tweet (
    val text           : String    = "",
    val profileImageUrl: String    = "",
    val hashTag        : String    = "",
    val createdDate    : Timestamp = Timestamp(Date().time),
    var gender         : String?   = null, // Enum: Male, Female, Unknown
    var sentiment      : String    = "Neutral",   // Enum: Pos, Neg, Neu, Unknown
    var age            : Int?      = null,
    val mediaType      : String    = ""
) : Serializable