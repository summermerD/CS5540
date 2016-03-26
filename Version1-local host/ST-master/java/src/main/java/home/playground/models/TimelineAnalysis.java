package home.playground.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class TimelineAnalysis implements Serializable {
	private Timestamp timestamp;
	private Integer count;
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
