/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Laboratory;

public class LaboratoryDAO {
    public static Long save(Laboratory laboratory) {
       Long id = 0L;
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(laboratory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }  
        return id;
    }

    public static void update(Laboratory laboratory) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(laboratory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Laboratory laboratory = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            laboratory = (Laboratory) session.get(Laboratory.class, id);
            if(laboratory != null) {
                session.delete(laboratory);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Laboratory find(Long id) {     
        Laboratory laboratory = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            laboratory = (Laboratory) session.get(Laboratory.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return laboratory;
    }    

    public static Set<Laboratory> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Laboratory> laboratories = new HashSet<Laboratory>(0);
        try {
            session.beginTransaction();
            List<Laboratory> list = (List<Laboratory>) session.createQuery("from laboratory").list();
            session.getTransaction().commit();
            laboratories = new HashSet<Laboratory>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return laboratories;        
    }       
}
