package com.paymybuddy.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entities.Transaction;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
@Disabled
public class TransactionRepositoryJpaImplTest {

	private static String persistence = "persistencePostgreTest";

	private static ResourceDatabasePopulator resourceDatabasePopulator;

	private static DriverManagerDataSource dataSource;

	private ITransactionRepository transactionRepositoryImplUnderTest;

	private IUtilisateurRepository utilisateurRepositoryImplUnderTest;

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

		transactionRepositoryImplUnderTest = RepositoryFactory.getTransactionRepository("jpa", persistence);

		utilisateurRepositoryImplUnderTest = RepositoryFactory.getUtilisateurRepository("jpa", persistence);
	}

	@Test
	public void createTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

		Transaction transactionToCreate = new Transaction();
		transactionToCreate.setInitiateur(initiateur);
		transactionToCreate.setContrepartie(contrepartie);
		transactionToCreate.setMontant(123d);
		transactionToCreate.setCommentaire("Transaction created for test purpose");

		// ACT
		Transaction transactionCreated = transactionRepositoryImplUnderTest.create(transactionToCreate);

		// ASSERT
		assertNotNull(transactionRepositoryImplUnderTest.read(transactionCreated.getIdTransaction()));
		assertEquals(transactionToCreate, transactionCreated);

		assertThat(transactionCreated).isEqualTo(transactionToCreate);
	}

	@Test
	public void deleteTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

		Transaction transactionToDelete = new Transaction();
		transactionToDelete.setInitiateur(initiateur);
		transactionToDelete.setContrepartie(contrepartie);
		transactionToDelete.setMontant(123d);
		transactionToDelete.setCommentaire("Transaction created for test purpose");

		Transaction transactionDeleted = transactionRepositoryImplUnderTest.create(transactionToDelete);

		// ACT
		transactionRepositoryImplUnderTest.delete(transactionDeleted.getIdTransaction());

		// ASSERT
		assertNull(transactionRepositoryImplUnderTest.read(transactionDeleted.getIdTransaction()));
	}

	@Test
	public void updateTransaction() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

		Transaction transactionToUpdate = new Transaction();
		transactionToUpdate.setInitiateur(initiateur);
		transactionToUpdate.setContrepartie(contrepartie);
		transactionToUpdate.setMontant(321d);
		transactionToUpdate.setCommentaire("Transaction created for test purpose");

		transactionToUpdate = transactionRepositoryImplUnderTest.create(transactionToUpdate);

		transactionToUpdate.setCommentaire("Transaction udpated");

		// ACT
		transactionRepositoryImplUnderTest.update(transactionToUpdate);

		// ASSERT
		Transaction transactionUdpated = transactionRepositoryImplUnderTest
				.read(transactionToUpdate.getIdTransaction());

		assertEquals("Transaction udpated", transactionUdpated.getCommentaire());

		assertThat(transactionUdpated.getCommentaire()).isEqualTo("Transaction udpated");

	}

	@Test
	public void readTransaction_whenTransactionExist() {
		// ARRANGE
		Utilisateur initiateur = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie = utilisateurRepositoryImplUnderTest.read("def@test.com");

		Transaction transactionToRead = new Transaction();
		transactionToRead.setInitiateur(initiateur);
		transactionToRead.setContrepartie(contrepartie);
		transactionToRead.setMontant(123d);
		transactionToRead.setCommentaire("Transaction created for test purpose");

		transactionToRead = transactionRepositoryImplUnderTest.create(transactionToRead);

		// ACT
		Transaction transactionRead = transactionRepositoryImplUnderTest.read(transactionToRead.getIdTransaction());

		// ASSERT
		assertNotNull(transactionRead);
		assertEquals(transactionToRead, transactionRead);

		assertThat(transactionRead).isEqualTo(transactionToRead);
	}

	@Test
	public void readTransaction_whenTransactionNotExist() {
		// ACT & ASSERT
		assertNull(transactionRepositoryImplUnderTest.read(123));
	}

	@Test
	public void getAllTransactions() {
		// ARRANGE
		Transaction transactionToGet1 = new Transaction();
		Utilisateur initiateur1 = utilisateurRepositoryImplUnderTest.read("abc@test.com");
		Utilisateur contrepartie1 = utilisateurRepositoryImplUnderTest.read("def@test.com");
		transactionToGet1.setInitiateur(initiateur1);
		transactionToGet1.setContrepartie(contrepartie1);
		transactionToGet1.setMontant(1d);
		transactionToGet1 = transactionRepositoryImplUnderTest.create(transactionToGet1);

		Transaction transactionToGet2 = new Transaction();
		transactionToGet2.setInitiateur(initiateur1);
		transactionToGet2.setContrepartie(contrepartie1);
		transactionToGet2.setMontant(2d);
		transactionToGet2 = transactionRepositoryImplUnderTest.create(transactionToGet2);

		Transaction transactionToGet3 = new Transaction();
		Utilisateur contrepartie2 = utilisateurRepositoryImplUnderTest.read("ghi@test.com");
		transactionToGet3.setInitiateur(initiateur1);
		transactionToGet3.setContrepartie(contrepartie2);
		transactionToGet3.setMontant(3d);
		transactionToGet3 = transactionRepositoryImplUnderTest.create(transactionToGet3);

		// ACT
		List<Transaction> transactionsGet = new ArrayList<>();
		transactionsGet = transactionRepositoryImplUnderTest.getTransactions("abc@test.com");

		// ASSERT
		assertNotNull(transactionsGet);
		assertEquals(3, transactionsGet.size());

		assertThat(transactionsGet).containsExactlyInAnyOrder(transactionToGet1, transactionToGet2, transactionToGet3);
	}

}
