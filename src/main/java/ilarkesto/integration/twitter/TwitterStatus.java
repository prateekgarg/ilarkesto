package ilarkesto.integration.twitter;

import ilarkesto.auth.LoginDataProvider;
import ilarkesto.base.time.DateAndTime;

import java.text.ParseException;
import java.util.TimeZone;

import org.jdom2.Element;

public class TwitterStatus implements Comparable<TwitterStatus> {

	LoginDataProvider login;
	private DateAndTime createdAt;
	private String id;
	private String text;
	private String source;
	private boolean turncated;
	private String inReplyToStatusId;
	private String inReplyToUserId;
	private TwitterUser user;

	public TwitterStatus(LoginDataProvider login, Element e) {
		this.login = login;
		try {
			String s = e.getChildText("created_at");
			createdAt = new DateAndTime(Twitter.DATE_TIME_FORMAT.parse(s));
			createdAt = createdAt.addHours(TimeZone.getDefault().getOffset(createdAt.toMillis()));
		} catch (ParseException ex) {
			throw new RuntimeException(ex);
		}
		// createdAt = createdAt.addHours(-2);
		id = e.getChildText("id");
		text = e.getChildText("text");
		source = e.getChildText("source");
		turncated = Boolean.parseBoolean(e.getChildText("turncated"));
		inReplyToStatusId = e.getChildText("in_reply_to_status_id");
		inReplyToUserId = e.getChildText("in_reply_to_user_id");
		user = new TwitterUser(e.getChild("user"));
	}

	public DateAndTime getCreatedAt() {
		return createdAt;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getSource() {
		return source;
	}

	public boolean isTurncated() {
		return turncated;
	}

	public String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public String getInReplyToUserId() {
		return inReplyToUserId;
	}

	public TwitterUser getUser() {
		return user;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public int compareTo(TwitterStatus o) {
		return createdAt.compareTo(o.createdAt);
	}

	@Override
	public String toString() {
		return createdAt + " " + user + ": " + text;
	}
}