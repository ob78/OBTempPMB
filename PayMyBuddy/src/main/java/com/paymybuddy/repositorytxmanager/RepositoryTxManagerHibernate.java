package com.paymybuddy.repositorytxmanager;

import java.io.File;
import java.sql.Connection;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class in charge of the Tx management with Hibernate implementation.
 */
public class RepositoryTxManagerHibernate {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryTxManagerHibernate.class);

	private Session currentSession;

	private Transaction currentTx;

	private String paymybuddyPropertiesFile;

	private static RepositoryTxManagerHibernate repositoryTxManagerHibernate = null;

	private RepositoryTxManagerHibernate(String paymybuddyPropertiesFile) {
		this.paymybuddyPropertiesFile = paymybuddyPropertiesFile;
	}

	/**
	 * Create an instance of RepositoryTxManagerHibernate, if not already exist.
	 * 
	 * @param paymybuddyPropertiesFile The path of the paymybuddy properties file
	 * 
	 * @return The RepositoryTxManagerHibernate
	 */
	public static RepositoryTxManagerHibernate getRepositoryTxManagerHibernate(String paymybuddyPropertiesFile) {

		if (repositoryTxManagerHibernate == null) {

			repositoryTxManagerHibernate = new RepositoryTxManagerHibernate(paymybuddyPropertiesFile);

			logger.info("Creation of Tx Hibernate manager : OK");
		}

		return repositoryTxManagerHibernate;
	}

	/**
	 * Create an instance of Hibernate SessionFactory.
	 * 
	 * @param configurationFile The path of the paymybuddy properties file
	 * 
	 * @return The SessionFactory
	 */
	private SessionFactory getSessionFactory() {

		Properties paymybuddyProperties = new Properties();
		try {
			paymybuddyProperties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(paymybuddyPropertiesFile));
		} catch (Exception e) {
			logger.error("Error during load of paymybuddy properties file", e);
		}
		paymybuddyProperties.put(Environment.SHOW_SQL, "true");
		paymybuddyProperties.put(Environment.FORMAT_SQL, "true");
		paymybuddyProperties.put(Environment.DRIVER, "org.postgresql.Driver");
		paymybuddyProperties.put(Environment.HBM2DDL_AUTO, "none");
		paymybuddyProperties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
		paymybuddyProperties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		paymybuddyProperties.put(Environment.C3P0_MAX_SIZE, "300");
		paymybuddyProperties.put(Environment.POOL_SIZE, "300");

		/*
		 * File configFile = new File(hibernateConfigurationFile);
		 * 
		 * final StandardServiceRegistry registry = new
		 * StandardServiceRegistryBuilder().configure(configFile).build();
		 * 
		 * SessionFactory sessionFactory = new
		 * MetadataSources(registry).buildMetadata().buildSessionFactory();
		 */

		Configuration configuration = new Configuration().setProperties(paymybuddyProperties);
		// .addClass(com.paymybuddy.entities.Transaction.class)
		// .addClass(com.paymybuddy.entities.Utilisateur.class)
		// .setProperties(dbConnectionProperties);
		// .configure(hibernateConfigurationFile);

		// SessionFactory sessionFactory = new
		// Configuration().mergeProperties(dbConnectionProperties).configure("src/test/resources/hibernate.cfg.xml").buildSessionFactory();

		SessionFactory sessionFactory = configuration.addAnnotatedClass(com.paymybuddy.entities.Transaction.class)
				.addAnnotatedClass(com.paymybuddy.entities.Utilisateur.class).buildSessionFactory();

		return sessionFactory;
	}

	/**
	 * Open a Hibernate Session.
	 */
	public Session openCurrentSession() {
		currentSession = getSessionFactory().openSession();
		return currentSession;
	}

	/**
	 * Open a Hibernate Session with Tx.
	 */
	public Session openCurrentSessionWithTx() {
		currentSession = getSessionFactory().openSession();
		currentTx = currentSession.beginTransaction();
		return currentSession;
	}

	/**
	 * Close current Hibernate Session.
	 */
	public void closeCurrentSession() {
		currentSession.close();
		logger.info("Session closed");
	}

	/**
	 * Commit Tx and close current Session.
	 */
	public void commitTxAndCloseCurrentSession() {
		currentTx.commit();
		currentSession.close();
		logger.info("Session with Tx closed");
	}

	/**
	 * Rollback Tx and close current Session.
	 */
	public void rollbackTxAndCloseCurrentSession() {
		currentTx.rollback();
		currentSession.close();
	}

	/**
	 * Commit Tx.
	 */
	public void commitTx() {
		currentTx.commit();
	}

	/**
	 * Rollback Tx.
	 */
	public void rollbackTx() {
		currentTx.rollback();
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public Transaction getCurrentTx() {
		return currentTx;
	}

	public void setCurrentTx(Transaction currentTx) {
		this.currentTx = currentTx;
	}
}
