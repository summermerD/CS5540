package home.playground.models;

import java.io.Serializable;

public class TweetsNumberAnalysis implements Serializable{
	private String username;
	private Integer tweetsCount;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getTweetsCount() {
		return tweetsCount;
	}
	public void setTweetsCount(Integer tweetsCount) {
		this.tweetsCount = tweetsCount;
	}
	

}
