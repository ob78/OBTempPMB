package com.paymybuddy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.repositorytxmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the data persistence for user using JPA implementation for
 * persistence and Hibernate for Tx management.
 */
public class UtilisateurRepositoryJpaTxHibernateImpl implements IUtilisateurRepository {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJpaTxHibernateImpl.class);

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public UtilisateurRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
	}

	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	@Override
	public void create(Utilisateur utilisateur) {

		//HIBERNATE
		//repositoryTxManager.getCurrentSession().save(utilisateur);

		//JPA
		repositoryTxManager.getCurrentSession().persist(utilisateur);
	}

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	@Override
	public void update(Utilisateur utilisateur) {

		//HIBERNATE
		//repositoryTxManager.getCurrentSession().update(utilisateur);
		
		//JPA
		repositoryTxManager.getCurrentSession().merge(utilisateur);

	}

	/**
	 * Read a user from the repository.
	 * 
	 * @param email The email of the user to read
	 * 
	 * @return The user read
	 */
	@Override
	public Utilisateur read(String email) {

		//HIBERNATE
		//Utilisateur utilisateur = repositoryTxManager.getCurrentSession().get(Utilisateur.class, email);

		//JPA
		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().find(Utilisateur.class, email);
	
		return utilisateur;
	}

	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	@Override
	public void delete(String email) {

		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().find(Utilisateur.class, email);

		//HIBERNATE
		//repositoryTxManager.getCurrentSession().delete(utilisateur);

		//JPA
		repositoryTxManager.getCurrentSession().remove(utilisateur);
	}

	/**
	 * Add a connection in the repository.
	 * 
	 * @param utilisateur The user for which to add a connection
	 * 
	 * @param connection  The connection to be added to the user
	 */
	@Override
	public void addConnection(Utilisateur utilisateur, Utilisateur connection) {
		update(utilisateur);
	}

	// A supprimer
	@Override
	public void updateAll(Utilisateur utilisateur) {
		update(utilisateur);
	}

}
