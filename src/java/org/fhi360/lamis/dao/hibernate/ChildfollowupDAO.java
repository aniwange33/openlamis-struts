/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Childfollowup;

public class ChildfollowupDAO {
    public static Long save(Childfollowup childfollowup) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(childfollowup);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Childfollowup childfollowup) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(childfollowup);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Childfollowup childfollowup = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            childfollowup = (Childfollowup) session.get(Childfollowup.class, id);
            if(childfollowup != null) {
                session.delete(childfollowup);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Childfollowup find(Long id) {     
        Childfollowup childfollowup = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            childfollowup = (Childfollowup) session.get(Childfollowup.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return childfollowup;
    }    

    public static Set<Childfollowup> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Childfollowup> childfollowups = new HashSet<Childfollowup>(0);
        try {
            session.beginTransaction();
            List<Childfollowup> list = (List<Childfollowup>) session.createQuery("from Childfollowup").list();
            session.getTransaction().commit();
            childfollowups = new HashSet<Childfollowup>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return childfollowups;        
    }       
}
