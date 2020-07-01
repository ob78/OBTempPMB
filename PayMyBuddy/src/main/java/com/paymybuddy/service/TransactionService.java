package com.paymybuddy.service;

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

public class TransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	String propertiesFilePath = "paymybuddy.properties";

	String persistence = "persistencePostgreProd";

	private IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository("jpa",
			persistence);

	private ITransactionRepository transactionRepository = RepositoryFactory.getTransactionRepository("jpa",
			persistence);

	public List<Transaction> getAllTransactions(String utilisateurEmail) {

		if (utilisateurRepository.read(utilisateurEmail) == null) {
			logger.error("WireToConnection : Utilisateur {} does not exist", utilisateurEmail);
			return null;
		} else {
			List<Transaction> transactions = transactionRepository.getTransactions(utilisateurEmail);

			return transactions;
		}
	}

	public boolean makeATransacton(String utilisateurEmail, String connectionEmail, Double montant) {

		boolean transactionDone = false;

		if (utilisateurRepository.read(utilisateurEmail) == null) {
			logger.error("WireToConnection : Utilisateur {} does not exist", utilisateurEmail);
		} else if (utilisateurRepository.read(connectionEmail) == null) {
			logger.error("WireToConnection : Utilisateur {} does not exist", connectionEmail);
		} else {
			Utilisateur utilisateur = utilisateurRepository.read(utilisateurEmail);
			Set<Utilisateur> utilisateurConnections = new HashSet<>();
			utilisateurConnections = utilisateur.getConnection();

			Utilisateur connection = utilisateurRepository.read(connectionEmail);
			if (!utilisateurConnections.contains(connection)) {
				logger.error("WireToConnection : Utilisateur {} not connected", utilisateurEmail);
			} else if (utilisateur.getSolde() < montant) {
				logger.error("WireToConnection : Utilisateur {} solde not sufficient", utilisateurEmail);
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

				logger.info("WireToConnection : Utilisateur {} done", utilisateurEmail);
				transactionDone = true;
			}
		}

		return transactionDone;

	}

}
