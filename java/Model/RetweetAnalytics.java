package Model;

import java.io.Serializable;

public class RetweetAnalytics implements Serializable {
	private String retweet;
	private Integer count;
	public String getRetweet() {
		return retweet;
	}
	public void setRetweet(String retweet) {
		this.retweet = retweet;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

}
