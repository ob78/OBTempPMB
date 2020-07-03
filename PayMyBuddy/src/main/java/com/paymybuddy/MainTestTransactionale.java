package com.paymybuddy;

import org.hibernate.Session;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.paymybuddy.configuration.RepositoryDataSource;
import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.factory.RepositoryFactory;
import com.paymybuddy.factory.SessionFactoryPayMyBuddy;
import com.paymybuddy.repository.IUtilisateurRepository;

public class MainTestTransactionale {

	public static void main(String[] args) {

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
		
		
		// Test insertion d'un utilisateur COMMIT JPA
		Utilisateur utilisateurToInsertJPACOMMIT = new Utilisateur();

		utilisateurToInsertJPACOMMIT.setEmail("abc@policier.com");
		utilisateurToInsertJPACOMMIT.setPassword("ac");
		utilisateurToInsertJPACOMMIT.setSolde(123d);
				
		//EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(persistence);
				
		//EntityManager entityManager = entityManagerFactory.createEntityManager();

		//EntityTransaction transaction = entityManager.getTransaction();
		//HibernateUtil.initialize();
		//Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
		
		//Session session = SessionFactoryPayMyBuddy.getSession();
		
		Session currentSession = SessionFactoryPayMyBuddy.getSession(); 
		
		//Transaction transaction = currentSession.getTransaction();
		//transaction.begin();
		currentSession.getTransaction().begin();
		
		try {
			utilisateurRepository.create(utilisateurToInsertJPACOMMIT);
			currentSession.getTransaction().commit();
			//transaction.commit();
		} catch (Exception e) {
			currentSession.getTransaction().rollback();
			//transaction.rollback();
			e.printStackTrace();
		} finally {
			//entityManager.close();
			//currentSession.flush();
			currentSession.close();
		}
		
		
		
	}

}
