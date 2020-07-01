package com.paymybuddy.repository;

import java.util.List;

import com.paymybuddy.entities.Transaction;

public interface ITransactionRepository {

	public Transaction create(Transaction transaction);

	public void update(Transaction transaction);

	public Transaction read(long idTransaction);

	public void delete(long idTransaction);

	public List<Transaction> getTransactions (String emailUtilisateur);
}
