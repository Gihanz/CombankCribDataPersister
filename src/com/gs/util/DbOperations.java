package com.gs.util;

import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gs.encrypt.CribEncryption;

public class DbOperations {
		
	private static String JDBC_URL;
	private static String JDBC_CLASS;
	private static String DBUSERNAME;
	private static String DBPASSWORD;
	public static Logger log = Logger.getLogger(DbOperations.class);
	
	public DbOperations() {
	    try {
	    	PropertyReader pr = new PropertyReader();
	    	Properties prop = pr.loadPropertyFile();
	    	JDBC_URL = prop.getProperty("JDBC_URL");
	    	JDBC_CLASS = prop.getProperty("JDBC_CLASS");
	    	DBUSERNAME = prop.getProperty("DBUSERNAME");
	    	DBPASSWORD = prop.getProperty("DBPASSWORD");	        
		} catch(Exception e) {
			System.out.println("Error DbOperations : " +e.fillInStackTrace());
			log.info("Error DbOperations : " +e.fillInStackTrace());
		}
	}
	
	// Insert H2H Response to DB.
	public void insertH2HResponseToDB(String response, int requestDetailId, String username, Connection conn) throws SQLException {
			
		PreparedStatement pstmt = null;

		log.info("Inserting H2H response to database...");
		pstmt = conn.prepareStatement("insert into CRIB_H2H_RESPONSE (REQUEST_DETAIL_ID, RESPONSE, IS_ACTIVE, CREATED, CREATED_BY) values (?, ?, ?, ?, ?)");
		pstmt.setInt(1, requestDetailId);
		Reader reader = new StringReader(response);
		pstmt.setCharacterStream(2, reader, response.length());
		pstmt.setInt(3, 1);
		pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(5, username);
		pstmt.executeUpdate();
			
		conn.commit();
		pstmt.close();
			
		System.out.println("H2H response inserted successfully.");
		log.info("H2H response inserted successfully.");	
	}
			
	// Get connection to DB.
	public Connection getConnection(String decryptionKey) {
		
		Connection connection = null;
        try {        	
        	CribEncryption cribEncryptor = new CribEncryption();
        	String dbUsername = cribEncryptor.decryptorSHA(DBUSERNAME, decryptionKey);
        	String dbPassword = cribEncryptor.decryptorSHA(DBPASSWORD, decryptionKey);
        	
            Class.forName(JDBC_CLASS);
            connection = DriverManager.getConnection(JDBC_URL, dbUsername, dbPassword);
            if(connection!=null){
            	connection.setAutoCommit(false);
                System.out.println("Connected to database successfully.");
                log.info("Connected to database successfully.");
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            log.info("Error (Unable to connect database) : " +e.fillInStackTrace());
        }

        return connection;
	}
	
	// Disconnect from DB.
	public void closeConnection(Connection conn) {
		
        try {
            if(conn!=null){
            	conn.close();
                System.out.println("Database connection disconnected.");
                log.info("Database connection disconnected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("Error while closing database connection: " +e.fillInStackTrace());
        }
	}
		 
}
