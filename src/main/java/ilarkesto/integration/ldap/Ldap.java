package ilarkesto.integration.ldap;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Ldap {

	public static void main(String[] args) {
		System.out.println(authenticateUser("ldap://adcsv10:389/", "bind user", "bind password", "base dn",
			"user filter", "user", "password"));
	}

	public static boolean authenticateUser(String url, String bindUser, String bindPassword, String baseDn,
			String userFilterRegex, String user, String password) {
		NamingEnumeration<SearchResult> searchResultEnum;
		try {
			DirContext ctx = Ldap.createDirContext(url, bindUser, bindPassword);
			String filter = userFilterRegex == null ? user : userFilterRegex.replaceAll("%u", user);
			SearchControls cons = new SearchControls(SearchControls.SUBTREE_SCOPE, 0, 0, null, true, false);
			searchResultEnum = ctx.search(baseDn, filter, cons);
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
		SearchResult searchResult = searchResultEnum.nextElement();
		if (searchResult == null) return false;
		try {
			Ldap.createDirContext(url, searchResult.getName() + "," + baseDn, password);
		} catch (AuthenticationException ex) {
			return false;
		}
		return true;
	}

	public static DirContext createDirContext(String url, String user, String password) throws AuthenticationException {
		Hashtable env = new Hashtable();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// TODO depends on sun-jdk?

		env.put(Context.PROVIDER_URL, url);

		if (user != null) {
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, user);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}

		try {
			return new InitialDirContext(env);
		} catch (AuthenticationException ex) {
			throw ex;
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
	}

}
