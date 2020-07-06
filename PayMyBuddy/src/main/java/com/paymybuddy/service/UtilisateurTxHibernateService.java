package com.paymybuddy.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTxManagerHibernate;

public class UtilisateurTxHibernateService {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurTxHibernateService.class);

	private static String hibernateConfigurationFile = "hibernate.cfg.xml";

	private RepositoryTxManagerHibernate repositoryTxManager = RepositoryTxManagerHibernate
			.getRepositoryManagerHibernateImpl(hibernateConfigurationFile);

	private IUtilisateurRepository utilisateurRepository = RepositoryFactory
			.getUtilisateurRepository(repositoryTxManager);

	public boolean registerToApplication(String email, String password) {

		boolean utilisateurRegistered = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			if (utilisateurRepository.read(email) != null) {
				logger.error("Registration : Utilisateur {} already exist", email);
			} else {
				Utilisateur utilisateurToCreate = new Utilisateur();
				utilisateurToCreate.setEmail(email);
				utilisateurToCreate.setPassword(password);
				utilisateurToCreate.setSolde(0d);

				utilisateurRepository.create(utilisateurToCreate);

				repositoryTxManager.commitTx();

				logger.info("Registration : Utilisateur {} registered", email);

				utilisateurRegistered = true;
			}

		} catch (Exception e) {
			logger.error("Registration : Error in Utilisateur {} registration", email);

			repositoryTxManager.rollbackTx();
		} finally {
			logger.error("Closing current session");

			repositoryTxManager.closeCurrentSession();
		}

		return utilisateurRegistered;
	}

	public boolean connectToApplication(String email, String password) {

		boolean utilisateurConnected = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			if (utilisateurRepository.read(email) == null) {
				logger.error("Connection : Utilisateur {} does not exist", email);
			} else if (!utilisateurRepository.read(email).getPassword().equals(password)) {
				logger.error("Connection : Utilisateur {} wrong password", email);
			} else {
				repositoryTxManager.commitTx();

				logger.info("Connection : Utilisateur {} connected", email);

				utilisateurConnected = true;
			}
		} catch (Exception e) {
			logger.error("Connection : Error in Utilisateur {} connection", email);

			repositoryTxManager.rollbackTx();
		} finally {
			logger.error("Closing current session");

			repositoryTxManager.closeCurrentSession();
		}

		return utilisateurConnected;

	}

	public boolean wireToAccount(String email, Double amount) {

		boolean wireToAccountDone = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			Utilisateur utilisateurToUpdate = utilisateurRepository.read(email);
			if (utilisateurToUpdate == null) {
				logger.error("Wire to account : Utilisateur {} does not exist", email);
			} else {

				Double oldSolde = utilisateurToUpdate.getSolde();
				Double newSolde = oldSolde + amount;
				utilisateurToUpdate.setSolde(newSolde);

				// utilisateurToUpdate.setSolde(utilisateurToUpdate.getSolde()+amount);

				utilisateurRepository.update(utilisateurToUpdate);

				repositoryTxManager.commitTx();

				logger.info("Wire to account by Utilisateur {} for Amount {} : done", email, amount);

				wireToAccountDone = true;
			}
		} catch (Exception e) {
			logger.error("Wire to account : Error in Utilisateur {} wire to account", email);

			repositoryTxManager.rollbackTx();
		} finally {
			logger.error("Closing current session");

			repositoryTxManager.closeCurrentSession();
		}

		return wireToAccountDone;
	}

	public boolean withdrawalFromAccount(String email, Double amount) {

		boolean withdrawalFromAccountDone = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			Utilisateur utilisateurToUpdate = utilisateurRepository.read(email);
			if (utilisateurToUpdate == null) {
				logger.error("Withdrawal from account : Utilisateur {} does not exist", email);
			} else {

				Double oldSolde = utilisateurToUpdate.getSolde();

				if (oldSolde < amount) {
					logger.error("Withdrawal from account : Utilisateur {} solde = {} not sufficient for amount = {}",
							email, oldSolde, amount);
				} else {
					Double newSolde = oldSolde - amount;

					utilisateurToUpdate.setSolde(newSolde);

					utilisateurRepository.update(utilisateurToUpdate);

					repositoryTxManager.commitTx();

					logger.info("Withdrawal from account by Utilisateur {} for Amount = {} done", email, amount);

					withdrawalFromAccountDone = true;
				}
			}
		} catch (Exception e) {
			logger.error("Withdrawal from account : Error in Utilisateur {} withdrawal from account", email);

			repositoryTxManager.rollbackTx();
		} finally {
			logger.error("Closing current session");

			repositoryTxManager.closeCurrentSession();
		}

		return withdrawalFromAccountDone;
	}

	public boolean addConnection(String utilisateurEmail, String connectionEmail) {

		boolean connectionAdded = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			Utilisateur utilisateurToAddConnection = utilisateurRepository.read(utilisateurEmail);
			Utilisateur newConnection = utilisateurRepository.read(connectionEmail);

			if (utilisateurToAddConnection == null) {
				logger.error("Add a connection : Utilisateur {} does not exist", utilisateurEmail);
			} else if (newConnection == null) {
				logger.error("Add a connection : Connection {} does not exist", connectionEmail);
			} else if (utilisateurEmail.equals(connectionEmail)) {
				logger.error("Add a connection : Utilisateur {} same as connection to add", utilisateurEmail);
			} else {
				Set<Utilisateur> utilisateurConnections = new HashSet<>();
				utilisateurConnections = utilisateurToAddConnection.getConnection();

				if (utilisateurConnections.contains(newConnection)) {
					logger.error("Add a conection : Utilisateur {} has already Connection {}", utilisateurEmail,
							connectionEmail);
				} else {
					utilisateurConnections.add(newConnection);
					utilisateurToAddConnection.setConnection(utilisateurConnections);
					utilisateurRepository.addConnection(utilisateurToAddConnection, newConnection);
					// utilisateurRepository.update(utilisateur);

					repositoryTxManager.commitTx();

					logger.info("Add a conection : Utilisateur {} Connection {} added", utilisateurEmail,
							connectionEmail);

					connectionAdded = true;
				}
			}
		} catch (Exception e) {
			logger.error("Add a connection : Error in Utilisateur {} add connection", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {
			logger.error("Closing current session");

			repositoryTxManager.closeCurrentSession();
		}

		return connectionAdded;
	}

}
