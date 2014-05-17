/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package ilarkesto.jdbc;

import ilarkesto.core.logging.Log;
import ilarkesto.jdbc.Jdbc.RecordHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcConnector {

	private static Log log = Log.get(JdbcConnector.class);

	private String driver;
	private String protocol;
	private String host;
	private String port;
	private String database;
	private String login;
	private String password;

	private Connection connection;

	public JdbcConnector(String driver, String protocol, String host, String port, String database, String login,
			String password) {
		this.driver = driver;
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.database = database;
		this.login = login;
		this.password = password;
	}

	public List<Long> listColumnValuesAsLong(String table, final String column, String condition, Object... params)
			throws SQLException {
		final List<Long> ret = new ArrayList<Long>();
		String sql = "SELECT " + column + " FROM " + table;
		if (condition != null) sql += " WHERE " + condition;
		executeQuery(new RecordHandler() {

			@Override
			public void onRecord(ResultSet rs) throws SQLException {
				ret.add(rs.getLong(column));
			}
		}, sql, params);
		return ret;
	}

	public List<String> listColumnValuesAsString(String table, final String column, String condition, Object... params)
			throws SQLException {
		final List<String> ret = new ArrayList<String>();
		String sql = "SELECT " + column + " FROM " + table;
		if (condition != null) sql += " WHERE " + condition;
		executeQuery(new RecordHandler() {

			@Override
			public void onRecord(ResultSet rs) throws SQLException {
				ret.add(rs.getString(column));
			}
		}, sql, params);
		return ret;
	}

	public PreparedStatement prepareStatement(String sql, Object... params) {
		return Jdbc.prepareStatement(getConnection(), sql, params);
	}

	public void executeQuery(RecordHandler handler, String sql, Object... params) throws SQLException {
		Jdbc.executeQuery(getConnection(), handler, sql, params);
	}

	public void execute(String sql, Object... params) throws SQLException {
		Jdbc.execute(getConnection(), sql, params);
	}

	public synchronized Connection getConnection() {
		if (connection == null) {
			createConnection();
		} else {
			try {
				if (connection.isClosed()) createConnection();
				if (!connection.isValid(0)) createConnection();
			} catch (SQLException ex) {
				createConnection();
			}
		}
		return connection;
	}

	private synchronized void createConnection() {
		connection = Jdbc.createConnection(driver, protocol, host, port, database, login, password);
	}

	public synchronized void closeConnection() {
		Jdbc.closeQuiet(connection);
		connection = null;
	}

}
