package wasdev.sample.servlet;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.avro.test.Simple;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import Model.FollowerAnalytics;
import Model.FriendAnalytics;
import Model.HashtagAnalytics;
import Model.LanguageAnalytics;
import Model.RetweetAnalytics;
import Model.TimelineAnalytics;
import Model.TweetsAnalytics;
import Model.FollowerAnalytics;
import Model.VerificationAnalytics;
import scala.Tuple2;
import twitter4j.Status;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.Authorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SparkRunner implements Runnable, Serializable
{
	final static public Map<String, Collection> pipeBank = new HashMap<>();

    static {
    	final SparkRunner runner = new SparkRunner();
    	new Thread(runner).start();
    }
    
	
	public SparkRunner() {
		pipeBank.put(LanguageServlet.pipeKey, new ConcurrentLinkedQueue<List<LanguageAnalytics>>());
		pipeBank.put(HashtagServlet.pipeKey, new ConcurrentLinkedQueue<List<HashtagAnalytics>>());
		pipeBank.put(FollowerServlet.pipeKey, new ConcurrentLinkedQueue<List<FollowerAnalytics>>());
		pipeBank.put(FriendServlet.pipeKey, new ConcurrentLinkedQueue<List<FriendAnalytics>>());
		pipeBank.put(TweetsServlet.pipeKey, new ConcurrentLinkedQueue<List<TweetsAnalytics>>());
		pipeBank.put(VerificationServlet.pipeKey, new ConcurrentLinkedQueue<List<VerificationAnalytics>>());
		pipeBank.put(RetweetServlet.pipeKey, new ConcurrentLinkedQueue<List<RetweetAnalytics>>());
		pipeBank.put(TimelineServlet.pipeKey, new ConcurrentLinkedQueue<List<TimelineAnalytics>>());


		// TODO add more pipe
	}
	
	@Override
	public void run()
	{		
	    final SparkConf conf = new SparkConf().setMaster("local[4]")
	            .setAppName("SparkPlayground");
	    	
	    final Configuration configuration = new ConfigurationBuilder()
				.setOAuthConsumerKey("R2v2WMKrF7UGipifRcMkOyjT1")
				.setOAuthConsumerSecret("InkVklJfUsJPQyA17GzGks9uzFSwUnRY9HqsR9m4vZ5Et3sW2d")
				.setOAuthAccessToken("3630687739-9y2qw6YKOMgeApmq09DKOuYosm2piadUy8aa96n")
				.setOAuthAccessTokenSecret("IBjoDz21BTBaXwnJ13jy2A0hOFaYzCYHmNRxCrhLLJong")
				.build();
		
	    final Authorization authorization = new TwitterStreamFactory(configuration).getInstance().getAuthorization();
	    
	    // Collect batch of tweets every 3,000 milliseconds or 3 seconds
	    final JavaStreamingContext javaStreamingContext = new JavaStreamingContext(conf, new Duration(3000));

        TwitterUtils.createStream(javaStreamingContext, authorization)
        .foreachRDD(new Function<JavaRDD<Status>, Void>() {
			@Override
			public Void call(JavaRDD<Status> statusRDD) throws Exception {
				
				// First query
				final List<LanguageAnalytics> languageAnalysisResult = statusRDD.groupBy(new Function<Status, String>(){
					@Override
					public String call(Status status) throws Exception {
						return LanguageAnalytics.resolveLanguage(status.getLang());
					}}
				).map(new Function<Tuple2<String, Iterable<Status>>, LanguageAnalytics>() {
					@Override
					public LanguageAnalytics call(Tuple2<String, Iterable<Status>> pair) throws Exception {
						final LanguageAnalytics ta = new LanguageAnalytics();
						ta.setLanguage(pair._1);
						ta.setCountLanguage(Arrays.asList(pair._2).size());
						
						return ta;
					}
					
				}).collect();
				
		        final ConcurrentLinkedQueue<List<LanguageAnalytics>> languageAnalysisPipe = (ConcurrentLinkedQueue<List<LanguageAnalytics>>) pipeBank.get(LanguageServlet.pipeKey);
		        languageAnalysisPipe.add(languageAnalysisResult);
				if (languageAnalysisPipe.size() == 2) {
					languageAnalysisPipe.remove();
				}
				// First query end here
				
				// More query
				// Get result
				// put result into the pie
				final List<HashtagAnalytics> hashtagAnalysisResult = statusRDD.filter(new Function<Status, Boolean>() {
					@Override
					public Boolean call(Status status) throws Exception {
						// TODO Auto-generated method stub
						return status.getHashtagEntities().length != 0 && status.getLang().equals("en");
					}
				}).groupBy(new Function<Status, String>(){
					@Override
					public String call(Status status) throws Exception {
						return status.getHashtagEntities()[0].getText();
					}}
				).map(new Function<Tuple2<String, Iterable<Status>>, HashtagAnalytics>() {
					@Override
					public HashtagAnalytics call(Tuple2<String, Iterable<Status>> pair) throws Exception {
						final HashtagAnalytics ta = new HashtagAnalytics();
						ta.setHashtag(pair._1);
						ta.setCount(Arrays.asList(pair._2).size());
						
						return ta;
					}
					
				}).sortBy(new Function<HashtagAnalytics, Integer>() {
					@Override
					public Integer call(HashtagAnalytics v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2).take(10);
				
		        final ConcurrentLinkedQueue<List<HashtagAnalytics>> hashtagAnalysisPipe = (ConcurrentLinkedQueue<List<HashtagAnalytics>>) pipeBank.get(HashtagServlet.pipeKey);
		        hashtagAnalysisPipe.add(hashtagAnalysisResult);
				if (hashtagAnalysisPipe.size() == 2) {
					hashtagAnalysisPipe.remove();
				}
				
				//follower analysis
				
				final List<FollowerAnalytics> followerAnalysisResult = statusRDD.filter(new Function<Status, Boolean>() {
					@Override
					public Boolean call(Status status) throws Exception {
						// TODO Auto-generated method stub
						return status.getLang().equals("en");
					}
				}).groupBy(new Function<Status, String>(){
                    @Override
                    public String call(Status status) throws Exception {
                        return status.getUser().getName();
                    }
                })
                .map(new Function<Tuple2<String, Iterable<Status>>, FollowerAnalytics>() {
                    @Override
                    public FollowerAnalytics call(Tuple2<String, Iterable<Status>> pair) throws Exception {
                        int followerCount = 0;
                        for (final Status s : pair._2) {
                            if (s.getUser().getFollowersCount() > followerCount) {
                                // get some new followers
                                followerCount = s.getUser().getFollowersCount();
                            }
                        }
                        
                        final FollowerAnalytics fa = new FollowerAnalytics();
                        fa.setUsername(pair._1);
                        fa.setCount(followerCount);
                        
                        return fa;
                    }
                }).	sortBy(new Function<FollowerAnalytics, Integer>() {
					@Override
					public Integer call(FollowerAnalytics v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2).take(10);
				
		        final ConcurrentLinkedQueue<List<FollowerAnalytics>> followerAnalysisPipe = (ConcurrentLinkedQueue<List<FollowerAnalytics>>) pipeBank.get(FollowerServlet.pipeKey);
		        followerAnalysisPipe.add(followerAnalysisResult);
				if (followerAnalysisPipe.size() == 2) {
					followerAnalysisPipe.remove();
				}
				
				//friend analysis
				final List<FriendAnalytics> friendAnalysisResult = statusRDD.filter(new Function<Status, Boolean>() {
					@Override
					public Boolean call(Status status) throws Exception {
						// TODO Auto-generated method stub
						return status.getLang().equals("en");
					}
				}).groupBy(new Function<Status, String>(){
                    @Override
                    public String call(Status status) throws Exception {
                        return status.getUser().getName();
                    }
                })
                .map(new Function<Tuple2<String, Iterable<Status>>, FriendAnalytics>() {
                    @Override
                    public FriendAnalytics call(Tuple2<String, Iterable<Status>> pair) throws Exception {
                        int friendCount = 0;
                        for (final Status s : pair._2) {
                            if (s.getUser().getFriendsCount() > friendCount) {
                                // get some new friends
                                friendCount = s.getUser().getFriendsCount();
                            }
                        }
                        
                        final FriendAnalytics fa = new FriendAnalytics();
                        fa.setUsername(pair._1);
                        fa.setCount(friendCount);
                        
                        return fa;
                    }
                }).sortBy(new Function<FriendAnalytics, Integer>() {
					@Override
					public Integer call(FriendAnalytics v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2).take(10);
				
		        final ConcurrentLinkedQueue<List<FriendAnalytics>> friendAnalysisPipe = (ConcurrentLinkedQueue<List<FriendAnalytics>>) pipeBank.get(FriendServlet.pipeKey);
		        friendAnalysisPipe.add(friendAnalysisResult);
				if (friendAnalysisPipe.size() == 2) {
					friendAnalysisPipe.remove();
				}
				
				
				//user most tweets analysis
				final List<TweetsAnalytics> tweetsAnalysisResult = statusRDD.filter(new Function<Status, Boolean>() {
					@Override
					public Boolean call(Status status) throws Exception {
						// TODO Auto-generated method stub
						return status.getLang().equals("en");
					}
				}).groupBy(new Function<Status, String>(){
                    @Override
                    public String call(Status status) throws Exception {
                        return status.getUser().getName();
                    }
                })
                .map(new Function<Tuple2<String, Iterable<Status>>, TweetsAnalytics>() {
                    @Override
                    public TweetsAnalytics call(Tuple2<String, Iterable<Status>> pair) throws Exception {
                        int tweetCount = 0;
                        for (final Status s : pair._2) {
                            if (s.getUser().getStatusesCount() > tweetCount) {
                                // get some new friends
                                tweetCount = s.getUser().getStatusesCount();
                            }
                        }
                        
                        final TweetsAnalytics fa = new TweetsAnalytics();
                        fa.setUsername(pair._1);
                        fa.setCount(tweetCount);
                        
                        return fa;
                    }
                }).sortBy(new Function<TweetsAnalytics, Integer>() {
					@Override
					public Integer call(TweetsAnalytics v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2).take(10);
				
		        final ConcurrentLinkedQueue<List<TweetsAnalytics>> tweetsAnalysisPipe = (ConcurrentLinkedQueue<List<TweetsAnalytics>>) pipeBank.get(TweetsServlet.pipeKey);
		        tweetsAnalysisPipe.add(tweetsAnalysisResult);
				if (tweetsAnalysisPipe.size() == 2) {
					tweetsAnalysisPipe.remove();
				}
				
				
				//verification analysis
				final List<VerificationAnalytics> verificationAnalysisResult = statusRDD.filter(new Function<Status, Boolean>() {
					@Override
					public Boolean call(Status status) throws Exception {
						// TODO Auto-generated method stub
						return status.getLang().equals("en");
					}
				}).groupBy(new Function<Status, Boolean>(){
					@Override
					public Boolean call(Status status) throws Exception {
						return status.getUser().isVerified();
					}}
				).map(new Function<Tuple2<Boolean, Iterable<Status>>, VerificationAnalytics>() {
					@Override
					public VerificationAnalytics call(Tuple2<Boolean, Iterable<Status>> pair) throws Exception {
						final VerificationAnalytics ta = new VerificationAnalytics();
						ta.setVerified(pair._1);
						ta.setCount(Arrays.asList(pair._2).size());
						
						return ta;
					}
					
				}).collect();
				
		        final ConcurrentLinkedQueue<List<VerificationAnalytics>> verificationAnalysisPipe = (ConcurrentLinkedQueue<List<VerificationAnalytics>>) pipeBank.get(VerificationServlet.pipeKey);
		        verificationAnalysisPipe.add(verificationAnalysisResult);
				if (verificationAnalysisPipe.size() == 2) {
					verificationAnalysisPipe.remove();
				}
				
				
				//retweet analysis
				final List<RetweetAnalytics> retweetAnalysisResult = statusRDD.filter(new Function<Status, Boolean>() {
					@Override
					public Boolean call(Status status) throws Exception {
						// TODO Auto-generated method stub
						return status.getLang().equals("en");
					}
				}).groupBy(new Function<Status, String>(){
                    @Override
                    public String call(Status status) throws Exception {
						if (status.getRetweetedStatus() != null){
							return status.getRetweetedStatus().getText();
						}
						else return null;
                    }
                })
                .map(new Function<Tuple2<String, Iterable<Status>>, RetweetAnalytics>() {
                    @Override
                    public RetweetAnalytics call(Tuple2<String, Iterable<Status>> pair) throws Exception {
                        int retweetCount = 0;
                        for (final Status s : pair._2) {
                            if ((s.getRetweetedStatus() != null) && s.getRetweetedStatus().getRetweetCount() > retweetCount) {
                                // get some new friends
                                retweetCount = s.getRetweetedStatus().getRetweetCount();
                            }
                        }
                        
                        final RetweetAnalytics fa = new RetweetAnalytics();
                        fa.setRetweet(pair._1);
                        fa.setCount(retweetCount);
                        
                        return fa;
                    }
                }).sortBy(new Function<RetweetAnalytics, Integer>() {
					@Override
					public Integer call(RetweetAnalytics v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2).take(10);
				
		        final ConcurrentLinkedQueue<List<RetweetAnalytics>> retweetAnalysisPipe = (ConcurrentLinkedQueue<List<RetweetAnalytics>>) pipeBank.get(RetweetServlet.pipeKey);
		        retweetAnalysisPipe.add(retweetAnalysisResult);
				if (retweetAnalysisPipe.size() == 2) {
					retweetAnalysisPipe.remove();
				}
				
				
				
				
				//timeline analysis
				final List<TimelineAnalytics> timelineAnalysisResult = statusRDD.groupBy(new Function<Status, Timestamp>(){
					@Override
					public Timestamp call(Status status) throws Exception {
						Timestamp time = new Timestamp(status.getCreatedAt().getTime());
						System.out.print(time);
						return time;
					}}
				).map(new Function<Tuple2<Timestamp, Iterable<Status>>, TimelineAnalytics>() {
					@Override
					public TimelineAnalytics call(Tuple2<Timestamp, Iterable<Status>> pair) throws Exception {
						final TimelineAnalytics ta = new TimelineAnalytics();
						ta.setTimestamp(pair._1);
						ta.setCount(Arrays.asList(pair._2).size());
						
						return ta;
					}
					
				}).sortBy(new Function<TimelineAnalytics, Timestamp>() {
					@Override
					public Timestamp call(TimelineAnalytics v1) throws Exception {
						return v1.getTimestamp();
					}
				}, true, 2).collect();
				
		        final ConcurrentLinkedQueue<List<TimelineAnalytics>> timelineAnalysisPipe = (ConcurrentLinkedQueue<List<TimelineAnalytics>>) pipeBank.get(TimelineServlet.pipeKey);
		        timelineAnalysisPipe.add(timelineAnalysisResult);
				if (timelineAnalysisPipe.size() == 2) {
					timelineAnalysisPipe.remove();
				}
				
				return null;
			}
		});
    
        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
        
//    	(new Thread(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					Thread.sleep(60000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				javaStreamingContext.stop(false, true);
//			}
//		})).start();
	}   
}
