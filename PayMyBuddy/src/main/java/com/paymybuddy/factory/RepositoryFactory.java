package com.paymybuddy.factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repository.TransactionRepositoryJdbcImpl;
import com.paymybuddy.repository.TransactionRepositoryJpaImpl;
import com.paymybuddy.repository.TransactionRepositoryJpaImpl2;
import com.paymybuddy.repository.UtilisateurRepositoryJdbcImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaImpl2;

public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);
	
	private static ITransactionRepository transactionRepository = null;

	public static ITransactionRepository getTransactionRepository(String repositoryName, String properties) {

		if (repositoryName.equals("jdbc")) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);
						
			transactionRepository = new TransactionRepositoryJdbcImpl(repositoryConfiguration);

			logger.info("Factory : Creation JDBC Transaction Repository OK");
		
		} else if (repositoryName.equals("jpa")) {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			transactionRepository = new TransactionRepositoryJpaImpl(entityManagerFactory);

			logger.info("Factory : Creation JPA Transaction Repository OK");
			
		} else {
			
			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			transactionRepository = new TransactionRepositoryJpaImpl(entityManagerFactory);

			logger.info("Factory : Transaction Repository requested does not exist -> Creation JPA Transaction Repository by default");
			
		}

		return transactionRepository;
	}

	private static IUtilisateurRepository utilisateurRepository = null;

	public static IUtilisateurRepository getUtilisateurRepository(String repositoryName, String properties) {

		if (repositoryName.equals("jdbc")) {

			RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
					.getRepositoryConfiguration(properties);

			utilisateurRepository = new UtilisateurRepositoryJdbcImpl(repositoryConfiguration);

			logger.info("Factory : Creation JDBC Utilisateur Repository OK");
		
		}

		else if (repositoryName.equals("jpa")) {

			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			utilisateurRepository = new UtilisateurRepositoryJpaImpl(entityManagerFactory);

			logger.info("Factory : Creation JPA Utilisateur Repository OK");
			
		}
		
		else {
		
			EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

			utilisateurRepository = new UtilisateurRepositoryJpaImpl(entityManagerFactory);

			logger.info("Factory : Transaction Repository requested does not exist -> Creation JPA Utilisateur Repository by default");
			
		}
		
		return utilisateurRepository;
	}

}