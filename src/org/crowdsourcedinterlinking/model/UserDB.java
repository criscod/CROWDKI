package org.crowdsourcedinterlinking.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserDB {
	
	private Connection connect;
	private String hostname = "localhost";
	private String schema ="crowdmapinput"; 
	private String user ="root";
	private String password="rootwest2012";

	public UserDB() {

		try {

			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			connect = (Connection) DriverManager.getConnection("jdbc:mysql://"
					+ hostname + "/" + schema + "?autoReconnect=true", user,
					password);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {

		try {

			connect.close();
		} catch (Exception e) {
			System.out.println("Error");
		}

	}

	public Connection getConnection() {
		return connect;
	}



}
