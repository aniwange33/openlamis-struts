/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Dhisvalue;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;

/**
 *
 * @author user10
 */
public class DhisvalueDAO {

    public static Long save(Dhisvalue indicator) {
        Long id = 0L;
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            id = (Long) session.save(indicator);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return id;
    }

    public static void update(Dhisvalue indicator) {
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(indicator);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Dhisvalue indicator = null;
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            indicator = (Dhisvalue) session.get(Dhisvalue.class, id);
            if (indicator != null) {
                session.delete(indicator);
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw e;
        }
    }

    public static Dhisvalue find(Long id) {
        Dhisvalue indicator = null;
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            indicator = (Dhisvalue) session.get(Dhisvalue.class, id);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return indicator;
    }

    public static Set<Dhisvalue> list() {
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Dhisvalue> indicators = new HashSet<Dhisvalue>(0);
        try {
            session.beginTransaction();
            List<Dhisvalue> list = (List<Dhisvalue>) session.createQuery("from dhisvalue").list();
            session.getTransaction().commit();
            indicators = new HashSet<Dhisvalue>(list);

        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return indicators;
    }

    public List<Dhisvalue> findByPeriod(long facilityId, int indicatorNo, int period) {
        List<Dhisvalue> list = new ArrayList<>();
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            Transaction transaction = session.beginTransaction();
            String hql = "from dhisvalue i where i.dhisvalue_id = :id and i.facilityId = :facilityId, and i.period = :period";
            Query query = session.createQuery(hql);
            query.setParameter("facilityId", facilityId);
            query.setParameter("period", period);
            list = (List<Dhisvalue>) query.list();
            transaction.commit();
        } catch (HibernateException exception) {
            exception.printStackTrace();
            session.getTransaction().rollback();
            throw exception;
        }
        return list;
    }

}
