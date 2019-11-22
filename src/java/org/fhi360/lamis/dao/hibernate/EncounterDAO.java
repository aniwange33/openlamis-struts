/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Encounter;

public class EncounterDAO {
    public static Long save(Encounter encounter) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(encounter);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Encounter encounter) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(encounter);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Encounter encounter = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            encounter = (Encounter) session.get(Encounter.class, id);
            if(encounter != null) {
                session.delete(encounter);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Encounter find(Long id) {     
        Encounter encounter = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            encounter = (Encounter) session.get(Encounter.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return encounter;
    }    

    public static Set<Encounter> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Encounter> encounters = new HashSet<Encounter>(0);
        try {
            session.beginTransaction();
            List<Encounter> list = (List<Encounter>) session.createQuery("from Encounter").list();
            session.getTransaction().commit();
            encounters = new HashSet<Encounter>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return encounters;        
    }       
}
