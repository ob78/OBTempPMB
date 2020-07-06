package com.paymybuddy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTxManagerHibernate;

/**
 * Class managing the data persistence for the user using JPA implementation for
 * persistence and Hibernate for Tx management.
 */
public class UtilisateurRepositoryJpaTxHibernateImpl implements IUtilisateurRepository {

	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJpaTxHibernateImpl.class);

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public UtilisateurRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
		logger.info("UtilisateurRepositoryJpaTxHibernateImpl successfully created.");
	}

	/**
	 * Add a user in the repository.
	 * 
	 * @param Utilisateur The user to add
	 */
	@Override
	public void create(Utilisateur utilisateur) {

		repositoryTxManager.getCurrentSession().save(utilisateur);

	}

	/**
	 * Update a user in the repository.
	 * 
	 * @param Utilisateur The user to update
	 */
	@Override
	public void update(Utilisateur utilisateur) {

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

		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().get(Utilisateur.class, email);

		return utilisateur;
	}

	/**
	 * Delete a user from the repository.
	 * 
	 * @param email The email of the user to delete
	 */
	@Override
	public void delete(String email) {

		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().get(Utilisateur.class, email);

		repositoryTxManager.getCurrentSession().delete(utilisateur);

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
