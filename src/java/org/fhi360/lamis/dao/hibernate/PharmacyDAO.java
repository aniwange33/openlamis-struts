/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Pharmacy;

public class PharmacyDAO {
    public static Long save(Pharmacy pharmacy) {
       Long id = 0L;
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(pharmacy);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Pharmacy pharmacy) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(pharmacy);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }
  
    public static void delete(Long id) {
        Pharmacy pharmacy = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            pharmacy = (Pharmacy) session.get(Pharmacy.class, id);
            if(pharmacy != null) {
                session.delete(pharmacy);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Pharmacy find(Long id) {     
        Pharmacy pharmacy = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            pharmacy = (Pharmacy) session.get(Pharmacy.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return pharmacy;
    }    

    public static Set<Pharmacy> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Pharmacy> pharmacies = new HashSet<Pharmacy>(0);
        try {
            session.beginTransaction();
            List<Pharmacy> list = (List<Pharmacy>) session.createQuery("from Pharmacy").list();
            session.getTransaction().commit();
            pharmacies = new HashSet<Pharmacy>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return pharmacies;        
    }       
}
