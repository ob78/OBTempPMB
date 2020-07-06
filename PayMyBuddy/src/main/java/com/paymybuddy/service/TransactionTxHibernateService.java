package com.paymybuddy.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Transaction;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTransactionsManagerHibernateImpl;

public class TransactionTxHibernateService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionTxHibernateService.class);

	private static String persistence = "hibernate.cfg.xml";

	private RepositoryTransactionsManagerHibernateImpl repositoryTransactionsManager = RepositoryTransactionsManagerHibernateImpl
			.getRepositoryManagerHibernateImpl(persistence);

	private IUtilisateurRepository utilisateurRepository = RepositoryFactory
			.getUtilisateurRepository(repositoryTransactionsManager);

	private ITransactionRepository transactionRepository = RepositoryFactory
			.getTransactionRepository(repositoryTransactionsManager);

	public List<Transaction> getAllTransactions(String utilisateurEmail) {

		List<Transaction> transactions = new ArrayList<>();

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			if (utilisateurRepository.read(utilisateurEmail) == null) {
				logger.error("Get all transctions : Utilisateur {} does not exist", utilisateurEmail);
				// return null;
			} else {
				transactions = transactionRepository.getTransactions(utilisateurEmail);

				repositoryTransactionsManager.commitTransaction();

				logger.info("Get all transctions for Utilisateur {} : success", utilisateurEmail);
			}
		} catch (Exception e) {
			logger.error("Get all transctions for Utilisateur {} : error", utilisateurEmail);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return transactions;
	}

	public boolean makeATransacton(String utilisateurEmail, String connectionEmail, Double montant) {

		boolean transactionDone = false;

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			if (utilisateurRepository.read(utilisateurEmail) == null) {
				logger.error("Make a transaction : Utilisateur initiateur {} does not exist", utilisateurEmail);
			} else if (utilisateurRepository.read(connectionEmail) == null) {
				logger.error("Make a transaction : Utilisateur contrepartie {} does not exist", connectionEmail);
			} else {
				Utilisateur utilisateur = utilisateurRepository.read(utilisateurEmail);
				Set<Utilisateur> utilisateurConnections = new HashSet<>();
				utilisateurConnections = utilisateur.getConnection();

				Utilisateur connection = utilisateurRepository.read(connectionEmail);
				if (!utilisateurConnections.contains(connection)) {
					logger.error("Make a transaction : Utilisateur {} not connected", utilisateurEmail);
				} else if (utilisateur.getSolde() < montant) {
					logger.error("Make a transaction : Utilisateur {} solde not sufficient", utilisateurEmail);
				} else {
					utilisateur.setSolde(utilisateur.getSolde() - montant);
					connection.setSolde(connection.getSolde() + montant);
					utilisateurRepository.update(utilisateur);
					utilisateurRepository.update(connection);

					Transaction transaction = new Transaction();
					transaction.setInitiateur(utilisateur);
					transaction.setContrepartie(connection);
					transaction.setMontant(montant);

					transactionRepository.create(transaction);

					repositoryTransactionsManager.commitTransaction();

					logger.info(
							"Make a transaction by Utilisateur intitateur {} to : Utilisateur contraprtie {} for amount = {} : done",
							utilisateurEmail, connectionEmail, montant);

					transactionDone = true;
				}
			}
		} catch (Exception e) {
			logger.error("Make a transaction by Utilisateur intitateur {} : error", utilisateurEmail);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return transactionDone;

	}

}
