/**
 *
 * @author aalozie
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Statushistory;

public class StatushistoryDAO {
    
    public static Long save(Statushistory statushistory) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(statushistory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }

    public static void update(Statushistory statushistory) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(statushistory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Statushistory statushistory = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            statushistory = (Statushistory) session.get(Statushistory.class, id);
            if(statushistory != null) {
                session.delete(statushistory);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Statushistory find(Long id) {     
        Statushistory statushistory = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            statushistory = (Statushistory) session.get(Statushistory.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            throw e;
        }
        return statushistory;
    }    

    public static Set<Statushistory> list() {
        Set<Statushistory> statushistories = new HashSet<Statushistory>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Statushistory> list = (List<Statushistory>) session.createQuery("from Statushistory").list();
            session.getTransaction().commit();
            statushistories = new HashSet<Statushistory>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return statushistories;        
    }  
    
}
