package com.paymybuddy.service;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repository.IUtilisateurRepository;

public class UtilisateurService {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurService.class);

	String propertiesFilePath = "paymybuddy.properties";

	String persistence = "persistencePostgreProd";

	private IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository("jdbc",
			propertiesFilePath);

	public boolean registerToApplication(String email, String password) {

		boolean utilisateurRegistered = false;

		if (utilisateurRepository.read(email) != null) {
			logger.error("Registration : Utilisateur {} already exist", email);
		} else {
			Utilisateur utilisateurToCreate = new Utilisateur();
			utilisateurToCreate.setEmail(email);
			utilisateurToCreate.setPassword(password);
			utilisateurToCreate.setSolde(0d);

			utilisateurRepository.create(utilisateurToCreate);

			logger.info("Registration : Utilisateur {} registered", email);
			utilisateurRegistered = true;
		}

		return utilisateurRegistered;
	}

	public boolean connectToApplication(String email, String password) {

		boolean utilisateurConnected = false;

		if (utilisateurRepository.read(email) == null) {
			logger.error("Connection : Utilisateur {} does not exist", email);
		} else if (!utilisateurRepository.read(email).getPassword().equals(password)) {
			logger.error("Connection : Utilisateur {} wrong password", email);
		} else {
			logger.info("Connection : Utilisateur {} connected", email);

			utilisateurConnected = true;
		}

		return utilisateurConnected;

	}

	public boolean wireToAccount(String email, Double amount) {

		boolean wireToAccountDone = false;

		Utilisateur utilisateurToUpdate = utilisateurRepository.read(email);
		if (utilisateurToUpdate == null) {
			logger.error("WireToAccount : Utilisateur {} does not exist", email);
		} else {

			Double oldSolde = utilisateurToUpdate.getSolde();
			Double newSolde = oldSolde + amount;
			utilisateurToUpdate.setSolde(newSolde);

			// utilisateurToUpdate.setSolde(utilisateurToUpdate.getSolde()+amount);

			utilisateurRepository.update(utilisateurToUpdate);

			logger.info("WireToAccount : Utilisateur {} done", email);
			wireToAccountDone = true;
		}

		return wireToAccountDone;
	}

	public boolean withdrawalFromAccount(String email, Double amount) {

		boolean withdrawalFromAccountDone = false;

		Utilisateur utilisateurToUpdate = utilisateurRepository.read(email);
		if (utilisateurToUpdate == null) {
			logger.error("WithdrawalFromAccount : Utilisateur {} does not exist", email);
		} else {

			Double oldSolde = utilisateurToUpdate.getSolde();

			if (oldSolde < amount) {
				logger.error("WithdrawalFromAccount : Utilisateur {} solde not sufficient", email);
			} else {
				Double newSolde = oldSolde - amount;

				utilisateurToUpdate.setSolde(newSolde);

				utilisateurRepository.update(utilisateurToUpdate);

				logger.info("WithdrawalFromAccount : Utilisateur {} done", email);
				withdrawalFromAccountDone = true;
			}

		}

		return withdrawalFromAccountDone;
	}

	public boolean addConnection(String utilisateurEmail, String connectionEmail) {

		boolean connectionAdded = false;

		Utilisateur utilisateurToAddConnection = utilisateurRepository.read(utilisateurEmail);
		Utilisateur newConnection = utilisateurRepository.read(connectionEmail);
		
		if (utilisateurToAddConnection == null) {
			logger.error("AddConection : Utilisateur {} does not exist", utilisateurEmail);
		} else if (newConnection == null) {
			logger.error("AddConection : Utilisateur {} does not exist", connectionEmail);
		} else if( utilisateurEmail.equals(connectionEmail) ) {
			logger.error("AddConection : Utilisateur {} same as connection to add", utilisateurEmail);
		} else {
			//Utilisateur utilisateur = utilisateurRepository.read(utilisateurEmail);
			Set<Utilisateur> utilisateurConnections = new HashSet<>();
			utilisateurConnections = utilisateurToAddConnection.getConnection();
			
			if (utilisateurConnections.contains(newConnection)) {
				logger.error("AddConection : Utilisateur {} connection already exist", utilisateurEmail);
			} else {
				utilisateurConnections.add(newConnection);
				utilisateurToAddConnection.setConnection(utilisateurConnections);
				utilisateurRepository.addConnection(utilisateurToAddConnection, newConnection);
				//utilisateurRepository.update(utilisateur);
				logger.info("AddConection : Utilisateur {} connection added", utilisateurEmail);
				connectionAdded = true;
			}
		}

		return connectionAdded;
	}

}
