package com.paymybuddy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entities.Transaction;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.ServiceFactory;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;
import com.paymybuddy.service.TransactionTxHibernateService;
import com.paymybuddy.service.UtilisateurTxHibernateService;

public class TransactionTxHibernateServiceTest {

	private static String hibernateConfigFile = "src/test/resources/hibernateTest.cfg.xml";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private RepositoryTxManagerHibernate repositoryTxManager;

	private IUtilisateurRepository utilisateurRepositoryImpl;

	private ITransactionRepository transactionRepositoryImpl;

	private TransactionTxHibernateService transactionTxHibernateServiceUnderTest;

	@BeforeAll
	private static void setUpAllTest() {
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyTest", "postgres", "admin");

		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/dataTransactionsForTests.sql"));
	}

	@BeforeEach
	private void setUpPerTest() {
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(hibernateConfigFile);

		utilisateurRepositoryImpl = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		transactionRepositoryImpl = RepositoryFactory.getTransactionRepository(repositoryTxManager);

		transactionTxHibernateServiceUnderTest = ServiceFactory.getTransactionService(repositoryTxManager,
				utilisateurRepositoryImpl, transactionRepositoryImpl);
	}

	@Test
	public void getAllTransactions() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Transaction transactionToGet1 = new Transaction();
		Utilisateur initiateur1 = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartie1 = utilisateurRepositoryImpl.read("def@test.com");
		transactionToGet1.setInitiateur(initiateur1);
		transactionToGet1.setContrepartie(contrepartie1);
		transactionToGet1.setMontant(1d);
		transactionToGet1 = transactionRepositoryImpl.create(transactionToGet1);

		Transaction transactionToGet2 = new Transaction();
		transactionToGet2.setInitiateur(initiateur1);
		transactionToGet2.setContrepartie(contrepartie1);
		transactionToGet2.setMontant(2d);
		transactionToGet2 = transactionRepositoryImpl.create(transactionToGet2);

		Transaction transactionToGet3 = new Transaction();
		Utilisateur contrepartie2 = utilisateurRepositoryImpl.read("ghi@test.com");
		transactionToGet3.setInitiateur(initiateur1);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);
		transactionToGet3 = transactionRepositoryImpl.create(transactionToGet3);
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionTxHibernateServiceUnderTest.getAllTransactions("abc@test.com");

		// ASSERT
		assertNotNull(transactionsGet);
		assertEquals(3, transactionsGet.size());

		// assertThat(transactionsGet).containsExactlyInAnyOrder(transactionToGet1,
		// transactionToGet2, transactionToGet3);
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreConnectedAndSoldeSufficient() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest
				.getAllTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), 10d);

		// ASSERT
		assertTrue(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals((double) (initiateurBeforeNewTransaction.getSolde() - 10d),
				initiateurAfterNewTransaction.getSolde());
		assertEquals((double) (contrepartieBeforeNewTransaction.getSolde() + 10d),
				contrepartieAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getAllTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size() + 1, listTransactionsAfterNewTransaction.size());

		Transaction newTransaction = listTransactionsAfterNewTransaction.iterator().next();

		assertEquals(10d, newTransaction.getMontant());
		assertEquals(initiateurBeforeNewTransaction.getEmail(), newTransaction.getInitiateur().getEmail());
		assertEquals(contrepartieBeforeNewTransaction.getEmail(), newTransaction.getContrepartie().getEmail());
	}
}
