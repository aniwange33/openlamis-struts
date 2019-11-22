/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Indexcontact;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class IndexcontactDAO {
    public static Long save(Indexcontact indexcontact) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(indexcontact);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Indexcontact indexcontact) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
           
            session.update(indexcontact);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            System.out.println("EOYISCO "+e.getMessage());
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Indexcontact indexcontact = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            indexcontact = (Indexcontact) session.get(Indexcontact.class, id);
            if(indexcontact != null) {
                session.delete(indexcontact);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Indexcontact find(Long id) {     
        Indexcontact indexcontact = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            indexcontact = (Indexcontact) session.get(Indexcontact.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return indexcontact;
    }    

    public static Set<Indexcontact> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Indexcontact> indexcontacts = new HashSet<Indexcontact>(0);
        try {
            session.beginTransaction();
            List<Indexcontact> list = (List<Indexcontact>) session.createQuery("from Indexcontact").list();
            session.getTransaction().commit();
            indexcontacts = new HashSet<Indexcontact>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return indexcontacts;        
    }       
    
}
