package com.paymybuddy.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

	@Override
	public void create(Utilisateur utilisateur) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.persist(utilisateur);
		//entityManager.close();
	}

	@Override
	public void update(Utilisateur utilisateur) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.merge(utilisateur);
		entityManager.close();
	}

	@Override
	public Utilisateur read(String email) {
		Utilisateur utilisateur = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		utilisateur = entityManager.find(Utilisateur.class, email);
		entityManager.close();
		if (utilisateur == null) {
			logger.error("Error : utilisateur with email {} not found", email);
		}
		return utilisateur;
	}

	@Override
	public void delete(String email) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Utilisateur utilisateurToDelete = entityManager.find(Utilisateur.class, email);
		entityManager.remove(utilisateurToDelete);
		entityManager.close();
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
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery("SELECT u FROM utilisateur u");
		return query.getResultList();
	}

}
