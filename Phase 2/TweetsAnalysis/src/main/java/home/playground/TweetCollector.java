package home.playground;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.mutable.MutableLong;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import com.google.gson.Gson;

import home.playground.models.TweetAnalytics;
import jdk.nashorn.internal.ir.visitor.NodeOperatorVisitor;
import twitter4j.Status;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.Authorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
public class TweetCollector
{
	public static void main(String[] args)
	{
		final Configuration configuration = new ConfigurationBuilder()
				.setOAuthConsumerKey("R2v2WMKrF7UGipifRcMkOyjT1")
				.setOAuthConsumerSecret("InkVklJfUsJPQyA17GzGks9uzFSwUnRY9HqsR9m4vZ5Et3sW2d")
				.setOAuthAccessToken("3630687739-9y2qw6YKOMgeApmq09DKOuYosm2piadUy8aa96n")
				.setOAuthAccessTokenSecret("IBjoDz21BTBaXwnJ13jy2A0hOFaYzCYHmNRxCrhLLJong")
				.build();
		
        Authorization authorization = new TwitterStreamFactory(configuration).getInstance().getAuthorization();

        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("TweetCollector");
        
        final MutableLong count = new MutableLong(0);
        long limit = 100000L;
        
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, new Duration(3000));
        
        TwitterUtils.createStream(javaStreamingContext, authorization)
	        .foreachRDD(new Function<JavaRDD<Status>, Void>() {
				@Override
				public Void call(JavaRDD<Status> status) throws Exception {
					JavaRDD<String> tweets = status.filter(s -> s.getHashtagEntities().length != 0)
						.map(new Function<Status, String>() {
							@Override
							public String call(Status status) throws Exception {
								TweetAnalytics tweetAnalytics = new TweetAnalytics();
								tweetAnalytics.setUsername(status.getUser().getName());
								tweetAnalytics.setFollowerCount(status.getUser().getFollowersCount());
								tweetAnalytics.setHashTag(status.getHashtagEntities()[0].getText());
								tweetAnalytics.setLanguage(tweetAnalytics.resolveLanguage(status.getLang()));
								tweetAnalytics.setText(status.getText());
								if (status.getRetweetedStatus() != null){
									tweetAnalytics.setRetweetText(status.getRetweetedStatus().getText());
									tweetAnalytics.setRetweetCount(status.getRetweetedStatus().getRetweetCount());
									}
								else {
									tweetAnalytics.setRetweetText("Null");
									tweetAnalytics.setRetweetCount(0);
								}
								Timestamp timestamp = new Timestamp(status.getCreatedAt().getTime());
								tweetAnalytics.setFriendCount(status.getUser().getFriendsCount());
								tweetAnalytics.setCreatedDate(timestamp);
								tweetAnalytics.setTweetsCount(status.getUser().getStatusesCount());
								tweetAnalytics.setVerified(status.getUser().isVerified());
								Gson gson = new Gson();
								// Use Gson to transform java object to JSON string
								return gson.toJson(tweetAnalytics);
							}
						});
						
					count.add(tweets.count());
					
					System.out.println(count.longValue());
					tweets.saveAsTextFile("/Users/Ting/Downloads/tweet_bank/" + System.currentTimeMillis());
	
					if (count.longValue() > limit) {
						System.exit(0);
					}
					
					return null;
				}
			});
        
        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
	}
}
