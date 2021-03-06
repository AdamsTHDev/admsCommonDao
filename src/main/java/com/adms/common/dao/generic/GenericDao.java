package com.adms.common.dao.generic;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.data.domain.Pageable;

public interface GenericDao<T, PK extends Serializable> {
	
	public T save(T object) throws Exception;
	public void save(List<T> objects) throws Exception;
	
	public List<T> findAll() throws Exception;
	public T find(PK id) throws Exception;
	public List<T> find(T example) throws Exception;
	public List<T> findByCriteria(DetachedCriteria criteria) throws Exception;
	public List<T> findByCriteria(DetachedCriteria criteria, Pageable pageable) throws Exception;
	
	public boolean delete(PK id) throws Exception;
	
	public Long findTotalCount() throws Exception;
	public Long findTotalCount(final DetachedCriteria criteria) throws Exception;
	public Long findTotalCount(final T object) throws Exception;
	
	public List<T> findByExamplePaging(T object, Pageable pageable) throws Exception;
	
	public List<T> searchByExamplePaging(T object, Pageable pageable) throws Exception;
	
	public List<T> findByHQL(String hql, Object... vals) throws Exception;
	public List<T> findByHQL(String hql) throws Exception;
	
	public List<T> findByNamedQuery(String queryName, Object ... values) throws Exception;
	public Long countByNamedQuery(String queryName, Object ... values) throws Exception;
	
	public int bulkUpdate(String query, Object... values) throws Exception;
}
