package com.paymybuddy.repository;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;

public class UtilisateurRepositoryJpaImpl2 implements IUtilisateurRepository {

	/*
	@PersistenceContext
	EntityManager entityManager;
	*/
	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJpaImpl2.class);

	private EntityManagerFactory entityManagerFactory = null;

	public UtilisateurRepositoryJpaImpl2(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	
	private Session currentSession;
    
    private Transaction currentTransaction;
 
    public UtilisateurRepositoryJpaImpl2() {
    }
 
    public Session openCurrentSession() {
        currentSession = getSessionFactory().openSession();
        return currentSession;
    }
 
    public Session openCurrentSessionwithTransaction() {
        currentSession = getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }
     
    public void closeCurrentSession() {
        currentSession.close();
    }
     
    public void commitAndCloseCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }
     
    public void rollbackAndCloseCurrentSessionwithTransaction() {
        currentTransaction.rollback();
        currentSession.close();
    }
    
    public void commitTransaction() {
        currentTransaction.commit();
    }
     
    public void rollbackTransaction() {
        currentTransaction.rollback();
    }
    
    private static SessionFactory getSessionFactory() {
        /*
    	Configuration configuration = new Configuration().configure();
        
    	StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        
    	SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());
        */
		File configFile = new File("hibernate.cfg.xml");

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
														.configure(configFile)
														.build();
		
		SessionFactory sessionFactory = new MetadataSources(registry)
				                            .buildMetadata()
				                            .buildSessionFactory();
    	
    	
    	return sessionFactory;
    }
    
    public Session getCurrentSession() {
        return currentSession;
    }
 
    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }
 
    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }
 
    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }
	
	
	
	@Override
	public void create(Utilisateur utilisateur) {
		
		 getCurrentSession().save(utilisateur);
		
		/*
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.persist(utilisateur);
		entityManager.close();
		*/
	}

	@Override
	public void update(Utilisateur utilisateur) {
		
		getCurrentSession().merge(utilisateur);
		
		/*
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.merge(utilisateur);
		entityManager.close();
		*/
	}

	@Override
	public Utilisateur read(String email) {
		
		Utilisateur utilisateur = getCurrentSession().get(Utilisateur.class, email);
		/*
		Utilisateur utilisateur = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		utilisateur = entityManager.find(Utilisateur.class, email);
		entityManager.close();
		if (utilisateur == null) {
			logger.error("Error : utilisateur with email {} not found", email);
		}
		*/
		return utilisateur;
	}

	@Override
	public void delete(String email) {
		
		Utilisateur utilisateur = getCurrentSession().get(Utilisateur.class, email);
		
		 getCurrentSession().delete(utilisateur);
		
		/*
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Utilisateur utilisateurToDelete = entityManager.find(Utilisateur.class, email);
		entityManager.remove(utilisateurToDelete);
		entityManager.close();
		*/
	}

	@Override
	public void addConnection(Utilisateur utilisateur, Utilisateur connection) {
		update(utilisateur);
	}

	// A supprimer
	@Override
	public void updateAll(Utilisateur utilisateur) {
		update(utilisateur);
	}

	// A supprimer
	//@Override
	public List<Utilisateur> getAllUtilisateur() {
		
		List<Utilisateur> utilisateur = getCurrentSession().createQuery("SELECT u FROM utilisateur u").list();
		
		return utilisateur;
		
		//List<Utilisateur> utilisateur = (List<Utilisateur>) getCurrentSession().createQuery("from Book").list();
		
		/*
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery("SELECT u FROM utilisateur u");
		return query.getResultList();
		*/
		
		
	}

}
