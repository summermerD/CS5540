package home.playground.services

import com.google.gson.Gson
import com.google.gson.JsonElement
import home.playground.models.FaceRecognization
import home.playground.models.Tweet
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

/**
 * Created by cuong on 11/14/15.
 */
class FaceAnalysisService
{
    private val apiKey: String = "49b985496341422f8251257fa06873ab"
    private val apiSecret: String = "Q64esshZDgV-o9h42WHStXgY8FF-N3z7"

    fun faceAnalysis(tweet: Tweet)
    {
        println(tweet.profileImageUrl.replace("normal", "400x400"))
        val fr = faceAnalysis(tweet.profileImageUrl.replace("normal", "400x400"))

        tweet.age = fr.age
        tweet.gender = fr.gender
    }

    fun faceAnalysis(imgUrl: String): FaceRecognization
    {
        val encodeUrl: String = URLEncoder.encode(imgUrl, "ISO-8859-1")
        val fr = FaceRecognization()

        val jsonBuff = StringBuffer()
        for (retry in 1..10) {
            val url = URL("http://apius.faceplusplus.com/v2/detection/detect?api_key=$apiKey&api_secret=$apiSecret&url=$encodeUrl&attribute=age%2Cgender%2Crace")
            val conn = url.openConnection()

            try {
                val rd = BufferedReader(InputStreamReader(conn.inputStream));

                var line: String? = rd.readLine()
                do {
                    jsonBuff.append(line)
                    line = rd.readLine()
                } while(line != null)

                break
            } catch(e: IOException) {
                println("$retry connection failed!")

                if (retry == 10) {
                    return fr
                } else {
                    Thread.sleep(3000)
                }
            }
        }

        val gson = Gson()
        val jsonObj = gson.fromJson(jsonBuff.toString(), JsonElement::class.java).asJsonObject

        if (jsonObj.getAsJsonArray("face").size() != 0) {
            val face = jsonObj.getAsJsonArray("face").first()

            fr.age = face.asJsonObject.get("attribute").asJsonObject.get("age").asJsonObject.get("value").asInt
            fr.range = face.asJsonObject.get("attribute").asJsonObject.get("age").asJsonObject.get("range").asInt
            fr.gender = face.asJsonObject.get("attribute").asJsonObject.get("gender").asJsonObject.get("value").asString
        }

        return fr
    }
}