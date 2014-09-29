package com.adms.common.dao.generic;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Pageable;

public interface GenericDao<T, PK extends Serializable> {
	
	public T save(T object) throws Exception;
	public void save(List<T> objects) throws Exception;
	
	public List<T> findAll() throws Exception;
	public T find(PK id) throws Exception;
	public List<T> find(T example) throws Exception;
	
	public boolean delete(PK id) throws Exception;
	
	public Long findTotalCount() throws Exception;
	public Long findTotalCount(final T object) throws Exception;
	
	public List<T> findByExamplePaging(T object, Pageable pageable) throws Exception;
	
	public List<T> searchByExamplePaging(T object, Pageable pageable) throws Exception;
	
}
