/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Eac;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class EacDAO {
    public static Long save(Eac eac) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(eac);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Eac eac) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(eac);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Eac eac = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            eac = (Eac) session.get(Eac.class, id);
            if(eac != null) {
                session.delete(eac);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Eac find(Long id) {     
        Eac eac = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            eac = (Eac) session.get(Eac.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return eac;
    }    

    public static Set<Eac> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Eac> eacs = new HashSet<Eac>(0);
        try {
            session.beginTransaction();
            List<Eac> list = (List<Eac>) session.createQuery("from Eac").list();
            session.getTransaction().commit();
            eacs = new HashSet<Eac>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return eacs;        
    }       
}
