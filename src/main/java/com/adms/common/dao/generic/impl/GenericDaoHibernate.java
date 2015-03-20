package com.adms.common.dao.generic.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.adms.common.dao.generic.GenericDao;
import com.adms.common.domain.CountableDomain;

public class GenericDaoHibernate<T, PK extends Serializable> extends HibernateDaoSupport implements GenericDao<T, PK> {

	private Class<T> persistentClass;

	public GenericDaoHibernate(Class<T> persistentClass)
	{
		this.persistentClass = persistentClass;
	}

	@Autowired
	public void anyMethodName(SessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	@Autowired
	public void init(SessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	public T save(T object)
			throws Exception
	{
		return super.getHibernateTemplate().merge(object);
	}

	public void save(List<T> objects)
			throws Exception
	{
		for (T object : objects)
		{
			this.save(object);
		}
	}

	public List<T> findAll()
			throws Exception
	{
		return super.getHibernateTemplate().loadAll(this.persistentClass);
	}

	public T find(PK id)
			throws Exception
	{
		T entity = null;
		try
		{
			entity = super.getHibernateTemplate().get(this.persistentClass, id);

			if (entity == null)
			{
				throw new ObjectRetrievalFailureException(this.persistentClass, id);
			}
			return entity;
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			entity = null;
		}
	}

	@Override
	public List<T> find(T example)
			throws Exception
	{
		return super.getHibernateTemplate().findByExample(example);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(DetachedCriteria criteria) throws Exception {
		return (List<T>) super.getHibernateTemplate().findByCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByHQL(String hql, Object...vals) throws Exception {
		Session session = super.getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		for(int i = 0; i < vals.length; i++) {
			query.setParameter(i, vals[i]);
		}
		
		return (List<T>) query.list();
	}
	
	/**
	 * <p>The Object[] must contained paramName then paramValue; 
	 * <br/>ex: Object[0] is paramName, Object[1] is paramValue
	 * </p>
	 * remark: this hql is able to use "in" where cause with ArrayList Object
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByHqlParamName(String hql, Object...params) throws Exception {
		Session session = super.getSessionFactory().getCurrentSession();
		Query query = session.createQuery(hql);
		for(int i = 0; i < params.length; i+=2) {
			String param = String.valueOf(params[i]);
			Object val = params[i+1];
			if(val instanceof List) {
				query.setParameterList(param, ((List<?>) val).toArray());
			} else {
				query.setParameter(param, val);
			}
		}
		return (List<T>) query.list();
	}
	
	public List<T> findByHQL(String hql) throws Exception {
		
		return findByHQL(hql, new Object[0]);
	}

	public boolean delete(PK id)
			throws Exception
	{
		try
		{
			super.getHibernateTemplate().delete(this.find(id));
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			super.getHibernateTemplate().flush();
		}
		return true;
	}

	public Long findTotalCount()
			throws Exception
	{
		Long count = 0L;
		Session session = super.getSessionFactory().getCurrentSession();
		Query query = session.createQuery("select count(*) from " + this.persistentClass.getName());

		try
		{
			count = Long.parseLong(query.uniqueResult().toString());
		}
		catch (Exception e)
		{
		}
		return count;
	}

	public Long findTotalCount(final T object)
			throws Exception
	{
		Long count = 0L;
		try
		{
			Session session = super.getSessionFactory().getCurrentSession();
			Example example = Example.create(object).enableLike().ignoreCase();
			Criteria criteria = session.createCriteria(object.getClass()).add(example);
			count = Long.parseLong(String.valueOf(criteria.list().size()));
		}
		catch (Exception e)
		{
			throw e;
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public List<T> findByExamplePaging(T object, Pageable pageable)
			throws Exception
	{
		Example example = Example.create(object);
		DetachedCriteria criteria = DetachedCriteria.forClass(object.getClass()).add(example);

		if (pageable != null)
		{
			if (pageable.getSort() != null)
			{
				for (Iterator<org.springframework.data.domain.Sort.Order> it = pageable.getSort().iterator(); it.hasNext();)
				{
					org.springframework.data.domain.Sort.Order sortOrder = it.next();
					org.hibernate.criterion.Order order = null;
					if (sortOrder.getDirection().toString().equalsIgnoreCase("DESC"))
					{
						order = org.hibernate.criterion.Order.desc(sortOrder.getProperty());
					}
					else
					{
						order = org.hibernate.criterion.Order.asc(sortOrder.getProperty());
					}
					criteria.addOrder(order);
				}
			}
		}
		return (List<T>) super.getHibernateTemplate().findByCriteria(criteria, pageable.getPageNumber(), pageable.getPageSize());
		// return super.getHibernateTemplate().findByExample(object,
		// pageable.getPageNumber(), pageable.getPageSize());
	}

	@SuppressWarnings("unchecked")
	public List<T> searchByExamplePaging(T object, Pageable pageable)
			throws Exception
	{
		Session session = super.getSessionFactory().getCurrentSession();
		Example example = Example.create(object).enableLike().ignoreCase();
		Criteria criteria = session.createCriteria(object.getClass()).add(example);

		if (pageable != null)
		{
			if (pageable.getPageSize() >= 0)
			{
				criteria.setMaxResults(pageable.getPageSize());
			}
			if (pageable.getPageNumber() > 0)
			{
				criteria.setFirstResult(pageable.getPageNumber());
			}

			if (pageable.getSort() != null)
			{
				for (Iterator<org.springframework.data.domain.Sort.Order> it = pageable.getSort().iterator(); it.hasNext();)
				{
					org.springframework.data.domain.Sort.Order sortOrder = it.next();
					org.hibernate.criterion.Order order = null;
					if (sortOrder.getDirection().toString().equalsIgnoreCase("DESC"))
					{
						order = org.hibernate.criterion.Order.desc(sortOrder.getProperty());
					}
					else
					{
						order = org.hibernate.criterion.Order.asc(sortOrder.getProperty());
					}
					criteria.addOrder(order);
				}
			}
		}

		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedQuery(String queryName, Object ... values) throws Exception {
		return (List<T>) super.getHibernateTemplate().findByNamedQuery(queryName, values);
	}

	public Long countByNamedQuery(String queryName, Object ... values) throws Exception {
		return ((CountableDomain) super.getHibernateTemplate().findByNamedQuery(queryName, values).get(0)).getTotalCount();
	}

}
