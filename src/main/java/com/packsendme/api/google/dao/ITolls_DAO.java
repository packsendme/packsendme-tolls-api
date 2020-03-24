package com.packsendme.api.google.dao;

import java.util.List;
import java.util.Map;

public interface ITolls_DAO<T,D> {

	public T add(T entity);
	
	public void addList(List<T> entity);

	public Map<String, T> find(D object);
	
	public List<T> findAll();
	
	public void remove(T entity);
	
	public T update(T entity);
	
}
