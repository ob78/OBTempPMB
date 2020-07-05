package com.paymybuddy.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

public class UtilisateurRepositoryJdbcImplTest {

	private static String propertiesFilePathTest = "paymybuddyTest.properties";

	private static ResourceDatabasePopulator resourceDatabasePopulator;
	
	private static DriverManagerDataSource dataSource;
	
	private IUtilisateurRepository utilisateurRepositoryImplUnderTest;
	
	@BeforeAll 
	private static void setUpAllTest(){
		// We get a dataSource
		dataSource = RepositoryDataSource.getDataSource("org.postgresql.Driver",
				"jdbc:postgresql://localhost/PayMyBuddyTest", "postgres", "admin");

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
	public void createUtilisateur() {
		// ARRANGE
		Utilisateur utilisateurToCreate = new Utilisateur();
		utilisateurToCreate.setEmail("abc@test.com");
		utilisateurToCreate.setPassword("abc");
		utilisateurToCreate.setSolde(123d);
				
		// ACT
		utilisateurRepositoryImplUnderTest.create(utilisateurToCreate);

		// ASSERT
		assertNotNull(utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail()));
		assertEquals(utilisateurToCreate,
				utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToCreate.getEmail())).isEqualTo(utilisateurToCreate);
	}
	
	@Test
	public void deleteUtilisateur() {
		// ARRANGE
		Utilisateur utilisateurToDelete = new Utilisateur();
		utilisateurToDelete.setEmail("abc@test.com");
		utilisateurToDelete.setPassword("abc");
		utilisateurToDelete.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToDelete);

		// ACT
		utilisateurRepositoryImplUnderTest.delete(utilisateurToDelete.getEmail());

		// ASSERT
		assertNull(utilisateurRepositoryImplUnderTest.read(utilisateurToDelete.getEmail()));
	}
	
	@Test
	public void updateUtilisateur() {
		// ARRANGE
		Utilisateur utilisateurToUpdate = new Utilisateur();
		utilisateurToUpdate.setEmail("abc@test.com");
		utilisateurToUpdate.setPassword("abc");
		utilisateurToUpdate.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToUpdate);

		Utilisateur utilisateurUpdated = new Utilisateur();
		utilisateurUpdated.setEmail("abc@test.com");
		utilisateurUpdated.setPassword("abc");
		utilisateurUpdated.setSolde(456d);

		// ACT
		utilisateurRepositoryImplUnderTest.update(utilisateurUpdated);

		// ASSERT
		assertEquals(utilisateurUpdated, utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToUpdate.getEmail())).isEqualTo(utilisateurUpdated);

	}
	
	@Test
	public void readUtilisateur_whenUtilisateurExist_whenUtilisateurHasNoConnection() {
		// ARRANGE
		Utilisateur utilisateurToRead = new Utilisateur();
		utilisateurToRead.setEmail("abc@test.com");
		utilisateurToRead.setPassword("abc");
		utilisateurToRead.setSolde(123d);

		utilisateurRepositoryImplUnderTest.create(utilisateurToRead);

		// ACT
		Utilisateur utilisateurRead = utilisateurRepositoryImplUnderTest.read(utilisateurToRead.getEmail());

		// ASSERT
		assertNotNull(utilisateurRead);
		assertEquals(utilisateurToRead, utilisateurRead);
		
		assertThat(utilisateurRead).isEqualTo(utilisateurToRead);
	}
	
	@Test
	public void readUtilisateur_whenUtilisateurExist_whenUtilisateurHasAConnection() {
		// ARRANGE
		Utilisateur utilisateurToRead = new Utilisateur();
		utilisateurToRead.setEmail("abc@test.com");
		utilisateurToRead.setPassword("abc");
		utilisateurToRead.setSolde(123d);
		utilisateurRepositoryImplUnderTest.create(utilisateurToRead);
		
		Utilisateur utilisateurConnectionToRead = new Utilisateur();
		utilisateurConnectionToRead.setEmail("def@test.com");
		utilisateurConnectionToRead.setPassword("def");
		utilisateurConnectionToRead.setSolde(456d);
		utilisateurRepositoryImplUnderTest.create(utilisateurConnectionToRead);

		Set<Utilisateur> connectionsToRead = new HashSet<>();
		connectionsToRead.add(utilisateurConnectionToRead);
		utilisateurToRead.setConnection(connectionsToRead);
		
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToRead, utilisateurConnectionToRead);

		// ACT
		Utilisateur utilisateurRead = utilisateurRepositoryImplUnderTest.read(utilisateurToRead.getEmail());

		// ASSERT
		assertNotNull(utilisateurRead);
		assertEquals(utilisateurToRead, utilisateurRead);
		
		assertThat(utilisateurRead).isEqualTo(utilisateurToRead);
	}
	
	@Test
	public void readUtilisateur_whenUtilisateurNotExist() {
		// ACT & ASSERT
		assertNull(utilisateurRepositoryImplUnderTest.read("UtilisateurNotExist"));
	}
	
	@Test
	public void addAConnection_whenNoExistingConnection() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(123d);
		
		utilisateurRepositoryImplUnderTest.create(utilisateurToAddConnection);
		
		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("def@test.com");
		utilisateurNewConnection.setPassword("def");
		utilisateurNewConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);
		
		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurNewConnection);
		utilisateurToAddConnection.setConnection(connections);
		
		// ACT
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurNewConnection);

		// ASSERT
		assertEquals(utilisateurToAddConnection, utilisateurRepositoryImplUnderTest.read(utilisateurToAddConnection.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToAddConnection.getEmail())).isEqualTo(utilisateurToAddConnection);

	}
	
	@Test
	public void addAConnection_whenExistingConnection() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(123d);

		Utilisateur utilisateurExistingConnection = new Utilisateur();
		utilisateurExistingConnection.setEmail("def@test.com");
		utilisateurExistingConnection.setPassword("def");
		utilisateurExistingConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurExistingConnection);
		
		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurExistingConnection);
		utilisateurToAddConnection.setConnection(connections);
		
		utilisateurRepositoryImplUnderTest.create(utilisateurToAddConnection);
		
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurExistingConnection);  ;
		
		Utilisateur utilisateurNewConnection = new Utilisateur();
		utilisateurNewConnection.setEmail("ghi@test.com");
		utilisateurNewConnection.setPassword("ghi");
		utilisateurNewConnection.setSolde(789d);

		utilisateurRepositoryImplUnderTest.create(utilisateurNewConnection);
		
		connections.add(utilisateurNewConnection);
		utilisateurToAddConnection.setConnection(connections);
		
		// ACT
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurNewConnection);   ;

		// ASSERT
		assertEquals(utilisateurToAddConnection, utilisateurRepositoryImplUnderTest.read(utilisateurToAddConnection.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToAddConnection.getEmail())).isEqualTo(utilisateurToAddConnection);

	}
	
	@Test
	public void addAConnection_whenConnectionAlreadyExisting() {
		// ARRANGE
		Utilisateur utilisateurToAddConnection = new Utilisateur();
		utilisateurToAddConnection.setEmail("abc@test.com");
		utilisateurToAddConnection.setPassword("abc");
		utilisateurToAddConnection.setSolde(123d);

		Utilisateur utilisateurExistingConnection = new Utilisateur();
		utilisateurExistingConnection.setEmail("def@test.com");
		utilisateurExistingConnection.setPassword("def");
		utilisateurExistingConnection.setSolde(456d);

		utilisateurRepositoryImplUnderTest.create(utilisateurExistingConnection);
		
		Set<Utilisateur> connections = new HashSet<>();
		connections.add(utilisateurExistingConnection);
		utilisateurToAddConnection.setConnection(connections);
		
		utilisateurRepositoryImplUnderTest.create(utilisateurToAddConnection);
		
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurExistingConnection);

		// ACT
		utilisateurRepositoryImplUnderTest.addConnection(utilisateurToAddConnection, utilisateurExistingConnection);

		// ASSERT
		assertEquals(utilisateurToAddConnection, utilisateurRepositoryImplUnderTest.read(utilisateurToAddConnection.getEmail()));
	
		assertThat(utilisateurRepositoryImplUnderTest.read(utilisateurToAddConnection.getEmail())).isEqualTo(utilisateurToAddConnection);

	}
	
}
