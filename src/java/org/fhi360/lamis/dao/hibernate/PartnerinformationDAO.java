/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Partnerinformation;

public class PartnerinformationDAO {
    public static Long save(Partnerinformation partnerinformation) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(partnerinformation);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Partnerinformation partnerinformation) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(partnerinformation);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Partnerinformation partnerinformation = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            partnerinformation = (Partnerinformation) session.get(Partnerinformation.class, id);
            if(partnerinformation != null) {
                session.delete(partnerinformation);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Partnerinformation find(Long id) {     
        Partnerinformation partnerinformation = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            partnerinformation = (Partnerinformation) session.get(Partnerinformation.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return partnerinformation;
    }    

    public static Set<Partnerinformation> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Partnerinformation> partnerinformations = new HashSet<Partnerinformation>(0);
        try {
            session.beginTransaction();
            List<Partnerinformation> list = (List<Partnerinformation>) session.createQuery("from Partnerinformation").list();
            session.getTransaction().commit();
            partnerinformations = new HashSet<Partnerinformation>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return partnerinformations;        
    } 
	
}
