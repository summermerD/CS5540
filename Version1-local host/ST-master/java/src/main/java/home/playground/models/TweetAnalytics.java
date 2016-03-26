package home.playground.models;

import java.io.Serializable;
import java.sql.Timestamp;


import twitter4j.GeoLocation;


public class TweetAnalytics implements Serializable
{
    private String text;
    private String hashTag;
    private String mediaType;
    private String sentiment;
    private String profileImageUrl;
    private Timestamp createdDate;
    private Integer followerCount;
    private String language;
    private Integer retweetCount;
    private String username;
    private Integer friendCount;
    private Integer tweetsCount;
    private Boolean verified;
    private String retweetText;
    
    public  static String resolveLanguage (String language) {
    	if (language.equals("ar")) {
    		return "Arabic";
    	} else if (language.equals("zh")) {
    		return "Chinese";
    	} else if (language.equals("nl")) {
    		return "Dutch";
    	} else if (language.equals("en")) {
    		return "English";
    	} else if (language.equals("fa")) {
    		return "Farsi";
    	} else if (language.equals("fi")) {
    		return "Finnish";
    	} else if (language.equals("fr")) {
    		return "French";
    	} else if (language.equals("hi")) {
    		return "Hindi";
    	} else if (language.equals("id")) {
    		return "Indonesian";
    	} else if (language.equals("it")) {
    		return "Italian";
    	} else if (language.equals("ja")) {
    		return "Japanese";
    	} else if (language.equals("ko")) {
    		return "Korean";
    	} else if (language.equals("no")) {
    		return "Norwegian";
    	} else if (language.equals("pl")) {
    		return "Polish";
    	} else if (language.equals("pt")) {
    		return "Portuguese";
    	} else if (language.equals("ru")) {
    		return "Russian";
    	} else if (language.equals("es")) {
    		return "Spanish";
    	} else if (language.equals("sv")) {
    		return "Swedish";
    	} else if (language.equals("th")) {
    		return "Thai";
    	} else if (language.equals("tr")) {
    		return "Turkish";
    	}
    	else return "Unknown"; 	
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getHashTag()
    {
        return hashTag;
    }

    public void setHashTag(String hashTag)
    {
        this.hashTag = hashTag;
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public String getSentiment()
    {
        return sentiment;
    }

    public void setSentiment(String sentiment)
    {
        this.sentiment = sentiment;
    }

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public Timestamp getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate)
    {
        this.createdDate = createdDate;
    }

	public Integer getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(Integer followerCount) {
		this.followerCount = followerCount;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(Integer friendCount) {
		this.friendCount = friendCount;
	}

	public Integer getTweetsCount() {
		return tweetsCount;
	}

	public void setTweetsCount(Integer tweetsCount) {
		this.tweetsCount = tweetsCount;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getRetweetText() {
		return retweetText;
	}

	public void setRetweetText(String retweetText) {
		this.retweetText = retweetText;
	}


}
