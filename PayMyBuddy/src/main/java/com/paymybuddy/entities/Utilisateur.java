package com.paymybuddy.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

@Entity
@Table(name="utilisateur")
public class Utilisateur {

	@Id
	private String email;
	
	//@Column(nullable=false)
	private String password;
	
	//@Column(nullable=false)
	private Double solde;
	
	//@Column(name = "connection")
	@ManyToMany//(fetch = FetchType.EAGER)
	@JoinTable(name="utilisateur_connection", joinColumns=@JoinColumn(name="utilisateur_email"), inverseJoinColumns=@JoinColumn(name="utilisateur_connection_email", nullable=true))
	private Set<Utilisateur> connection;

	public Utilisateur() {
		super();
	}

	public Utilisateur(String email, String password, Double solde, Set<Utilisateur> connection) {
		super();
		this.email = email;
		this.password = password;
		this.solde = solde;
		this.connection = connection;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Double getSolde() {
		return solde;
	}

	public void setSolde(Double solde) {
		this.solde = solde;
	}

	public Set<Utilisateur> getConnection() {
		return connection;
	}

	public void setConnection(Set<Utilisateur> connection) {
		this.connection = connection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connection == null) ? 0 : connection.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((solde == null) ? 0 : solde.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Utilisateur other = (Utilisateur) obj;
		if (connection == null) {
			if (other.connection != null)
				return false;
		} else if (!connection.equals(other.connection))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (solde == null) {
			if (other.solde != null)
				return false;
		} else if (!solde.equals(other.solde))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Utilisateur [email=" + email + ", password=" + password + ", solde=" + solde + ", connection="
				+ connection + "]";
	}

}
