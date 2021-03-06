package com.paymybuddy.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.repository.IUtilisateurRepository;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the services related to users using Hibernate Tx management.
 */
public class UtilisateurTxHibernateService {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurTxHibernateService.class);

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepository;

	public UtilisateurTxHibernateService(RepositoryTxManagerHibernate repositoryTxManager,
			IUtilisateurRepository utilisateurRepository) {
		super();
		this.repositoryTxManager = repositoryTxManager;
		this.utilisateurRepository = utilisateurRepository;
	}

	/**
	 * Method managing the registration of a user to the application.
	 * 
	 * @param utilisateurEmail The email of the user to register
	 * 
	 * @param password         The password of the user to register
	 * 
	 * @return True if the registration has been successfully executed, false if it
	 *         has failed
	 */
	public boolean registerToApplication(String utilisateurEmail, String password) {

		boolean utilisateurRegistered = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			// We check that the utilisateur with this email address is not already
			// registered in the application
			if (utilisateurRepository.read(utilisateurEmail) != null) {
				logger.error("Registration : Utilisateur {} already exist", utilisateurEmail);

			} else {
				// We create the utilisateur and save it in the database in order to register it
				// in the application
				Utilisateur utilisateurToCreate = new Utilisateur();
				utilisateurToCreate.setEmail(utilisateurEmail);
				utilisateurToCreate.setPassword(password);
				utilisateurToCreate.setSolde(0d);

				utilisateurRepository.create(utilisateurToCreate);

				repositoryTxManager.commitTx();

				logger.info("Registration : Utilisateur {} registered", utilisateurEmail);

				utilisateurRegistered = true;
			}

		} catch (Exception e) {
			logger.error("Registration : Error in Utilisateur {} registration", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return utilisateurRegistered;
	}

	/**
	 * Method managing the connection of a user to the application.
	 * 
	 * @param utilisateurEmail The email of the user to connect
	 * 
	 * @param password         The password of the user to connect
	 * 
	 * @return True if the connection has been successfully executed, false if it
	 *         has failed
	 */
	public boolean connectToApplication(String utilisateurEmail, String password) {

		boolean utilisateurConnected = false;

		try {
			repositoryTxManager.openCurrentSessionWithTx();

			// We check that the utilisateur with this email address is registered in the
			// application
			if (utilisateurRepository.read(utilisateurEmail) == null) {
				logger.error("Connection : Utilisateur {} does not exist", utilisateurEmail);

			// We check that the password is correct
			} else if (!utilisateurRepository.read(utilisateurEmail).getPassword().equals(password)) {
				logger.error("Connection : Utilisateur {} wrong password", utilisateurEmail);

			// If all is ok then the utilisateur is connected to the application
			} else {
				repositoryTxManager.commitTx();

				logger.info("Connection : Utilisateur {} connected", utilisateurEmail);

				utilisateurConnected = true;
			}
		} catch (Exception e) {
			logger.error("Connection : Error in Utilisateur {} connection", utilisateurEmail);

			repositoryTxManager.rollbackTx();
		} finally {

			repositoryTxManager.closeCurrentSession();
		}

		return utilisateurConnected;

	}

	/**
	 * Method managing the wire by a user to his account for a certain amount.
	 * 
	 * @param utilisateurEmail The email of the user for which to perform the wire
	 * 
	 * @param montant          The amount of the wire
	 * 
	 * @return True if the wire has been successfully executed, false if it has
	 *         failed
	 */
	public boolean wireToAccount(String utilisateurEmail, Double montant) {

		boolean wireToAccountDone = false;

		// We check that the amount to be wired is positive
		if (montant <= 0) {
			logger.error("Wire to account : Utilisateur {}, amount = {} must be positive", utilisateurEmail, montant);
		} else {

			try {
				repositoryTxManager.openCurrentSessionWithTx();

				Utilisateur utilisateurToUpdate = utilisateurRepository.read(utilisateurEmail);

				// We check that the utilisateur with this email address is registered in the
				// application
				if (utilisateurToUpdate == null) {
					logger.error("Wire to account : Utilisateur {} does not exist", utilisateurEmail);
				} else {

					// If all is ok, we update the utilisateur with a new solde being the old one
					// plus the amount wired
					Double oldSolde = utilisateurToUpdate.getSolde();
					Double newSolde = oldSolde + montant;
					utilisateurToUpdate.setSolde(newSolde);

					utilisateurRepository.update(utilisateurToUpdate);

					repositoryTxManager.commitTx();

					logger.info("Wire to account by Utilisateur {} for amount {} : done", utilisateurEmail, montant);

					wireToAccountDone = true;
				}
			} catch (Exception e) {
				logger.error("Wire to account : Error in Utilisateur {} wire to account", utilisateurEmail);

				repositoryTxManager.rollbackTx();
			} finally {

				repositoryTxManager.closeCurrentSession();
			}
		}

		return wireToAccountDone;
	}

	/**
	 * Method managing the withdrawal by a user from his account for a certain
	 * amount.
	 * 
	 * @param utilisateurEmail The email of the user for which to perform the
	 *                         withdrawal
	 * 
	 * @param montant          The amount of the withdrawal
	 * 
	 * @return True if the withdrawal has been successfully executed, false if it
	 *         has failed
	 */
	public boolean withdrawalFromAccount(String utilisateurEmail, Double montant) {

		boolean withdrawalFromAccountDone = false;

		// We check that the amount to be withdrawn is positive
		if (montant <= 0) {
			logger.error("Withdrawal from account : Utilisateur {}, amount = {} must be positive", utilisateurEmail,
					montant);
		} else {

			try {
				repositoryTxManager.openCurrentSessionWithTx();

				Utilisateur utilisateurToUpdate = utilisateurRepository.read(utilisateurEmail);

				// We check that the utilisateur with this email address is registered in the
				// application
				if (utilisateurToUpdate == null) {
					logger.error("Withdrawal from account : Utilisateur {} does not exist", utilisateurEmail);
				} else {

					Double oldSolde = utilisateurToUpdate.getSolde();

					// We check that the solde of the Utilisateur is sufficient to perform the
					// withdrawal
					if (oldSolde < montant) {
						logger.error(
								"Withdrawal from account : Utilisateur {} solde = {} not sufficient for amount = {}",
								utilisateurEmail, oldSolde, montant);
					} else {
						// If all is ok, we update the utilisateur with a new solde being the old one
						// minus the amount withdrawn
						Double newSolde = oldSolde - montant;

						utilisateurToUpdate.setSolde(newSolde);

						utilisateurRepository.update(utilisateurToUpdate);

						repositoryTxManager.commitTx();

						logger.info("Withdrawal from account by Utilisateur {} for amount = {} done", utilisateurEmail,
								montant);

						withdrawalFromAccountDone = true;
					}
				}
			} catch (Exception e) {
				logger.error("Withdrawal from account : Error in Utilisateur {} withdrawal from account",
						utilisateurEmail);

				repositoryTxManager.rollbackTx();
			} finally {

				repositoryTxManager.closeCurrentSession();
			}

		}

		return withdrawalFromAccountDone;
	}

	/**
	 * Method managing the addition by a user of a new connection.
	 * 
	 * @param utilisateurEmail The email of the user for which to add a new
	 *                         connection
	 * 
	 * @param connectionEmail  The email of the the connection to add
	 * 
	 * @return True if the connection add has been successfully executed, false if
	 *         it has failed
	 */
	public boolean addConnection(String utilisateurEmail, String connectionEmail) {

		boolean connectionAdded = false;

		// We check that the utilisateur and the connection to add are not
		// the same
		if (utilisateurEmail.equals(connectionEmail)) {
			logger.error("Add a connection : Utilisateur {} same as connection to add", utilisateurEmail);
		} else {

			try {
				repositoryTxManager.openCurrentSessionWithTx();

				Utilisateur utilisateurToAddConnection = utilisateurRepository.read(utilisateurEmail);
				Utilisateur newConnection = utilisateurRepository.read(connectionEmail);

				// We check that the utilisateur to which add a connection is registered in the
				// application
				if (utilisateurToAddConnection == null) {
					logger.error("Add a connection : Utilisateur {} does not exist", utilisateurEmail);

				// We check that the new connection is registered in the application
				} else if (newConnection == null) {
					logger.error("Add a connection : Connection {} does not exist", connectionEmail);

				} else {
					Set<Utilisateur> utilisateurConnections = utilisateurToAddConnection.getConnection();

					// We check that the utilisateur and the new connection are not already
					// connected
					if (utilisateurConnections!=null && utilisateurConnections.contains(newConnection)) {
						logger.error("Add a conection : Utilisateur {} has already Connection {}", utilisateurEmail,
								connectionEmail);

					// If all is ok, then we add the new connection to the utilisateur :
					} else {
						if (utilisateurConnections==null) {
							utilisateurConnections = new HashSet<>();
						}
						utilisateurConnections.add(newConnection);
						utilisateurToAddConnection.setConnection(utilisateurConnections);
						utilisateurRepository.addConnection(utilisateurToAddConnection, newConnection);

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

				repositoryTxManager.closeCurrentSession();
			}
		}

		return connectionAdded;
	}

}
