package collection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.StreamController.User;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;




public class StreamCollection {
	public static void main(String[] args) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("R2v2WMKrF7UGipifRcMkOyjT1");
        cb.setOAuthConsumerSecret("InkVklJfUsJPQyA17GzGks9uzFSwUnRY9HqsR9m4vZ5Et3sW2d");
        cb.setOAuthAccessToken("3630687739-9y2qw6YKOMgeApmq09DKOuYosm2piadUy8aa96n");
        cb.setOAuthAccessTokenSecret("IBjoDz21BTBaXwnJ13jy2A0hOFaYzCYHmNRxCrhLLJong");

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        StatusListener listener = new StatusListener() {

            public void onStatus(Status status) {
            	
            	String a="@" + status.getUser().getScreenName() + " - " +status.getId() + " - " + status.getUser().getLocation() + " - "+ status.getText();
                System.out.println(a);
                
                try {

                	PrintWriter pw = new PrintWriter(new FileWriter("tweetsData.txt", true));
                	pw.println(a);
                	pw.close();
                  } catch ( IOException e ) {
                     e.printStackTrace();
                  }
                
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            	String b="Got a status deletion notice id:" + statusDeletionNotice.getStatusId();
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            	String c="Got track limitation notice:" + numberOfLimitedStatuses;
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
            	String d="Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId;
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}
			
			
        };
        

        
        
        FilterQuery fq = new FilterQuery();
    
        String keywords[] = {"travel"};

        fq.track(keywords);
        fq.language("en");

        twitterStream.addListener(listener);
        twitterStream.filter(fq);  

    }

}
