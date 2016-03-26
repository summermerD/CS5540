package home.playground.services

import com.google.gson.Gson
import com.google.gson.JsonElement
import home.playground.models.Tweet
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

/**
 * Created by cuong on 11/14/15.
 */
class SentimentAnalysis
{
    fun sentimentAnalysis(tweet: Tweet)
    {
        tweet.sentiment = sentimentAnalysis(tweet.text)
    }

    fun sentimentAnalysis(text: String): String
    {
        val apiKey = "2d1d83e324ca89a09731e06449b33073b2e5b48b"
        val encodedText = URLEncoder.encode(text, "ISO-8859-1")

        val sentimentApi = "http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=$apiKey&text=$encodedText&outputMode=json"

        val url = URL(sentimentApi)

        val conn = url.openConnection()
        val rd = BufferedReader(InputStreamReader(conn.inputStream));

        val jsonBuff = StringBuffer()
        var line: String? = rd.readLine()

        do {
            jsonBuff.append(line)
            line = rd.readLine()
        } while(line != null)

        val gson = Gson()
        val doc = gson.fromJson(jsonBuff.toString(), JsonElement::class.java).asJsonObject

        return doc?.get("docSentiment")?.asJsonObject?.get("type")?.asString?.capitalize() ?: "Neutral"
    }
}