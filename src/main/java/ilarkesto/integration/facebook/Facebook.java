package ilarkesto.integration.facebook;

import ilarkesto.auth.LoginData;
import ilarkesto.auth.LoginDataProvider;
import ilarkesto.base.Str;
import ilarkesto.core.logging.Log;
import ilarkesto.core.time.Date;
import ilarkesto.core.time.DateAndTime;
import ilarkesto.integration.oauth.OAuth;
import ilarkesto.io.IO;
import ilarkesto.json.JsonObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;

/**
 * https://developers.facebook.com/docs/reference/api/
 */
public class Facebook {

	public static void main(String[] args) {
		Properties properties = IO.loadProperties(new File("../organizanto/runtimedata/config.properties"), IO.UTF_8);
		String apisecret = properties.getProperty("facebook.oauth.apisecret");
		String callbackUri = "https://servisto.de/organizanto/facebookcallback";

		Facebook facebook = new Facebook(new LoginData("2a66c6df5ed07c971c8bc474949b69f3", apisecret), callbackUri);

		// String url = facebook.getUserOAuthUrl(Utl.toList(PERMISSION_READ_STREAM,
		// PERMISSION_USER_ACTIVITIES,
		// PERMISSION_OFFLINE_ACCESS));
		// System.out.println(url);

		// String code = "";
		// LoginData accessToken = facebook.createAccessToken(code);
		// System.out.println(accessToken.getLogin());
		// System.out.println(accessToken.getPassword());

		String accessToken = properties.getProperty("facebook.oauth.accesstoken");

		// System.out.println(facebook.loadMeLikes(accessToken, null));
		System.out.println(facebook.loadPerson(accessToken, "koczewski"));
	}

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

	public static final String PERMISSION_READ_STREAM = "read_stream";
	public static final String PERMISSION_OFFLINE_ACCESS = "offline_access";
	public static final String PERMISSION_USER_ACTIVITIES = "user_activities";
	public static final String PERMISSION_USER_LIKES = "user_likes";
	public static final String PERMISSION_USER_EVENTS = "user_events";
	public static final String PERMISSION_USER_PHOTOS = "user_photos";
	public static final String PERMISSION_USER_STATUS = "user_status";
	public static final String PERMISSION_FRIENDS_ACTIVITIES = "friends_activities";
	public static final String PERMISSION_FRIENDS_LIKES = "friends_likes";
	public static final String PERMISSION_FRIENDS_EVENTS = "friends_events";
	public static final String PERMISSION_FRIENDS_PHOTOS = "friends_photos";
	public static final String PERMISSION_FRIENDS_STATUS = "friends_status";

	private static Log log = Log.get(Facebook.class);

	private OAuthService oauthService;
	private String callbackUri;
	private LoginDataProvider oauthApiKey;

	public Facebook(LoginDataProvider oauthApiKey, String callbackUri) {
		this.oauthApiKey = oauthApiKey;
		this.callbackUri = callbackUri;
	}

	public static String getProfileUrl(String id) {
		return "https://www.facebook.com/" + id;
	}

	public static String getProfilePictureUrl(String id) {
		return getGraphUrl(id + "/picture");
	}

	public Person loadPerson(String oauthAccessToken, String id) {
		JsonObject json = loadJson(oauthAccessToken, id);
		if (json == null) return null;
		return new Person(json);
	}

	public List<Reference> loadMeFriends(String oauthAccessToken) {
		JsonObject json = loadJson(oauthAccessToken, "me/friends");
		List<JsonObject> data = json.getArrayOfObjects("data");
		List<Reference> friends = new ArrayList<Reference>(data.size());
		for (JsonObject jReference : data) {
			friends.add(new Reference(jReference));
		}
		return friends;
	}

	public List<Like> loadMeLikes(String oauthAccessToken, Date deadline) {
		JsonObject json = loadJson(oauthAccessToken, "me/likes");
		List<JsonObject> data = json.getArrayOfObjects("data");
		List<Like> likes = new ArrayList<Like>(data.size());
		for (JsonObject jLike : data) {
			String id = jLike.getString("id");
			if (id == null) {
				log.warn("Element has no id:", jLike);
				continue;
			}
			Like like = new Like(jLike);
			if (deadline != null) {
				DateAndTime createdTime = like.getCreatedTime();
				if (createdTime == null) continue;
				if (createdTime.isBefore(deadline)) continue;
			}
			JsonObject jSubject = loadJson(oauthAccessToken, id);
			jLike.put("id__loaded", jSubject);
			likes.add(like);
		}
		return likes;
	}

	public Person loadMe(String oauthAccessToken) {
		JsonObject json = loadJson(oauthAccessToken, "me");
		return new Person(json);
	}

	public MeFeed loadMeFeed(String oauthAccessToken) {
		JsonObject json = loadJson(oauthAccessToken, "me/feed");
		return new MeFeed(json);
	}

	public JsonObject loadJson(String oauthAccessToken, String id) {
		JsonObject json = OAuth
				.loadUrlAsJson(getOauthService(), new LoginData(oauthAccessToken, null), getGraphUrl(id));
		log.info("Loaded", id, "->", json.toFormatedString());
		return json;
	}

	private static String getGraphUrl(String id) {
		return "https://graph.facebook.com/" + id;
	}

	public String createAccessToken(String code) {
		return OAuth.createAccessToken(getOauthService(), null, code).getLogin();
	}

	public String getUserOAuthUrl(Collection<String> permissions) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://www.facebook.com/dialog/oauth");
		sb.append("?client_id=").append(oauthApiKey.getLoginData().getLogin());
		if (!permissions.isEmpty()) sb.append("&scope=").append(Str.concat(permissions, ","));
		sb.append("&redirect_uri=").append(Str.encodeUrlParameter(callbackUri));
		return sb.toString();
	}

	public String getCallbackUri() {
		return callbackUri;
	}

	private OAuthService getOauthService() {
		if (oauthService == null) oauthService = OAuth.createService(FacebookApi.class, oauthApiKey, callbackUri);
		return oauthService;
	}

	// --- helper ---

	public static java.util.Date parseDate(String s) {
		if (s == null) return null;
		try {
			return DATE_FORMAT.parse(s);
		} catch (java.text.ParseException ex) {
			throw new RuntimeException("Parsing date with format \"" + DATE_FORMAT.toString() + "\" failed: " + s, ex);
		}
	}

}
