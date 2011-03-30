package ilarkesto.integration.twitter;

import ilarkesto.auth.LoginData;
import ilarkesto.auth.LoginDataProvider;
import ilarkesto.base.Str;
import ilarkesto.base.Sys;
import ilarkesto.core.logging.Log;
import ilarkesto.integration.jdom.JDom;
import ilarkesto.integration.oauth.OAuth;
import ilarkesto.io.IO;
import ilarkesto.io.StringOutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Response;
import org.scribe.oauth.OAuthService;

public class Twitter {

	public static void main(String[] args) {
		Sys.setHttpProxy("webproxy.i001.finanzit.sko.de", 8081);
		Twitter twitter = new Twitter(new LoginData("lsNPMTQLKuSUlDMjAfRudg", ""));
		log.info(twitter.friendsTimeline(new LoginData("15586763-CP8HLDWt6OMzhwK8Ut6YiqhGL453YaVQi94clTqra", ""), 10));
	}

	static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
	private static final Log log = Log.get(Twitter.class);

	private LoginDataProvider oauthApiKey;
	private OAuthService oauthService;

	public Twitter(LoginDataProvider oauthApiKey) {
		this.oauthApiKey = oauthApiKey;
	}

	public synchronized Document loadUrlAsXml(LoginDataProvider oauthAccessToken, String url) {
		Response response = OAuth.loadUrl(getOauthService(), oauthAccessToken, url);
		Document document = JDom.createDocumentFromStream(response.getStream());
		return document;
	}

	public List<TwitterStatus> friendsTimeline(LoginDataProvider oauthAccessToken, Integer count) {
		log.debug("Loading Twitter statuses for", oauthAccessToken.getLoginData().getLogin());
		String url = "http://twitter.com/statuses/friends_timeline.xml";
		if (count != null) {
			url += "?count=" + count;
		}
		Document doc = loadUrlAsXml(oauthAccessToken, url);

		List<TwitterStatus> ret = new ArrayList<TwitterStatus>();
		Element root = doc.getRootElement();
		for (Element eStatus : (List<Element>) root.getChildren("status")) {
			ret.add(new TwitterStatus(oauthAccessToken, eStatus));
		}

		log.info("Statuses loaded:", ret);

		return ret;
	}

	public List<TwitterStatus> userTimeline(LoginDataProvider oauthAccessToken, Integer count) {
		log.debug("Loading statuses for", oauthAccessToken.getLoginData().getLogin());
		String url = "http://twitter.com/statuses/user_timeline.xml";
		if (count != null) {
			url += "?count=" + count;
		}
		Document doc = loadUrlAsXml(oauthAccessToken, url);

		List<TwitterStatus> ret = new ArrayList<TwitterStatus>();
		Element root = doc.getRootElement();
		for (Element eStatus : (List<Element>) root.getChildren("status")) {
			ret.add(new TwitterStatus(oauthAccessToken, eStatus));
		}

		return ret;
	}

	public void destroyOlderStatuses(LoginDataProvider oauthAccessToken, int maxAgeInDays) {
		log.debug("Destroying statuses older then", maxAgeInDays, "days for", oauthAccessToken.getLoginData()
				.getLogin());
		for (TwitterStatus status : userTimeline(oauthAccessToken, null)) {
			if (status.getCreatedAt().getPeriodToNow().toDays() > maxAgeInDays) {
				destroyStatus(status);
			}
		}
	}

	@Deprecated
	public synchronized static TwitterStatus postStatus(LoginDataProvider login, String text) {
		LoginData loginData = login.getLoginData();
		log.info("Updating status for", loginData.getLogin(), "->", text);
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", text);
		String result;
		try {
			result = IO.postAndGetResult("http://twitter.com/statuses/update.xml", params, IO.UTF_8,
				loginData.getLogin(), loginData.getPassword());
		} catch (Throwable ex) {
			throw new RuntimeException("Senden der Update-Nachricht zu Twitter (" + login.getLoginData().getLogin()
					+ ") fehlgeschlagen: " + Str.getRootCauseMessage(ex), ex);
		}
		log.debug("update result:", result);
		Document doc = JDom.createDocument(result);
		TwitterStatus status = new TwitterStatus(login, doc.getRootElement());
		return status;
	}

	@Deprecated
	public synchronized static void destroyStatus(TwitterStatus status) {
		LoginData loginData = status.login.getLoginData();
		int rc;
		StringOutputStream response = new StringOutputStream();
		try {
			rc = IO.httpPOST("http://twitter.com/statuses/destroy/" + status.getId() + ".xml", loginData.getLogin(),
				loginData.getPassword(), null, response);
		} catch (Throwable ex) {
			throw new RuntimeException("Destroying twitter status <" + status + "> failed: "
					+ Str.getRootCauseMessage(ex));
		}

		if (rc != 200)
			throw new RuntimeException("Destroying twitter status <" + status + "> failed. HTTP Code " + rc + ":\n"
					+ response);
		log.info("Deleted status:", status);
	}

	public OAuthService getOauthService() {
		if (oauthService == null) oauthService = OAuth.createService(TwitterApi.class, oauthApiKey, null);
		return oauthService;
	}

}
