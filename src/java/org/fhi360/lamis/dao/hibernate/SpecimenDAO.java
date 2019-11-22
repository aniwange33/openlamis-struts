/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Specimen;

public class SpecimenDAO {
    public static Long save(Specimen specimen) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(specimen);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Specimen specimen) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(specimen);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Specimen specimen = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            specimen = (Specimen) session.get(Specimen.class, id);
            if(specimen != null) {
                session.delete(specimen);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Specimen find(Long id) {     
        Specimen specimen = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            specimen = (Specimen) session.get(Specimen.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return specimen;
    }    

    public static Set<Specimen> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Specimen> specimens = new HashSet<Specimen>(0);
        try {
            session.beginTransaction();
            List<Specimen> list = (List<Specimen>) session.createQuery("from Specimen").list();
            session.getTransaction().commit();
            specimens = new HashSet<Specimen>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return specimens;        
    }       
}
