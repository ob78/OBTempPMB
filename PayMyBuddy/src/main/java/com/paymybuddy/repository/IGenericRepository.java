package com.paymybuddy.repository;

// Pas faisable dans ce cas car les 2 interfaces n'ont pas les mêmes méthodes
public interface IGenericRepository<DataObject, PrimaryKeyType> {

	public DataObject create(DataObject data);

	public void update(DataObject data);

	public DataObject read(PrimaryKeyType primaryKey);

	public void delete(PrimaryKeyType idTransaction);

}
