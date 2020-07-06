package com.paymybuddy.repositorytransactionsmanager;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Class in charge of the Tx management, Hibernate implementation.
 */
public class RepositoryTxManagerHibernate {

	private Session currentSession;

	private Transaction currentTx;

	private String hibernateConfigurationFile;

	private static RepositoryTxManagerHibernate repositoryTxManagerHibernate = null;

	private RepositoryTxManagerHibernate(String configFile) {
		this.hibernateConfigurationFile = configFile;
	}

	/**
	 * Create an instance of RepositoryTxManagerHibernate, if not already exist.
	 * 
	 * @param configurationFile The path of the Hibernate configuration file
	 * 
	 * @return The RepositoryTxManagerHibernate
	 */
	public static RepositoryTxManagerHibernate getRepositoryTxManagerHibernate(String configurationFile) {
		if (repositoryTxManagerHibernate == null) {
			repositoryTxManagerHibernate = new RepositoryTxManagerHibernate(configurationFile);
			return repositoryTxManagerHibernate;
		} else {
			return repositoryTxManagerHibernate;
		}
	}

	// private static SessionFactory getSessionFactory() {
	/**
	 * Create an instance of Hibernate SessionFactory.
	 * 
	 * @param configurationFile The path of the Hibernate configuration file
	 * 
	 * @return The SessionFactory
	 */
	private SessionFactory getSessionFactory() {

		File configFile = new File(hibernateConfigurationFile);

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(configFile).build();

		SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();

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
	}

	/**
	 * Commit Tx and close current Session.
	 */
	public void commitTxAndCloseCurrentSessionWithTx() {
		currentTx.commit();
		currentSession.close();
	}

	/**
	 * Rollback Tx and close current Session.
	 */
	public void rollbackTxAndCloseCurrentSessionwithTx() {
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