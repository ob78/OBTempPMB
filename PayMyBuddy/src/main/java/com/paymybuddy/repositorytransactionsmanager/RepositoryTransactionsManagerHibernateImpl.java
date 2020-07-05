package com.paymybuddy.repositorytransactionsmanager;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class RepositoryTransactionsManagerHibernateImpl {

	private Session currentSession;

	private Transaction currentTransaction;

	private String configurationFile;
	
	private static RepositoryTransactionsManagerHibernateImpl repositoryManagerHibernateImpl = null;
	
	private RepositoryTransactionsManagerHibernateImpl(String configFile) {
		this.configurationFile = configFile;
	}
	
	public static RepositoryTransactionsManagerHibernateImpl getRepositoryManagerHibernateImpl(String configurationFile) {
		if (repositoryManagerHibernateImpl==null) {
				repositoryManagerHibernateImpl = new RepositoryTransactionsManagerHibernateImpl(configurationFile);
				return repositoryManagerHibernateImpl;
		} else {
			return repositoryManagerHibernateImpl;
		}
	}
	
	//private static SessionFactory getSessionFactory() {
	private SessionFactory getSessionFactory() {

		File configFile = new File(configurationFile);

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure(configFile).build();

		SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();

		return sessionFactory;
	}
	
	public Session openCurrentSession() {
		currentSession = getSessionFactory().openSession();
		return currentSession;
	}

	public Session openCurrentSessionWithTransaction() {
		currentSession = getSessionFactory().openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSession() {
		currentSession.close();
	}

	public void commitTransactionAndCloseCurrentSessionWithTransaction() {
		currentTransaction.commit();
		currentSession.close();
	}

	public void rollbackTransactionAndCloseCurrentSessionwithTransaction() {
		currentTransaction.rollback();
		currentSession.close();
	}

	public void commitTransaction() {
		currentTransaction.commit();
	}

	public void rollbackTransaction() {
		currentTransaction.rollback();
	}



	public Session getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public Transaction getCurrentTransaction() {
		return currentTransaction;
	}

	public void setCurrentTransaction(Transaction currentTransaction) {
		this.currentTransaction = currentTransaction;
	}
}
