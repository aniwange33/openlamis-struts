/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Clinic;

public class ClinicDAO {
    public static Long save(Clinic clinic) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(clinic);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Clinic clinic) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(clinic);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Clinic clinic = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            clinic = (Clinic) session.get(Clinic.class, id);
            if(clinic != null) {
                session.delete(clinic);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Clinic find(Long id) {     
        Clinic clinic = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            clinic = (Clinic) session.get(Clinic.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return clinic;
    }    

    public static Set<Clinic> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Clinic> clinics = new HashSet<Clinic>(0);
        try {
            session.beginTransaction();
            List<Clinic> list = (List<Clinic>) session.createQuery("from Clinic").list();
            session.getTransaction().commit();
            clinics = new HashSet<Clinic>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return clinics;        
    }       
}
