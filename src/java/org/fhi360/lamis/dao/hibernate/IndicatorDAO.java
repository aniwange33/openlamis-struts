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
import org.fhi360.lamis.model.Clinic;
import org.fhi360.lamis.model.Indicatorvalue;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;

/**
 *
 * @author user10
 */
public class IndicatorDAO {
    public static Long save(Indicatorvalue indicator) {
       Long id = 0L; 
       org.hibernate.classic.Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(indicator);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Indicatorvalue indicator) {
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(indicator);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Indicatorvalue indicator = null;
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            indicator = (Indicatorvalue) session.get(Indicatorvalue.class, id);
            if(indicator != null) {
                session.delete(indicator);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Indicatorvalue find(Long id) {     
        Indicatorvalue indicator = null;        
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            indicator = (Indicatorvalue) session.get(Clinic.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return indicator;
    }    

    public static Set<Indicatorvalue> list() {
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Indicatorvalue> indicators = new HashSet<Indicatorvalue>(0);
        try {
            session.beginTransaction();
            List<Indicatorvalue> list = (List<Indicatorvalue>) session.createQuery("from Indicator").list();
            session.getTransaction().commit();
            indicators = new HashSet<Indicatorvalue>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return indicators;        
    }       
    
    public List<Indicatorvalue> findByPeriod(long facilityId, int indicatorNo, int month, int year) {
        List<Indicatorvalue> list = new ArrayList<>();
        org.hibernate.classic.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            Transaction transaction = session.beginTransaction();
            String hql = "from Indicator i where i.indicatorId = :id and i.facilityId = :facilityId, and i.month = :month and i.year = :year"; 
            Query query = session.createQuery(hql); 
            query.setParameter("facilityId", facilityId);
            query.setParameter("month", month);
            query.setParameter("year", year);
            list = (List<Indicatorvalue>) query.list();
            transaction.commit();
        } 
        catch (HibernateException exception) {
            exception.printStackTrace();
            session.getTransaction().rollback();
            throw exception;
        }        
        return list;
    }
    
}
