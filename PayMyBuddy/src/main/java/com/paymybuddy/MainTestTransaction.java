package com.paymybuddy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entities.Transaction;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.repository.ITransactionRepository;
import com.paymybuddy.repository.IUtilisateurRepository;

public class MainTestTransaction {

	public static void main(String[] args) {

		final Logger logger = LoggerFactory.getLogger(MainTestUtilisateur.class);

		// clear database
		ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/data.sql"));

		DriverManagerDataSource driverManagerDataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyProd", "postgres", "admin");

		//DatabasePopulatorUtils.execute(resourceDatabasePopulator, driverManagerDataSource);

		
		String propertiesFilePath = "paymybuddy.properties";

		String persistence = "persistencePostgreProd";

		/*
		ITransactionRepository transactionRepository = RepositoryFactory.getTransactionRepository("jdbc",
				propertiesFilePath);

		IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository("jdbc",
				propertiesFilePath);
		*/
		ITransactionRepository transactionRepository = RepositoryFactory.getTransactionRepository("jpa", persistence);

		IUtilisateurRepository utilisateurRepository = RepositoryFactory.getUtilisateurRepository("jpa", persistence);
	
		// Test insertion d'une transaction
		Utilisateur initiateurTransaction = utilisateurRepository.read("achristie@policier.com");
		Utilisateur contrepartieTransaction = utilisateurRepository.read("jdcarr@policier.com");

		Transaction transactionToInsert = new Transaction();
		transactionToInsert.setInitiateur(initiateurTransaction);
		transactionToInsert.setContrepartie(contrepartieTransaction);
		transactionToInsert.setMontant(123);
		transactionToInsert.setCommentaire("transaction inserted by Java code JDBC");

		long idTransactionInserted = transactionRepository.create(transactionToInsert).getIdTransaction();

		transactionToInsert.setIdTransaction(idTransactionInserted);

		logger.info("\nTest Insertion d'une transaction : {}\n", transactionToInsert);

		// Test update d'une transaction
		Transaction transactionUpdated = new Transaction();
		transactionUpdated.setIdTransaction(transactionToInsert.getIdTransaction());
		transactionUpdated.setInitiateur(transactionToInsert.getInitiateur());
		transactionUpdated.setContrepartie(transactionToInsert.getContrepartie());
		transactionUpdated.setMontant(456);
		transactionUpdated.setCommentaire("transaction updated by Java code JDBC");

		transactionRepository.update(transactionUpdated);

		logger.info("\nTest Update d'une transaction : {}\n", transactionUpdated);

		// Test read d'une transaction existante
		Transaction transactionRead = transactionRepository.read(transactionUpdated.getIdTransaction());

		logger.info("\nTest Read d'une transaction existante : {}\n", transactionRead);

		// Test delete d'une transaction
		long idTransactionToDelete = transactionUpdated.getIdTransaction();

		transactionRepository.delete(idTransactionToDelete);

		logger.info("\nTest Suppression d'une transaction : {}\n", idTransactionToDelete);

		// Test read d'une transaction absente
		logger.info("\nTest Read d'une transaction absente : {}", idTransactionToDelete);

		transactionRepository.read(idTransactionToDelete);
	
		// Test read transactions
		List<Transaction> transactions = transactionRepository.getTransactions("achristie@policier.com");
		for(Transaction transaction:transactions) {
			logger.info("\nTransaction : {}\n", transaction.getIdTransaction());
			logger.info("\nMontant : {}\n", transaction.getMontant());
		}
	}

}
