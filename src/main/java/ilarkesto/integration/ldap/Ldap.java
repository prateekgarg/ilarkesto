package ilarkesto.integration.ldap;

import ilarkesto.auth.AuthenticationFailedException;
import ilarkesto.auth.WrongPasswordException;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class Ldap {

	public static void main(String[] args) {
		System.out.println(authenticateUserGetEmail("ldap://adcsv10:389/", "bind user", "bind password", "base dn",
			"user filter", "user", "password"));
	}

	public static String authenticateUserGetEmail(String url, String bindUser, String bindPassword, String baseDn,
			String userFilterRegex, String user, String password) throws AuthenticationFailedException {
		NamingEnumeration<SearchResult> searchResultEnum;
		try {
			DirContext ctx = Ldap.createDirContext(url, bindUser, bindPassword);
			String filter = userFilterRegex == null ? user : userFilterRegex.replaceAll("%u", user);
			SearchControls cons = new SearchControls(SearchControls.SUBTREE_SCOPE, 0, 0, new String[] { "mail" }, true,
					false);
			searchResultEnum = ctx.search(baseDn, filter, cons);
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
		SearchResult searchResult = searchResultEnum.nextElement();
		if (searchResult == null) throw new AuthenticationFailedException("User does not exist.");

		try {
			Ldap.createDirContext(url, searchResult.getName() + "," + baseDn, password);
		} catch (AuthenticationException ex) {
			throw new WrongPasswordException();
		}

		try {
			Attribute mailAttribute = searchResult.getAttributes().get("mail");
			if (mailAttribute != null) return mailAttribute.get().toString();
		} catch (NamingException e) {
			// mail field not handled by ldap tree. can happen on some ldap server or with specific structures
		}
		return null;
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
