package ilarkesto.integration.facebook;

import ilarkesto.base.Utl;
import ilarkesto.core.base.Str;

import java.util.Collection;

public class Facebook {

	public static final String PERMISSION_READ_STREAM = "read_stream";
	public static final String PERMISSION_USER_ACTIVITIES = "user_activities";

	public static void main(String[] args) {
		String url = getUserOAuthUrl("", "", Utl.toList(PERMISSION_USER_ACTIVITIES));
	}

	public static String getUserOAuthUrl(String appId, String redirectUri, Collection<String> permissions) {
		StringBuilder sb = new StringBuilder();
		sb.append("https://www.facebook.com/dialog/oauth");
		sb.append("?client_id=").append(appId);
		sb.append("&redirect_uri=").append(redirectUri);
		if (!permissions.isEmpty()) sb.append("&scope=").append(Str.concat(permissions, ","));
		return sb.toString();
	}

}
