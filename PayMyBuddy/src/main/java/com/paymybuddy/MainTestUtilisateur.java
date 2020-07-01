package com.paymybuddy;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.configuration.RepositoryJdbcConfiguration;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repository.IUtilisateurRepository;

public class MainTestUtilisateur {

	public static void main(String[] args) {

		final Logger logger = LoggerFactory.getLogger(MainTestUtilisateur.class);

		// clear database
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/data.sql"));

		DriverManagerDataSource driverManagerDataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyProd", "postgres", "admin");

		DatabasePopulatorUtils.execute(resourceDatabasePopulator, driverManagerDataSource);
		
		
		String propertiesFilePath = "paymybuddy.properties";
		
		String persistence = "persistencePostgreProd";
		
		//IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository("jdbc", propertiesFilePath);
		IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository("jpa", persistence);
		
		// Test insertion d'un utilisateur
		Utilisateur utilisateurToInsert = new Utilisateur();

		utilisateurToInsert.setEmail("abc@policier.com");
		utilisateurToInsert.setPassword("ac");
		utilisateurToInsert.setSolde(123d);

		utilisateurRepository.create(utilisateurToInsert);

		logger.info("\nTest Insertion d'un utilisateur : {}\n", utilisateurToInsert);

		// Test update d'un utilisateur
		Utilisateur utilisateurUpdated = new Utilisateur();

		utilisateurUpdated.setEmail(utilisateurToInsert.getEmail());
		utilisateurUpdated.setPassword("newPassword");
		utilisateurUpdated.setSolde(123456d);

		utilisateurRepository.update(utilisateurUpdated);

		logger.info("\nTest Update d'un utilisateur : {}\n", utilisateurUpdated);

		// Test read d'un utilisateur existant
		Utilisateur utilisateurRead = utilisateurRepository.read(utilisateurUpdated.getEmail());

		logger.info("\nTest Read d'un utilisateur existant : {}\n", utilisateurRead);

		// Test delete d'un utilisateur
		String emailUtilisateurToDelete = utilisateurRead.getEmail();

		utilisateurRepository.delete(emailUtilisateurToDelete);

		logger.info("\nTest Suppression d'un utilisateur : {}\n", emailUtilisateurToDelete);

		// Test read d'un utilisateur absent
		logger.info("\nTest Read d'un utilisateur absent : {}", emailUtilisateurToDelete);

		utilisateurRepository.read(emailUtilisateurToDelete);
		
		// Test read d'un utilisateur avec connections
		Utilisateur utilisateurReadConnections = utilisateurRepository.read("achristie@policier.com");

		logger.info("\nTest Read d'un utilisateur existant : {}\n", utilisateurReadConnections);
				
		// Test ajout d'une connection
		Utilisateur utilisateurConnectionToAdd = utilisateurRepository.read("achristie@policier.com");
		Utilisateur newConnection = utilisateurRepository.read("jrrtolkien@fantasy.com");

		Set<Utilisateur> connections = utilisateurConnectionToAdd.getConnection();
		connections.add(newConnection);
		//utilisateurConnectionToAdd.setConnection(connections);
		//utilisateurRepository.update(utilisateurConnectionToAdd);
		utilisateurRepository.addConnection(utilisateurConnectionToAdd, newConnection);
		
		Utilisateur utilisateurAfterConnectionAdded = utilisateurRepository.read("achristie@policier.com");
		logger.info("\nTest Ajout d'une connection: {}\n", utilisateurAfterConnectionAdded);
		
		
	}

}
