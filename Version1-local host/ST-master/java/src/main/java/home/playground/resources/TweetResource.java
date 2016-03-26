package home.playground.resources;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import ch.epfl.lamp.fjbg.JConstantPool.Entry;
import home.playground.models.FollowerAnalysis;
import home.playground.models.FriendAnalysis;
import home.playground.models.HashtagAnalysis;
import home.playground.models.WordAnalysis;
import home.playground.models.LanguageAnalysis;
import home.playground.models.LocationAnalysis;
import home.playground.models.TweetsNumberAnalysis;
import home.playground.models.VerifiedAnalysis;
import home.playground.models.RetweetAnalysis;
import home.playground.models.TimelineAnalysis;
import home.playground.models.TweetAnalytics;
import javafx.scene.shape.Line;
import scala.Tuple2;
import scala.annotation.varargs;
import tachyon.thrift.MasterService.AsyncProcessor.liststatus;

import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.spherical.oned.ArcsSet.Split;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.catalyst.AbstractSparkSQLParser.Keyword;
import org.apache.zookeeper.server.quorum.Follower;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.codehaus.commons.compiler.Cookable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Controller
public class TweetResource implements Serializable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TweetResource.class);
    static public final MongoClientURI uri = new MongoClientURI("mongodb://root:password@ds037622.mongolab.com:37622/ase");
	
	static public final Gson gson = new Gson();

	static public final MongoClient dbCLient = new MongoClient(uri);
	static public final MongoDatabase db = dbCLient.getDatabase(uri.getDatabase());
	static public final MongoCollection<Document> collection = db.getCollection("twitter");	
    
    String id = "566089d4e4b0ed465284e831";

    @Autowired
    private JavaRDD<TweetAnalytics> tweetRdd;

    @RequestMapping(value="/")
    public String dashboard()
    {
        LOGGER.info("Get dashboard page");

        return "views/dashboard/page.html";
    }

    //get the percentage of verified/unverified users
    @ResponseBody
    @RequestMapping(value="/verified")
    public  List<List<Object>>  rerifiedAnalysisList()
    {
        LOGGER.info("Verified");
        final Document docResults = new Document();
        
        List<VerifiedAnalysis> verifiedAnalysis =  tweetRdd
        		.mapToPair(new PairFunction<TweetAnalytics, Boolean, Integer>() {
					@Override
					public Tuple2<Boolean, Integer> call(TweetAnalytics t) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<Boolean, Integer>(t.getVerified(), 1);
					}
				})
        		
        		.reduceByKey(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						// TODO Auto-generated method stub
						return v1 + v2;
					}
				})
        		
        		.map(new Function<Tuple2<Boolean, Integer>, VerifiedAnalysis>() {

					@Override
					public VerifiedAnalysis call(Tuple2<Boolean, Integer> v1) throws Exception {
						VerifiedAnalysis verifiedAnalysis = new VerifiedAnalysis();
						
						verifiedAnalysis.setVerified(v1._1);
						verifiedAnalysis.setCount(v1._2);

						return verifiedAnalysis;
					}
				}).collect();
        
        
		List<List<Object>> intm = new ArrayList<List<Object>>();   
		
        for (VerifiedAnalysis la : verifiedAnalysis) {
        	//to return value
        	List<Object> innerList = Arrays.asList(la.getVerified(), la.getCount());
        	intm.add(innerList);
        	
        	//record data to MongoLab
        	docResults.put("verified", la.getVerified());
    		docResults.put("count", la.getCount());    	
    		final Document findQuery = new Document("_id", new ObjectId(id));
    		final Document updateQuery = new Document("$push", new Document("Verified", docResults));
    	    
    		collection.findOneAndUpdate(findQuery, updateQuery);
        }
        
        return intm;         

    } 
    
  
    
    @ResponseBody //get 10 most popular hashtags
    @RequestMapping(value="/hashtag")
    public  List<List<Object>> hist()
    {
        LOGGER.info("Ten hashtag");
        final Document docResults = new Document();
        
        List<HashtagAnalysis> hashtagAnalysis = tweetRdd
        		
        		.mapToPair(new PairFunction<TweetAnalytics, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(TweetAnalytics t) throws Exception {
						return new Tuple2<String, Integer>(t.getHashTag(), 1);
					}
				})
        		
        		.reduceByKey(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				}) 
        		
        		// Transform to HashtashAnalytics  		
        		.map(new Function<Tuple2<String, Integer>, HashtagAnalysis>() { 
					@Override
					public HashtagAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						HashtagAnalysis hashtagAnalytics = new HashtagAnalysis();					
						hashtagAnalytics.setCount(v1._2);
						hashtagAnalytics.setHashTag(v1._1);
						return hashtagAnalytics;
					}
				})   
        		
        		.sortBy(new Function<HashtagAnalysis, Integer>() {
					@Override
					public Integer call(HashtagAnalysis v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2).take(10); 
        

        
		List<List<Object>> intm = new ArrayList<List<Object>>(); 
		
        for (HashtagAnalysis ha : hashtagAnalysis) {
        	//to return value
        	List<Object> innerList = Arrays.asList(ha.getHashTag(), ha.getCount());
        	intm.add(innerList);
        	
        	//to Mongolab
           	docResults.put("hashtag", ha.getHashTag());
        	docResults.put("count", ha.getCount());    	
        	final Document findQuery = new Document("_id", new ObjectId(id));
        	final Document updateQuery = new Document("$push", new Document("Hashtag", docResults));
        	    
        	collection.findOneAndUpdate(findQuery, updateQuery);
        }
        return intm;       
    }
    
    @ResponseBody //get 10 users who have most tweets
    @RequestMapping(value="/tweets")
    public List<List<Object>> mostTweetsUserList()
    {
        LOGGER.info("10 users who have most tweets");
        final Document docResults = new Document();
        
        List<TweetsNumberAnalysis> tweetsNumberAnalysis = tweetRdd.distinct()
        		
        		.mapToPair(new PairFunction<TweetAnalytics, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(TweetAnalytics t) throws Exception {
						return new Tuple2<String, Integer>(t.getUsername(), t.getTweetsCount());
					}
				})
        		
        		// Transform to ListAnalytics
        		.map(new Function<Tuple2<String, Integer>, TweetsNumberAnalysis>() {
					@Override
					public TweetsNumberAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						TweetsNumberAnalysis listAnalysis = new TweetsNumberAnalysis();				
						listAnalysis.setTweetsCount(v1._2);
						listAnalysis.setUsername(v1._1);						
						return listAnalysis;
					}
				}) 
        		
        		// Sort the most 
        		.sortBy(new Function<TweetsNumberAnalysis, Integer>() {
					@Override
					public Integer call(TweetsNumberAnalysis v1) throws Exception {
						return v1.getTweetsCount();
					}
				}, false, 2)
        		.take(10);
        
        		List<List<Object>> intm = new ArrayList<List<Object>>();
                
                for (TweetsNumberAnalysis ta :tweetsNumberAnalysis) {
                	List<Object> innerList = Arrays.asList(ta.getUsername(), ta.getTweetsCount());
                	intm.add(innerList);
                	
                	//to MongoLab
                	docResults.put("username", ta.getUsername());
            		docResults.put("count", ta.getTweetsCount());    	
            		final Document findQuery = new Document("_id", new ObjectId(id));
            		final Document updateQuery = new Document("$push", new Document("Tweetsnumber", docResults));
            	    
            		collection.findOneAndUpdate(findQuery, updateQuery);
                }
                return intm;
    }
    
    
    
    //get 10 most used language
    @ResponseBody
    @RequestMapping(value="/language")
    public List<List<Object>> languageList()
    {
        LOGGER.info("Language");
        final Document docResults = new Document();
        
        List<LanguageAnalysis> languageAnalysis = tweetRdd
        		.mapToPair(new PairFunction<TweetAnalytics, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(TweetAnalytics t) throws Exception {
						return new Tuple2<String, Integer>(t.getLanguage(), 1);
					}
				})
        		
        		.reduceByKey(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				})  
        		
        		// Transform to LanguageAnalytics
        		.map(new Function<Tuple2<String, Integer>, LanguageAnalysis>() {
					@Override
					public LanguageAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						LanguageAnalysis languageAnalysis = new LanguageAnalysis();
						languageAnalysis.setCount(v1._2);
						languageAnalysis.setLang(v1._1);						
						return languageAnalysis;
					}
				}) 
        		
        		// Sort the most 
        		.sortBy(new Function<LanguageAnalysis, Integer>() {
					@Override
					public Integer call(LanguageAnalysis v1) throws Exception {
						return v1.getCount();
					}
				}, false, 2)
        		.take(10);
//        		.stream()
//        		.collect(Collectors.toMap(new java.util.function.Function<LanguageAnalysis, String>() {
//					@Override
//					public String apply(LanguageAnalysis t) {
//						// TODO Auto-generated method stub
//						return t.getLang();
//					}
//				}, new java.util.function.Function<LanguageAnalysis, Integer>() {
//					@Override
//					public Integer apply(LanguageAnalysis t) {
//						// TODO Auto-generated method stub
//						return t.getCount();
//					}
//				}));
        

                
        for (LanguageAnalysis la : languageAnalysis) {

    		}
        		
        		List<List<Object>> intm = new ArrayList<List<Object>>();
                
                for (LanguageAnalysis la : languageAnalysis) {
                	
                	//to return value
                	List<Object> innerList = Arrays.asList(la.getLang(), la.getCount().toString());
                	intm.add(innerList);
                	
                	//to Mongolab
                	docResults.put("language", la.getLang());
            		docResults.put("count", la.getCount());    	
            		final Document findQuery = new Document("_id", new ObjectId(id));
            		final Document updateQuery = new Document("$push", new Document("Language", docResults));
            	    
            		collection.findOneAndUpdate(findQuery, updateQuery);
                }
                return intm;
    }
    
    
    //get 10 tweets have most retweet number
    @ResponseBody
    @RequestMapping(value="/retweet")
    public  List<List<Object>> retweetAnalysisList()
    {
        LOGGER.info("Retweet");
        final Document docResults = new Document();
        
        List<RetweetAnalysis> retweetAnalysis =  tweetRdd.distinct()
        		.mapToPair(new PairFunction<TweetAnalytics, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(TweetAnalytics t) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<String, Integer>(t.getRetweetText(), t.getRetweetCount());
					}
				})
        		
        		.map(new Function<Tuple2<String, Integer>, RetweetAnalysis>() {

					@Override
					public RetweetAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						RetweetAnalysis retweetAnalysis = new RetweetAnalysis();
						
						retweetAnalysis.setText(v1._1);
						retweetAnalysis.setRetweetCount(v1._2);

						return retweetAnalysis;
					}
				})
        		
        		.sortBy(new Function<RetweetAnalysis, Integer>() {
    					@Override
    					public Integer call(RetweetAnalysis v1) throws Exception {
    						return v1.getRetweetCount();
    					}
    				}, false, 2).take(10);
        

		List<List<Object>> intm = new ArrayList<List<Object>>();
        
        for (RetweetAnalysis ra : retweetAnalysis) {
        	List<Object> innerList = Arrays.asList(ra.getText(), ra.getRetweetCount());
        	intm.add(innerList);
        	
        	//to Mongolab
        	docResults.put("text", ra.getText());
			docResults.put("count", ra.getRetweetCount());    	
			final Document findQuery = new Document("_id", new ObjectId(id));
			final Document updateQuery = new Document("$push", new Document("Retweet", docResults));

			collection.findOneAndUpdate(findQuery, updateQuery);
        }
        return intm;
        

    }
    
    //get 10 user have most follower number
    @ResponseBody
    @RequestMapping(value="/follower")
    public  List<List<Object>> FollowerAnalysisList()
    {
        LOGGER.info("Follower");
        final Document docResults = new Document();
        
        List<FollowerAnalysis> followerAnalysis = tweetRdd.distinct()
        		.mapToPair(new PairFunction<TweetAnalytics, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(TweetAnalytics t) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<String, Integer>(t.getUsername(), t.getFollowerCount());
					}
				})
        		
        		.map(new Function<Tuple2<String, Integer>, FollowerAnalysis>() {

					@Override
					public FollowerAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						FollowerAnalysis followerAnalysis = new FollowerAnalysis();						
						followerAnalysis.setUsername(v1._1);
						followerAnalysis.setFollowerCount(v1._2);
						return followerAnalysis;
					}
				})
        		
        		.sortBy(new Function<FollowerAnalysis, Integer>() {
    					@Override
    					public Integer call(FollowerAnalysis v1) throws Exception {
    						return v1.getFollowerCount();
    					}
    				}, false, 2).take(10);
        

        List<List<Object>> intm = new ArrayList<List<Object>>();
        
        for (FollowerAnalysis fa : followerAnalysis) {
        	//to return value
        	List<Object> innerList = Arrays.asList(fa.getUsername(), fa.getFollowerCount());
        	intm.add(innerList);
        	
        	
        	//to Mongolab
        	docResults.put("username", fa.getUsername());
    		docResults.put("count", fa.getFollowerCount());    	
    		final Document findQuery = new Document("_id", new ObjectId(id));
    		final Document updateQuery = new Document("$push", new Document("Follower", docResults));
    	    
    		collection.findOneAndUpdate(findQuery, updateQuery);
        }
        return intm;
  }

  
    
  //get 10 user have most friends
    @ResponseBody
    @RequestMapping(value="/friend")
    public  List<List<Object>>  FriendAnalysisList()
    {
        LOGGER.info("friend");
        final Document docResults = new Document();
        
        List<FriendAnalysis> friendAnalysis = tweetRdd.distinct()
        		.mapToPair(new PairFunction<TweetAnalytics, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(TweetAnalytics t) throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<String, Integer>(t.getUsername(), t.getFriendCount());
					}
				})
        		
        		.map(new Function<Tuple2<String, Integer>, FriendAnalysis>() {

					@Override
					public FriendAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						FriendAnalysis friendAnalysis = new FriendAnalysis();					
						friendAnalysis.setUsername(v1._1);
						friendAnalysis.setFriendCount(v1._2);
						return friendAnalysis;
					}
				})
        		
        		.sortBy(new Function<FriendAnalysis, Integer>() {
    					@Override
    					public Integer call(FriendAnalysis v1) throws Exception {
    						return v1.getFriendCount();
    					}
    				}, false, 2).take(10); 
        

        List<List<Object>> intm = new ArrayList<List<Object>>();
        
        for (FriendAnalysis fa : friendAnalysis) {
        	
        	//to return value
        	List<Object> innerList = Arrays.asList(fa.getUsername(), fa.getFriendCount());
        	intm.add(innerList);
        	
        	//to Mongolab
          	docResults.put("username", fa.getUsername());
        	docResults.put("count", fa.getFriendCount());    	
        	final Document findQuery = new Document("_id", new ObjectId(id));
        	final Document updateQuery = new Document("$push", new Document("Friend", docResults));
        	    
        	collection.findOneAndUpdate(findQuery, updateQuery);
        }
        return intm;
        
    }
    
  
    @ResponseBody //get timeline for all tweets
    @RequestMapping(value="/timelines")
    public  List<List<Object>> timeline()
    {
        LOGGER.info("timeline");
        final Document docResults = new Document();
        String  id = "5664d879e4b04e04250ff8d9";
        List<TimelineAnalysis> timelineAnalysis = tweetRdd
        		.mapToPair(new PairFunction<TweetAnalytics, Timestamp, Integer>() {
					@Override
					public Tuple2<Timestamp, Integer> call(TweetAnalytics t) throws Exception {
						return new Tuple2<Timestamp, Integer>(t.getCreatedDate(), 1);
					}
				})
        		
        		.reduceByKey(new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				}) 
        		
        		.map(new Function<Tuple2<Timestamp, Integer>, TimelineAnalysis>() { // Transform to ListAnalytics
					@Override
					public TimelineAnalysis call(Tuple2<Timestamp, Integer> v1) throws Exception {
						TimelineAnalysis timelineAnalysis = new TimelineAnalysis();					
						timelineAnalysis.setCount(v1._2);
						timelineAnalysis.setTimestamp(v1._1);					
						return timelineAnalysis;
					}
				}) 
        		
        		// Sort the most 
        		.sortBy(new Function<TimelineAnalysis, Timestamp>() {  
					@Override
					public Timestamp call(TimelineAnalysis v1) throws Exception {
						return v1.getTimestamp();
					}
				}, true, 2).collect();  
        
        
        		List<List<Object>> intm = new ArrayList<List<Object>>();  
        		
                for (TimelineAnalysis ta : timelineAnalysis) {
                	//to return value
                	List<Object> innerList = Arrays.asList(ta.getTimestamp(), ta.getCount());
                	intm.add(innerList);
                	
                	//to Mongolab
                	docResults.put("timestamp", ta.getTimestamp().toString());
                	docResults.put("count", ta.getCount());    	
                	final Document findQuery = new Document("_id", new ObjectId(id));
                	final Document updateQuery = new Document("$push", new Document("Timeline", docResults));
           	    
            		collection.findOneAndUpdate(findQuery, updateQuery);
                }
                return intm;
    }
    
    //get timeline for a specific hashtag
    @ResponseBody
    @RequestMapping(value="/timeline/{hashTag}")
    public  Map<String, List<List<Long>>> hist(@PathVariable("hashTag") String hashTag)
    {
        LOGGER.info("Sentiment timeline of " + hashTag);

        return tweetRdd.filter(it -> it.getHashTag().equals(hashTag))
            .groupBy(TweetAnalytics::getHashTag)
            .mapValues(
                it -> StreamSupport.stream(it.spliterator(), false)
                    .collect(Collectors.groupingBy(TweetAnalytics::getCreatedDate, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .sorted(new Comparator<Map.Entry<Timestamp, Long>>() {
						@Override
						public int compare(Map.Entry<Timestamp, Long> lhs, Map.Entry<Timestamp, Long> rhs) {
							return lhs.getKey().compareTo(rhs.getKey());
						}
					})
                    .map(entry -> Arrays.asList(entry.getKey().getTime(), entry.getValue()))
                    .collect(Collectors.toList())
            )
            .collectAsMap();
    }
    

    @ResponseBody //get 10 most used words
    @RequestMapping(value="/words")
    public  List<List<Object>> wordAnalysisList()
    {
        LOGGER.info("words");
        final Document docResults = new Document();
        
        List<WordAnalysis> wordAnalysis = tweetRdd
        		.flatMap(new FlatMapFunction<TweetAnalytics, String>() {
					@Override
					public Iterable<String> call(TweetAnalytics t) throws Exception {
						String temp = t.getText();
						return Arrays.asList(temp.split(" "));
						}
					})
        		
        		.mapToPair(new PairFunction<String, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(String s){
						return new Tuple2<String, Integer>(s, 1);
					}
					
				})
        		
        		.reduceByKey(new Function2<Integer, Integer, Integer>() {
        			  public Integer call(Integer a, Integer b)
        			  { return a + b; }
        			  
        			  })
        		
        		.map(new Function<Tuple2<String, Integer>, WordAnalysis>() {
					@Override
					public WordAnalysis call(Tuple2<String, Integer> v1) throws Exception {
						WordAnalysis keywordAnalysis = new WordAnalysis();					
						keywordAnalysis.setKeyword(v1._1);
						keywordAnalysis.setCount(v1._2);
						return keywordAnalysis;
						}
					})
        		
        		.sortBy(new Function<WordAnalysis, Integer>() {
    					@Override
    					public Integer call(WordAnalysis v1) throws Exception {
    						return v1.getCount();
    					}
    				}, false, 2).take(10); 
   
        
        List<List<Object>> intm = new ArrayList<List<Object>>();
        
        for (WordAnalysis wa : wordAnalysis) {
        	//to return value
        	List<Object> innerList = Arrays.asList(wa.getKeyword(), wa.getCount());
        	intm.add(innerList);
        	
        	
        	//to Mongolab
        	docResults.put("word", wa.getKeyword());
    		docResults.put("count", wa.getCount());    	
    		final Document findQuery = new Document("_id", new ObjectId(id));
    		final Document updateQuery = new Document("$push", new Document("Word", docResults));
    	    
    		collection.findOneAndUpdate(findQuery, updateQuery);
        	}
        
        return intm;
    	}
    
}
 
    
    
    
