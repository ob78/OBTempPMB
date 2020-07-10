package com.paymybuddy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
public class TransactionTxHibernateServiceUnitTest {

	private static String hibernateConfigFile = "src/test/resources/hibernateTest.cfg.xml";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private RepositoryTxManagerHibernate repositoryTxManager;

	@Mock
	private IUtilisateurRepository utilisateurRepositoryMock;

	@Mock
	private ITransactionRepository transactionRepositoryMock;
	
	//private IUtilisateurRepository utilisateurRepositoryImpl;

	//private ITransactionRepository transactionRepositoryImpl;

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
		// We prepare the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		repositoryTxManager = RepositoryTxManagerHibernate.getRepositoryTxManagerHibernate(hibernateConfigFile);

		//utilisateurRepositoryImpl = RepositoryFactory.getUtilisateurRepository(repositoryTxManager);

		//transactionRepositoryImpl = RepositoryFactory.getTransactionRepository(repositoryTxManager);

		transactionTxHibernateServiceUnderTest = ServiceFactory.getTransactionService(repositoryTxManager,
				utilisateurRepositoryMock, transactionRepositoryMock);
	}

	@Test
	public void getTransactionsWhenUtilisateurExist() {
		// ARRANGE
		//repositoryTxManager.openCurrentSessionWithTx();
		
		Utilisateur initiateur1 = new Utilisateur();
		initiateur1.setEmail("abc@test.com");
		initiateur1.setPassword("abc");
		initiateur1.setSolde(0d);

		Utilisateur contrepartie1 = new Utilisateur();
		initiateur1.setEmail("def@test.com");
		initiateur1.setPassword("def");
		initiateur1.setSolde(0d);
		
		Utilisateur contrepartie2 = new Utilisateur();
		contrepartie2.setEmail("ghi@test.com");
		contrepartie2.setPassword("ghi");
		contrepartie2.setSolde(0d);
		
		/*
		Utilisateur initiateur1 = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartie1 = utilisateurRepositoryImpl.read("def@test.com");
		Utilisateur contrepartie2 = utilisateurRepositoryImpl.read("ghi@test.com");
		*/
		
		Transaction transactionToGet1 = new Transaction();
		transactionToGet1.setInitiateur(initiateur1);
		transactionToGet1.setContrepartie(contrepartie1);
		transactionToGet1.setMontant(1d);
		//transactionToGet1 = transactionRepositoryImpl.create(transactionToGet1);

		Transaction transactionToGet2 = new Transaction();
		transactionToGet2.setInitiateur(initiateur1);
		transactionToGet2.setContrepartie(contrepartie1);
		transactionToGet2.setMontant(2d);
		//transactionToGet2 = transactionRepositoryImpl.create(transactionToGet2);

		Transaction transactionToGet3 = new Transaction();
		//Utilisateur contrepartie2 = utilisateurRepositoryImpl.read("ghi@test.com");
		transactionToGet3.setInitiateur(initiateur1);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);
		//transactionToGet3 = transactionRepositoryImpl.create(transactionToGet3);
		//repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(initiateur1);

		List<Transaction> transactionsToReturn = new ArrayList<>();
		transactionsToReturn.add(transactionToGet1);
		transactionsToReturn.add(transactionToGet2);
		transactionsToReturn.add(transactionToGet3);
		
		when(transactionRepositoryMock.getTransactions("abc@test.com")).thenReturn(transactionsToReturn);
		
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ASSERT
		assertEquals(3, transactionsGet.size());

		// assertThat(transactionsGet).containsExactlyInAnyOrder(transactionToGet1,
		// transactionToGet2, transactionToGet3);
	}
/*
	@Test
	public void getTransactionsWhenUtilisateurNotExist() {
		// ARRANGE
		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionTxHibernateServiceUnderTest.getTransactions("UtilisateurNotExist");

		// ASSERT
		assertTrue(transactionsGet.isEmpty());
	}
*/
	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreConnectedAndSoldeSufficient() {
		// ARRANGE
		/*
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();
	*/
		Utilisateur initiateur = new Utilisateur();
		initiateur.setEmail("abc@test.com");
		initiateur.setPassword("abc");
		initiateur.setSolde(123d);

		Utilisateur contrepartie = new Utilisateur();
		contrepartie.setEmail("def@test.com");
		contrepartie.setPassword("def");
		contrepartie.setSolde(0d);
		
		Set <Utilisateur> connections =new HashSet<>();
		connections.add(contrepartie);
		initiateur.setConnection(connections);
		
		Transaction transaction = new Transaction();
		transaction.setInitiateur(initiateur);
		transaction.setContrepartie(contrepartie);
		transaction.setMontant(10d);
		
		
		
		//when(utilisateurRepositoryMock.read(anyString())).thenReturn(initiateur).thenReturn(contrepartie);
		
		//when(utilisateurRepositoryMock.read("abc@test.com")).thenReturn(initiateur);

		//when(utilisateurRepositoryMock.read("def@test.com")).thenReturn(contrepartie);
		
		doReturn(initiateur).when(utilisateurRepositoryMock).read("abc@test.com");
		
		doReturn(contrepartie).when(utilisateurRepositoryMock).read("def@test.com");
		
		//doNothing().when(utilisateurRepositoryMock).update(any(Utilisateur.class));
		
		doNothing().when(utilisateurRepositoryMock).update(initiateur);
		
		doNothing().when(utilisateurRepositoryMock).update(contrepartie);
	
		
		when(transactionRepositoryMock.create(transaction)).thenReturn(transaction);
		//doNothing().when(transactionRepositoryMock).create(transaction);

		
		//List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		//listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				"abc@test.com", "def@test.com", 10d);

		// ASSERT
		assertTrue(result);
/*
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals((double) (initiateurBeforeNewTransaction.getSolde() - 10d),
				(double) (initiateurAfterNewTransaction.getSolde()));
		assertEquals((double) (contrepartieBeforeNewTransaction.getSolde() + 10d),
				(double) (contrepartieAfterNewTransaction.getSolde()));

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size() + 1, listTransactionsAfterNewTransaction.size());

		Transaction newTransaction = listTransactionsAfterNewTransaction.iterator().next();

		assertEquals(10d, newTransaction.getMontant());
		assertEquals(initiateurBeforeNewTransaction.getEmail(), newTransaction.getInitiateur().getEmail());
		assertEquals(contrepartieBeforeNewTransaction.getEmail(), newTransaction.getContrepartie().getEmail());
*/
	}
/*
	@Test
	public void makeATransactionWhenInitiateurNotExist() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction("UtilisateurInitiateurNotExist",
				contrepartieBeforeNewTransaction.getEmail(), 10d);

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(contrepartieBeforeNewTransaction.getSolde(), contrepartieAfterNewTransaction.getSolde());
	}

	@Test
	public void makeATransactionWhenContrepartieNotExist() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest
				.makeATransaction(initiateurBeforeNewTransaction.getEmail(), "UtilisateurContrepartieNotExist", 10d);

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(initiateurBeforeNewTransaction.getSolde(), initiateurAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreNotConnected() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("klm@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), 10d);

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("klm@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(initiateurBeforeNewTransaction.getSolde(), initiateurAfterNewTransaction.getSolde());
		assertEquals(contrepartieBeforeNewTransaction.getSolde(), contrepartieAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}

	@Test
	public void makeATransactionWhenInitiateurAndContrepartieExistAndAreConnectedAndSoldeNotSufficient() {
		// ARRANGE
		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurBeforeNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieBeforeNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		List<Transaction> listTransactionsBeforeNewTransaction = new ArrayList<>();
		listTransactionsBeforeNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		// ACT
		boolean result = transactionTxHibernateServiceUnderTest.makeATransaction(
				initiateurBeforeNewTransaction.getEmail(), contrepartieBeforeNewTransaction.getEmail(), 1000d);

		// ASSERT
		assertFalse(result);

		repositoryTxManager.openCurrentSessionWithTx();
		Utilisateur initiateurAfterNewTransaction = utilisateurRepositoryImpl.read("abc@test.com");
		Utilisateur contrepartieAfterNewTransaction = utilisateurRepositoryImpl.read("def@test.com");
		repositoryTxManager.commitTxAndCloseCurrentSession();

		assertEquals(initiateurBeforeNewTransaction.getSolde(), initiateurAfterNewTransaction.getSolde());
		assertEquals(contrepartieBeforeNewTransaction.getSolde(), contrepartieAfterNewTransaction.getSolde());

		List<Transaction> listTransactionsAfterNewTransaction = new ArrayList<>();
		listTransactionsAfterNewTransaction = transactionTxHibernateServiceUnderTest.getTransactions("abc@test.com");

		assertEquals(listTransactionsBeforeNewTransaction.size(), listTransactionsAfterNewTransaction.size());
	}
*/
}
