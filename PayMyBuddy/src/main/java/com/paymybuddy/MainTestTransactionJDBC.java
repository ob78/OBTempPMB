package com.paymybuddy;

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
import com.paymybuddy.repositorytransactionsmanager.RepositoryTransactionsManagerJDBCImpl;

public class MainTestTransactionJDBC {



	public static void main(String[] args) {

		 String persistence = "paymybuddy.properties";
		
		 RepositoryTransactionsManagerJDBCImpl repositoryManager;

		 ResourceDatabasePopulator resourceDatabasePopulator;

		 DriverManagerDataSource dataSource;

		 ITransactionRepository transactionRepositoryImplUnderTest;

		 IUtilisateurRepository utilisateurRepositoryImplUnderTest;
		 
		 double montant = 1;
		
		
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyProd", "postgres", "admin");

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/dataTransactionsForTests.sql"));
		
		
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		repositoryManager = RepositoryTransactionsManagerJDBCImpl.getRepositoryManagerJDBCImpl(persistence);
		
		transactionRepositoryImplUnderTest = RepositoryFactory.getTransactionRepository(repositoryManager);

		utilisateurRepositoryImplUnderTest = RepositoryFactory.getUtilisateurRepository(repositoryManager);

		repositoryManager.getConnection();


		// ARRANGE
		try {
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

		
		initiateur.setSolde(initiateur.getSolde() - montant);
		contrepartie.setSolde(contrepartie.getSolde() + montant);
		utilisateurRepositoryImplUnderTest.update(initiateur);
		utilisateurRepositoryImplUnderTest.update(contrepartie);

		
		Transaction transaction = new Transaction();
		//transaction.setIdTransaction(1);
		transaction.setInitiateur(initiateur);
		transaction.setContrepartie(contrepartie);
		transaction.setMontant(montant);
		transaction.setCommentaire("Transaction created for test purpose");
		
		transactionRepositoryImplUnderTest.create(transaction);


		Utilisateur testUtilisateur = new Utilisateur();
		testUtilisateur.setEmail("abc@test.com");
		testUtilisateur.setPassword("abc");
		testUtilisateur.setSolde(333d);
		//utilisateurRepositoryImplUnderTest.create(testUtilisateur);
		
		repositoryManager.commitTransaction();
		
		} catch (Exception e) {
			System.out.println("Error -> Rollback "+ e);
			repositoryManager.rollbackTransaction();
		}
				
		repositoryManager.closeCurrentConnection();
	}
}

