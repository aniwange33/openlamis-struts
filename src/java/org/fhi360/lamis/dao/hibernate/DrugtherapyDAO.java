/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Drugtherapy;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class DrugtherapyDAO {
    public static Long save(Drugtherapy drugtherapy) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(drugtherapy);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Drugtherapy drugtherapy) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(drugtherapy);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Drugtherapy drugtherapy = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            drugtherapy = (Drugtherapy) session.get(Drugtherapy.class, id);
            if(drugtherapy != null) {
                session.delete(drugtherapy);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Drugtherapy  find(Long id) {     
        Drugtherapy drugtherapy = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            drugtherapy = (Drugtherapy) session.get(Drugtherapy.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return drugtherapy;
    }    

    public static Set<Drugtherapy> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Drugtherapy> drugtherapies = new HashSet<Drugtherapy>(0);
        try {
            session.beginTransaction();
            List<Drugtherapy> list = (List<Drugtherapy>) session.createQuery("from Drugtherapy").list();
            session.getTransaction().commit();
            drugtherapies = new HashSet<Drugtherapy>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return drugtherapies;        
    }       
    
}
