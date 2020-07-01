package com.paymybuddy.repository;

import com.paymybuddy.entities.Utilisateur;

public interface IUtilisateurRepository {

	public void create(Utilisateur utilisateur);

	public void update(Utilisateur utilisateur);

	public Utilisateur read(String email);

	public void delete(String email);

	public void addConnection(Utilisateur utilisateur, Utilisateur connection);
	
	public void updateAll(Utilisateur utilisateur);
}
