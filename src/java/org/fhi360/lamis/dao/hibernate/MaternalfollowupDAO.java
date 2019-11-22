/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Maternalfollowup;

public class MaternalfollowupDAO {
    public static Long save(Maternalfollowup maternalfollowup) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(maternalfollowup);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Maternalfollowup maternalfollowup) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(maternalfollowup);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Maternalfollowup maternalfollowup = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            maternalfollowup = (Maternalfollowup) session.get(Maternalfollowup.class, id);
            if(maternalfollowup != null) {
                session.delete(maternalfollowup);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Maternalfollowup find(Long id) {     
        Maternalfollowup maternalfollowup = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            maternalfollowup = (Maternalfollowup) session.get(Maternalfollowup.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return maternalfollowup;
    }    

    public static Set<Maternalfollowup> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Maternalfollowup> maternalfollowups = new HashSet<Maternalfollowup>(0);
        try {
            session.beginTransaction();
            List<Maternalfollowup> list = (List<Maternalfollowup>) session.createQuery("from Maternalfollowup").list();
            session.getTransaction().commit();
            maternalfollowups = new HashSet<Maternalfollowup>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return maternalfollowups;        
    }       
}
