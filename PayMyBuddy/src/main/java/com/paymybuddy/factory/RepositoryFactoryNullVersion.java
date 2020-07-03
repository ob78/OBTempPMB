package com.paymybuddy.factory;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repository.TransactionRepositoryJdbcImpl;
import com.paymybuddy.repository.TransactionRepositoryJpaImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJdbcImpl;
import com.paymybuddy.repository.UtilisateurRepositoryJpaImpl;

/**
 * Class Factory in charge of the construction of Utilisateur repository and
 * Transaction repository
 */
public class RepositoryFactoryNullVersion {

	private static IUtilisateurRepository utilisateurRepository = null;

	private static ITransactionRepository transactionRepository = null;

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
			if (utilisateurRepository == null) {

				RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
						.getRepositoryConfiguration(properties);

				utilisateurRepository = new UtilisateurRepositoryJdbcImpl(repositoryConfiguration);

			}
			return utilisateurRepository;
		}

		if (repositoryName.equals("jpa")) {
			if (utilisateurRepository == null) {

				EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

				utilisateurRepository = new UtilisateurRepositoryJpaImpl(entityManagerFactory);

			}
		}
		return utilisateurRepository;
	}

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
			if (transactionRepository == null) {

				RepositoryJdbcConfiguration repositoryConfiguration = RepositoryJdbcConfiguration
						.getRepositoryConfiguration(properties);

				transactionRepository = new TransactionRepositoryJdbcImpl(repositoryConfiguration);

			}
			return transactionRepository;
		}

		if (repositoryName.equals("jpa")) {
			if (transactionRepository == null) {

				EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(properties);

				transactionRepository = new TransactionRepositoryJpaImpl(entityManagerFactory);

			}
		}
		return transactionRepository;
	}

}
