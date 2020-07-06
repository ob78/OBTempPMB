package com.paymybuddy.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTransactionsManagerHibernateImpl;

public class UtilisateurTxHibernateService {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurTxHibernateService.class);

	private static String persistence = "hibernate.cfg.xml";

	private RepositoryTransactionsManagerHibernateImpl repositoryTransactionsManager = RepositoryTransactionsManagerHibernateImpl
			.getRepositoryManagerHibernateImpl(persistence);

	private IUtilisateurRepository utilisateurRepository = RepositoryFactory
			.getUtilisateurRepository(repositoryTransactionsManager);

	public boolean registerToApplication(String email, String password) {

		boolean utilisateurRegistered = false;

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			if (utilisateurRepository.read(email) != null) {
				logger.error("Registration : Utilisateur {} already exist", email);
			} else {
				Utilisateur utilisateurToCreate = new Utilisateur();
				utilisateurToCreate.setEmail(email);
				utilisateurToCreate.setPassword(password);
				utilisateurToCreate.setSolde(0d);

				utilisateurRepository.create(utilisateurToCreate);

				repositoryTransactionsManager.commitTransaction();

				logger.info("Registration : Utilisateur {} registered", email);

				utilisateurRegistered = true;
			}

		} catch (Exception e) {
			logger.error("Registration : Error in Utilisateur {} registration", email);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return utilisateurRegistered;
	}

	public boolean connectToApplication(String email, String password) {

		boolean utilisateurConnected = false;

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			if (utilisateurRepository.read(email) == null) {
				logger.error("Connection : Utilisateur {} does not exist", email);
			} else if (!utilisateurRepository.read(email).getPassword().equals(password)) {
				logger.error("Connection : Utilisateur {} wrong password", email);
			} else {
				repositoryTransactionsManager.commitTransaction();

				logger.info("Connection : Utilisateur {} connected", email);

				utilisateurConnected = true;
			}
		} catch (Exception e) {
			logger.error("Connection : Error in Utilisateur {} connection", email);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return utilisateurConnected;

	}

	public boolean wireToAccount(String email, Double amount) {

		boolean wireToAccountDone = false;

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			Utilisateur utilisateurToUpdate = utilisateurRepository.read(email);
			if (utilisateurToUpdate == null) {
				logger.error("Wire to account : Utilisateur {} does not exist", email);
			} else {

				Double oldSolde = utilisateurToUpdate.getSolde();
				Double newSolde = oldSolde + amount;
				utilisateurToUpdate.setSolde(newSolde);

				// utilisateurToUpdate.setSolde(utilisateurToUpdate.getSolde()+amount);

				utilisateurRepository.update(utilisateurToUpdate);

				repositoryTransactionsManager.commitTransaction();

				logger.info("Wire to account : Utilisateur {} done", email);

				wireToAccountDone = true;
			}
		} catch (Exception e) {
			logger.error("Wire to account : Error in Utilisateur {} wire to account", email);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return wireToAccountDone;
	}

	public boolean withdrawalFromAccount(String email, Double amount) {

		boolean withdrawalFromAccountDone = false;

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			Utilisateur utilisateurToUpdate = utilisateurRepository.read(email);
			if (utilisateurToUpdate == null) {
				logger.error("Withdrawal from account : Utilisateur {} does not exist", email);
			} else {

				Double oldSolde = utilisateurToUpdate.getSolde();

				if (oldSolde < amount) {
					logger.error("Withdrawal from account : Utilisateur {} solde not sufficient", email);
				} else {
					Double newSolde = oldSolde - amount;

					utilisateurToUpdate.setSolde(newSolde);

					utilisateurRepository.update(utilisateurToUpdate);

					repositoryTransactionsManager.commitTransaction();

					logger.info("Withdrawal from account : Utilisateur {} done", email);

					withdrawalFromAccountDone = true;
				}
			}
		} catch (Exception e) {
			logger.error("Withdrawal from account : Error in Utilisateur {} withdrawal from account", email);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return withdrawalFromAccountDone;
	}

	public boolean addConnection(String utilisateurEmail, String connectionEmail) {

		boolean connectionAdded = false;

		try {
			repositoryTransactionsManager.openCurrentSessionWithTransaction();

			Utilisateur utilisateurToAddConnection = utilisateurRepository.read(utilisateurEmail);
			Utilisateur newConnection = utilisateurRepository.read(connectionEmail);

			if (utilisateurToAddConnection == null) {
				logger.error("Add a conection : Utilisateur {} does not exist", utilisateurEmail);
			} else if (newConnection == null) {
				logger.error("Add a conection", connectionEmail);
			} else if (utilisateurEmail.equals(connectionEmail)) {
				logger.error("Add a conection : Utilisateur {} same as connection to add", utilisateurEmail);
			} else {
				// Utilisateur utilisateur = utilisateurRepository.read(utilisateurEmail);
				Set<Utilisateur> utilisateurConnections = new HashSet<>();
				utilisateurConnections = utilisateurToAddConnection.getConnection();

				if (utilisateurConnections.contains(newConnection)) {
					logger.error("Add a conection : Utilisateur {} connection already exist", utilisateurEmail);
				} else {
					utilisateurConnections.add(newConnection);
					utilisateurToAddConnection.setConnection(utilisateurConnections);
					utilisateurRepository.addConnection(utilisateurToAddConnection, newConnection);
					// utilisateurRepository.update(utilisateur);

					repositoryTransactionsManager.commitTransaction();

					logger.info("Add a conection : Utilisateur {} connection added", utilisateurEmail);

					connectionAdded = true;
				}
			}
		} catch (Exception e) {
			logger.error("Add a conection : Error in Utilisateur {} add conection", utilisateurEmail);

			repositoryTransactionsManager.rollbackTransaction();
		} finally {
			logger.error("Closing current session");

			repositoryTransactionsManager.closeCurrentSession();
		}

		return connectionAdded;
	}

}
