package com.packsendme.api.google.dao;

import java.util.List;

public interface ITolls_DAO<T> {

	public T add(T entity);
	
	public void addList(List<T> entity);

	public T find(String object);
	
	public List<T> findAll();
	
	public void remove(T entity);
	
	public T update(T entity);
	
}
