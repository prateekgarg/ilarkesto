package ilarkesto.integration.facebook;

import ilarkesto.auth.LoginData;
import ilarkesto.auth.LoginDataProvider;
import ilarkesto.base.Str;
import ilarkesto.base.Utl;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.oauth.OAuth;

import java.util.Collection;

import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;

public class Facebook {

	public static void main(String[] args) {

		Facebook facebook = new Facebook("192988147405004", new LoginData("2a66c6df5ed07c971c8bc474949b69f3", ""));

		String url = facebook.getUserOAuthUrl("https://servisto.de/organizanto/oauth?user=123",
			Utl.toList(PERMISSION_READ_STREAM, PERMISSION_USER_ACTIVITIES, PERMISSION_OFFLINE_ACCESS));
		System.out.println(url);

		// String code = "";
		// LoginData accessToken = facebook.createAccessToken(code);
		// System.out.println(accessToken.getLogin());
		// System.out.println(accessToken.getPassword());

		String accessToken = "";
		facebook.loadFeed(accessToken);
	}

	public static final String PERMISSION_READ_STREAM = "read_stream";
	public static final String PERMISSION_USER_ACTIVITIES = "user_activities";
	public static final String PERMISSION_OFFLINE_ACCESS = "offline_access";

	private static Log log = Log.get(Facebook.class);

	private OAuthService oauthService;
	private String applicationId;

	public Facebook(String applicationId, LoginDataProvider oauthApiKey) {
		this.applicationId = applicationId;
		oauthService = OAuth.createService(FacebookApi.class, oauthApiKey, "https://servisto.de/organizanto/oauth");
	}

	public void loadFeed(String oauthAccessToken) {
		String ret = OAuth.loadUrlAsString(oauthService, new LoginData(oauthAccessToken, null),
			"https://graph.facebook.com/me/feed");
		System.out.println(ret);
	}

	public String createAccessToken(String code) {
		return OAuth.createAccessToken(oauthService, null, code).getLogin();
	}

	public String getUserOAuthUrl(String redirectUri, Collection<String> permissions) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://www.facebook.com/dialog/oauth");
		sb.append("?client_id=").append(applicationId);
		if (!permissions.isEmpty()) sb.append("&scope=").append(Str.concat(permissions, ","));
		sb.append("&redirect_uri=").append(Str.encodeUrlParameter(redirectUri));
		return sb.toString();
	}

}
