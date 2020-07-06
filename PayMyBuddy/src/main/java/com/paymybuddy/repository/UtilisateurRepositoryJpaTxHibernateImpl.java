package com.paymybuddy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTxManagerHibernate;

public class UtilisateurRepositoryJpaTxHibernateImpl implements IUtilisateurRepository {


	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJpaTxHibernateImpl.class);

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public UtilisateurRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
	}

	@Override
	public void create(Utilisateur utilisateur) {
		
		repositoryTxManager.getCurrentSession().save(utilisateur);
		
	}

	@Override
	public void update(Utilisateur utilisateur) {
		
		repositoryTxManager.getCurrentSession().merge(utilisateur);
		
	}

	@Override
	public Utilisateur read(String email) {
		
		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().get(Utilisateur.class, email);

		return utilisateur;
	}

	@Override
	public void delete(String email) {
		
		Utilisateur utilisateur = repositoryTxManager.getCurrentSession().get(Utilisateur.class, email);
		
		repositoryTxManager.getCurrentSession().delete(utilisateur);
		
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

}
