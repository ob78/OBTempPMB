package com.paymybuddy.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Transaction;

public class TransactionRepositoryJpaImpl2 implements ITransactionRepository {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryJpaImpl2.class);

	private EntityManagerFactory entityManagerFactory = null;

	public TransactionRepositoryJpaImpl2(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public Transaction create(Transaction transactionToInsert) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.persist(transactionToInsert);
		entityManager.close();
		return transactionToInsert;
	}

	@Override
	public void update(Transaction transactionToUpdate) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.merge(transactionToUpdate);
		entityManager.close();
	}

	@Override
	public Transaction read(long idTransaction) {
		Transaction transactionRead = null;
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		transactionRead = entityManager.find(Transaction.class, idTransaction);
		entityManager.close();
		if (transactionRead == null) {
			logger.error("Error : utilisateur with email {} not found", idTransaction);
		}
		return transactionRead;
	}

	@Override
	public void delete(long idTransaction) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Transaction transactionToDelete = entityManager.find(Transaction.class, idTransaction);
		entityManager.remove(transactionToDelete);
		entityManager.close();
	}

	@Override
	public List<Transaction> getTransactions(String emailUtilisateur) {
		String REQUEST_TRANSACTIONS = "SELECT t FROM Transaction t WHERE t.initiateur.email IN ( SELECT email FROM Utilisateur WHERE email = :email ) ORDER by t.id DESC";
		List<Transaction> transactions = new ArrayList<>();
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		TypedQuery<Transaction> query = entityManager.createQuery(REQUEST_TRANSACTIONS, Transaction.class);
		transactions = query.setParameter("email", emailUtilisateur).getResultList();
		entityManager.close();
		return transactions;
	}

	// A supprimer
	/*
	public List<Transaction> getAllTransaction() {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		Query query = entityManager.createQuery("SELECT t FROM tranasction t");
		return query.getResultList();
	}
	*/
	
}
