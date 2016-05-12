package Model;

import java.io.Serializable;

public class HashtagAnalytics implements Serializable{
	private String hashtag;
	private Integer count;
	public String getHashtag() {
		return hashtag;
	}
	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
