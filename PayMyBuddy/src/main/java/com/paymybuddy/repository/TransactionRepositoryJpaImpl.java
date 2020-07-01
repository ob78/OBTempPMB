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

public class TransactionRepositoryJpaImpl implements ITransactionRepository {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryJpaImpl.class);

	private EntityManagerFactory entityManagerFactory = null;

	public TransactionRepositoryJpaImpl(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public Transaction create(Transaction transactionToInsert) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			entityManager.persist(transactionToInsert);

			transaction.commit();

			return transactionToInsert;

		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
			return null;
		} finally {
			entityManager.close();
		}

	}

	@Override
	public void update(Transaction transactionToUpdate) {

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			entityManager.merge(transactionToUpdate);

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			entityManager.close();
		}

	}

	@Override
	public Transaction read(long idTransaction) {
		Transaction transactionRead = null;

		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			transactionRead = entityManager.find(Transaction.class, idTransaction);

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			entityManager.close();
		}

		if (transactionRead == null) {
			logger.error("Error : utilisateur with email {} not found", idTransaction);
		}
		return transactionRead;

	}

	@Override
	public void delete(long idTransaction) {
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();

		try {
			Transaction transactionToDelete = entityManager.find(Transaction.class, idTransaction);
			entityManager.remove(transactionToDelete);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			entityManager.close();
		}
	}

	@Override
	public List<Transaction> getTransactions(String emailUtilisateur) {
		
		String REQUEST_TRANSACTIONS = "SELECT t FROM Transaction t WHERE t.initiateur.email IN ( SELECT email FROM Utilisateur WHERE email = :email ) ORDER by t.id DESC";
			
		List<Transaction> transactions = new ArrayList<>();
		
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		
		try {
			TypedQuery<Transaction> query = entityManager.createQuery(REQUEST_TRANSACTIONS, Transaction.class);
			
			transactions = query.setParameter("email", emailUtilisateur).getResultList();

			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			entityManager.close();
		}

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
