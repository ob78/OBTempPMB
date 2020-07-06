package com.paymybuddy.factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repository.TransactionRepositoryJdbcImpl;
import com.paymybuddy.repository.TransactionRepositoryJdbcTxImpl;
import com.paymybuddy.repository.TransactionRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repository.TransactionRepositoryJpaImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJdbcImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJdbcTxImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaTxHibernateImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaImpl;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTxManagerHibernate;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTxManagerJDBC;

/**
 * Class Factory in charge of the construction of Utilisateur repository and
 * Transaction repository for JDBC and JPA implementations
 */
public class RepositoryFactory {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryFactory.class);

	private static ITransactionRepository transactionRepository = null;

	// Repository TRANSACTION - Factory paramétrée - Sans gestion des transactions
	// -> JDBC & JPA
	/**
	 * Create a Transaction repository.
	 * 
	 * @param repositoryName The name of the repository to create
	 * 
	 * @param properties     The path of the file containing properties for the
	 *                       repository configuration
	 * 
	 * @return The Transaction repository
	 */
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

			logger.info(
					"Factory : Transaction Repository requested does not exist -> Creation JPA Transaction Repository by default");

		}

		return transactionRepository;
	}

	// Repository TRANSACTION - Factory non paramétrée - Avec gestion des
	// transactions -> HIBERNATE
	/**
	 * Create a Transaction repository (JPA persistence and Tx managed by
	 * Hibernate).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Transaction repository
	 */
	public static ITransactionRepository getTransactionRepository(RepositoryTxManagerHibernate repositoryManger) {

		transactionRepository = new TransactionRepositoryJpaTxHibernateImpl(repositoryManger);

		logger.info("Factory : Creation JPA Transaction Repository with Hibernate Tx management : OK");

		return transactionRepository;
	}

	// Repository TRANSACTION - Factory non paramétrée - Avec gestion des
	// transactions -> JDBC
	/**
	 * Create a Transaction repository (JDBC persistence and Tx managed by JDBC).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Transaction repository
	 */
	public static ITransactionRepository getTransactionRepository(RepositoryTxManagerJDBC repositoryManger) {

		transactionRepository = new TransactionRepositoryJdbcTxImpl(repositoryManger);

		logger.info("Factory : Creation JDBC Transaction Repository with JDBC Tx management : OK");

		return transactionRepository;
	}

	private static IUtilisateurRepository utilisateurRepository = null;

	// Repository UTILISATEUR - Factory paramétrée - Sans gestion des transactions
	// -> JDBC & JPA
	/**
	 * Create a Utilisateur repository.
	 * 
	 * @param repositoryName The name of the repository to create
	 * 
	 * @param properties     The path of the file containing properties for the
	 *                       repository configuration
	 * 
	 * @return The Utilisateur repository
	 */
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

			logger.info(
					"Factory : Transaction Repository requested does not exist -> Creation JPA Utilisateur Repository by default");

		}

		return utilisateurRepository;
	}

	// Repository UTILISATEUR - Factory non paramétrée - Avec gestion des
	// transactions -> HIBERNATE
	/**
	 * Create a Utilisateur repository (JPA persistence and Tx managed by
	 * Hibernate).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Transaction repository
	 */
	public static IUtilisateurRepository getUtilisateurRepository(RepositoryTxManagerHibernate repositoryManger) {

		utilisateurRepository = new UtilisateurRepositoryJpaTxHibernateImpl(repositoryManger);

		logger.info("Factory : Creation JPA Utilisateur Repository with Hibernate Tx management : OK");

		return utilisateurRepository;
	}

	// Repository UTILISATEUR - Factory non paramétrée - Avec gestion des
	// transactions -> JDBC
	/**
	 * Create a Utilisateur repository (JDBC persistence and Tx managed by JDBC).
	 * 
	 * @param repositoryManger The repositoryManger used to manage tx
	 * 
	 * @return The Transaction repository
	 */
	public static IUtilisateurRepository getUtilisateurRepository(RepositoryTxManagerJDBC repositoryManger) {

		utilisateurRepository = new UtilisateurRepositoryJdbcTxImpl(repositoryManger);

		logger.info("Factory : Creation JDBC Utilisateur Repository with JDBC Tx management : OK");

		return utilisateurRepository;
	}

}
