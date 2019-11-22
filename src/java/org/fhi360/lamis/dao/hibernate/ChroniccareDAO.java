/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Chroniccare;

public class ChroniccareDAO {
    public static Long save(Chroniccare chroniccare) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(chroniccare);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Chroniccare chroniccare) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(chroniccare);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Chroniccare chroniccare = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            chroniccare = (Chroniccare) session.get(Chroniccare.class, id);
            if(chroniccare != null) {
                session.delete(chroniccare);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Chroniccare find(Long id) {     
        Chroniccare chroniccare = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            chroniccare = (Chroniccare) session.get(Chroniccare.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return chroniccare;
    }    

    public static Set<Chroniccare> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Chroniccare> chroniccares = new HashSet<Chroniccare>(0);
        try {
            session.beginTransaction();
            List<Chroniccare> list = (List<Chroniccare>) session.createQuery("from Chroniccare").list();
            session.getTransaction().commit();
            chroniccares = new HashSet<Chroniccare>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return chroniccares;        
    }       
}
