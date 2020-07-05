package com.paymybuddy.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Utilisateur;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTransactionsManagerHibernateImpl;

public class UtilisateurRepositoryJpaHibernateImpl implements IUtilisateurRepository {


	private static final Logger logger = LoggerFactory.getLogger(UtilisateurRepositoryJpaHibernateImpl.class);

	private RepositoryTransactionsManagerHibernateImpl repositoryManager = null;

	public UtilisateurRepositoryJpaHibernateImpl(RepositoryTransactionsManagerHibernateImpl repositoryManager) {
		this.repositoryManager = repositoryManager;
	}

	@Override
	public void create(Utilisateur utilisateur) {
		
		repositoryManager.getCurrentSession().save(utilisateur);
		
	}

	@Override
	public void update(Utilisateur utilisateur) {
		
		repositoryManager.getCurrentSession().merge(utilisateur);
		
	}

	@Override
	public Utilisateur read(String email) {
		
		Utilisateur utilisateur = repositoryManager.getCurrentSession().get(Utilisateur.class, email);

		return utilisateur;
	}

	@Override
	public void delete(String email) {
		
		Utilisateur utilisateur = repositoryManager.getCurrentSession().get(Utilisateur.class, email);
		
		repositoryManager.getCurrentSession().delete(utilisateur);
		
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
