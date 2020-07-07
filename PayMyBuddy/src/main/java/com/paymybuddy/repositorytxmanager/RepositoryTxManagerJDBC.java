package com.paymybuddy.repositorytxmanager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class in charge of the Tx management, JDBC implementation.
 */
public class RepositoryTxManagerJDBC {

	private Connection currentConnection;
	
	private static RepositoryTxManagerJDBC repositoryTxManagerJDBC = null;
		
	// 	private Transaction currentTransaction;
		
	private static final Logger logger = LoggerFactory.getLogger(RepositoryTxManagerJDBC.class);

	private static final String PROPERTY_URL = "url";
	private static final String PROPERTY_USER_NAME = "username";
	private static final String PROPERTY_PASSWORD = "password";

	private String url;
	private String username;
	private String password;

	private RepositoryTxManagerJDBC(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}


	/**
	 * Return an instance of a JDBC repository configuration if it does not already
	 * exist.
	 * 
	 * @param propertiesFilePath The path and filename to the file containing
	 *                           properties for the connection to the database
	 * 
	 * @return An instance of a JDBC repository configuration
	 */
	public static RepositoryTxManagerJDBC getRepositoryTxManagerJDBC(String configurationFile) {
		if (repositoryTxManagerJDBC == null) {
			repositoryTxManagerJDBC = getRepositoryConfigurationInstance(configurationFile);
		}
		return repositoryTxManagerJDBC;
	}

	private static RepositoryTxManagerJDBC getRepositoryConfigurationInstance(String propertiesFilePath) {
		Properties properties = new Properties();

		String url = null;
		String username = null;
		String password = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream propertiesFile = classLoader.getResourceAsStream(propertiesFilePath);

		if (propertiesFile == null) {
			logger.error("Le fichier properties {} est introuvable.", propertiesFilePath);
		}

		try {
			properties.load(propertiesFile);
			url = properties.getProperty(PROPERTY_URL);
			username = properties.getProperty(PROPERTY_USER_NAME);
			password = properties.getProperty(PROPERTY_PASSWORD);
		} catch (IOException e) {
			logger.error("Impossible de charger le fichier properties {}", propertiesFilePath);
		}

		RepositoryTxManagerJDBC repositoryManagerJDBCImpl = new RepositoryTxManagerJDBC(url, username,
				password);

		return repositoryManagerJDBCImpl;
	}

	/**
	 * Return a JDBC connection to the database.
	 * 
	 * @return A connection to the database
	 */
	public Connection getConnection() {
		try {
			/*
			boolean auto = postgreCon.getAutoCommit();
			postgreCon.setAutoCommit(false);
			*/
			currentConnection = DriverManager.getConnection(url, username, password);
			currentConnection.setAutoCommit(false);
			
			return currentConnection;
		} catch (SQLException e) {
			logger.error("Impossible d'obtenir la connexion à la base de donnée.", e);
			return null;
		}
	}

	public void closeCurrentConnection() {
		try {
			currentConnection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void commitTx() {
		try {
			currentConnection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void rollbackTx() {
		try {
			currentConnection.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
