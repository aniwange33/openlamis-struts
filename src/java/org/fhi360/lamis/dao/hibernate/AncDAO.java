/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Anc;
import org.hibernate.Query;

public class AncDAO {
    public static Long save(Anc anc) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(anc);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Anc anc) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(anc);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Anc anc = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            anc = (Anc) session.get(Anc.class, id);
            if(anc != null) {
                session.delete(anc);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Anc find(Long id) {     
        Anc anc = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            anc = (Anc) session.get(Anc.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return anc;
    }    
    
    public static Anc findByPatientId(Long patientId) {     
        Anc ancInfo = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Anc> ancs = (List<Anc>) session.createQuery("from Anc where patient_id = "+patientId).list();
            if(ancs.size() > 0)
                ancInfo = ancs.get(0);
            session.getTransaction().commit();
        } 
        catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
        }
        return ancInfo;
    }    

    public static Set<Anc> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Anc> ancs = new HashSet<Anc>(0);
        try {
            session.beginTransaction();
            List<Anc> list = (List<Anc>) session.createQuery("from Anc").list();
            session.getTransaction().commit();
            ancs = new HashSet<Anc>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return ancs;        
    } 
	
}
