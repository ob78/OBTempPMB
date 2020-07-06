package com.paymybuddy.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paymybuddy.entities.Transaction;
import com.paymybuddy.repositorytransactionsmanager.RepositoryTxManagerHibernate;

public class TransactionRepositoryJpaTxHibernateImpl implements ITransactionRepository {

	private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryJpaTxHibernateImpl.class);

	private RepositoryTxManagerHibernate repositoryTxManager = null;

	public TransactionRepositoryJpaTxHibernateImpl(RepositoryTxManagerHibernate repositoryTxManager) {
		this.repositoryTxManager = repositoryTxManager;
	}

	@Override
	public Transaction create(Transaction transactionToInsert) {
			
		//return repositoryManager.getCurrentSession().save(transactionToInsert);
		
		repositoryTxManager.getCurrentSession().save(transactionToInsert);
	
		Transaction transactionCreated = repositoryTxManager.getCurrentSession().get(Transaction.class, transactionToInsert.getIdTransaction());
	
		return transactionCreated;
	}

	@Override
	public void update(Transaction transactionToUpdate) {
		
		repositoryTxManager.getCurrentSession().merge(transactionToUpdate);
	
	}

	@Override
	public Transaction read(long idTransaction) {
		
		Transaction transactionRead = repositoryTxManager.getCurrentSession().get(Transaction.class, idTransaction);
		
		return transactionRead;

	}

	@Override
	public void delete(long idTransaction) {
		
		Transaction transactionToDelete = repositoryTxManager.getCurrentSession().get(Transaction.class, idTransaction);
		
		repositoryTxManager.getCurrentSession().delete(transactionToDelete);

	}

	@Override
	public List<Transaction> getTransactions(String emailUtilisateur) {
		String REQUEST_TRANSACTIONS = "SELECT t FROM Transaction t WHERE t.initiateur.email IN ( SELECT email FROM Utilisateur WHERE email = :email ) ORDER by t.id DESC";
		List<Transaction> transactions = new ArrayList<>();
		
		TypedQuery<Transaction> query = repositoryTxManager.getCurrentSession().createQuery(REQUEST_TRANSACTIONS, Transaction.class);
		transactions = query.setParameter("email", emailUtilisateur).getResultList();
		
		/*
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		TypedQuery<Transaction> query = entityManager.createQuery(REQUEST_TRANSACTIONS, Transaction.class);
		transactions = query.setParameter("email", emailUtilisateur).getResultList();
		entityManager.close();
		*/
		return transactions;
	}


}
