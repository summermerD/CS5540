package home.playground.models;

import java.io.Serializable;

public class FriendAnalysis implements Serializable{
	private String username;
	private Integer friendCount;
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

}
