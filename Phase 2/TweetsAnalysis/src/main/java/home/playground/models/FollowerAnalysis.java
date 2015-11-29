package home.playground.models;

import java.io.Serializable;


public class FollowerAnalysis implements Serializable{
	private String username;
	private Integer followerCount;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getFollowerCount() {
		return followerCount;
	}
	public void setFollowerCount(Integer followerCount) {
		this.followerCount = followerCount;
	}

}
