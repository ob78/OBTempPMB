package com.paymybuddy.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;

public class UtilisateurRepositoryJdbcUpdateAllImplTest {

	private static String propertiesFilePathTest = "paymybuddyTest.properties";

	private static ResourceDatabasePopulator resourceDatabasePopulator;
	
	private static DriverManagerDataSource dataSource;
	
	private IUtilisateurRepository utilisateurRepositoryImplUnderTest;
	
	@BeforeAll 
	private static void setUpAllTest(){
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyTest", "postgres", "admin");
		/*
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUrl("jdbc:postgresql://localhost/PayMyBuddyTest");
		dataSource.setUsername("postgres");
		dataSource.setPassword("admin");
		*/
		// We get a resourceDatabasePopulator
		resourceDatabasePopulator = new ResourceDatabasePopulator();
		resourceDatabasePopulator.addScript(new ClassPathResource("/cleanDBForTests.sql"));
	}
	
	
	@BeforeEach
	private void setUpPerTest() {
		// We clear the database
		DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);

		utilisateurRepositoryImplUnderTest = RepositoryFactory.getUtilisateurRepository("jdbc", propertiesFilePathTest);
	}
	
	@Test
	public void updateUtilisateur_whenAddAConnectionWithNoExistingConnection() {
		// ARRANGE
		Utilisateur utilisateurToUpdate = new Utilisateur();
		utilisateurToUpdate.setEmail("abc@test.com");
		utilisateurToUpdate.setPassword("abc");
		utilisateurToUpdate.setSolde(123d);
		
		utilisateurRepositoryImplUnderTest.create(utilisateurToUpdate);
		
		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);
		
		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurNewConnection);
		utilisateurToUpdate.setConnection(connections);
		
		// ACT
		utilisateurRepositoryImplUnderTest.updateAll(utilisateurToUpdate);

		// ASSERT
		assertEquals(utilisateurToUpdate, utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail())).isEqualTo(utilisateurToUpdate);

	}
	
	@Test
	public void updateUtilisateur_whenAddAConnectionWithExistingConnection() {
		// ARRANGE
		Utilisateur utilisateurToUpdate = new Utilisateur();
		utilisateurToUpdate.setEmail("abc@test.com");
		utilisateurToUpdate.setPassword("abc");
		utilisateurToUpdate.setSolde(123d);

		Utilisateur utilisateurExistingConnection = new Utilisateur();
		utilisateurExistingConnection.setEmail("def@test.com");
		utilisateurExistingConnection.setPassword("def");
		utilisateurExistingConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurExistingConnection);
		
		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurExistingConnection);
		utilisateurToUpdate.setConnection(connections);
		
		utilisateurRepositoryImplUnderTest.create(utilisateurToUpdate);
		
		utilisateurRepositoryImplUnderTest.updateAll(utilisateurToUpdate);
		
		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("ghi@test.com");
		utilisateurNewConnection.setPassword("ghi");
		utilisateurNewConnection.setSolde(789d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);
		
		connections.add(utilisateurNewConnection);
		utilisateurToUpdate.setConnection(connections);
		
		// ACT
		utilisateurRepositoryImplUnderTest.updateAll(utilisateurToUpdate);

		// ASSERT
		assertEquals(utilisateurToUpdate, utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail())).isEqualTo(utilisateurToUpdate);

	}
	
	@Test
	public void updateUtilisateur_whenAddAConnectionAlreadyExisting() {
		// ARRANGE
		Utilisateur utilisateurToUpdate = new Utilisateur();
		utilisateurToUpdate.setEmail("abc@test.com");
		utilisateurToUpdate.setPassword("abc");
		utilisateurToUpdate.setSolde(123d);

		Utilisateur utilisateurExistingConnection = new Utilisateur();
		utilisateurExistingConnection.setEmail("def@test.com");
		utilisateurExistingConnection.setPassword("def");
		utilisateurExistingConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurExistingConnection);
		
		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurExistingConnection);
		utilisateurToUpdate.setConnection(connections);
		
		utilisateurRepositoryImplUnderTest.create(utilisateurToUpdate);
		
		utilisateurRepositoryImplUnderTest.updateAll(utilisateurToUpdate);
		/*
		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);
		
		connections.add(utilisateurNewConnection);
		utilisateurToUpdate.setConnection(connections);
		*/
		// ACT
		utilisateurRepositoryImplUnderTest.updateAll(utilisateurToUpdate);

		// ASSERT
		assertEquals(utilisateurToUpdate, utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail())).isEqualTo(utilisateurToUpdate);

	}
	
}
