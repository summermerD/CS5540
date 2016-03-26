package Model;

import java.io.Serializable;

public class FriendAnalytics implements Serializable{
	private String username;
	private Integer count;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

}
