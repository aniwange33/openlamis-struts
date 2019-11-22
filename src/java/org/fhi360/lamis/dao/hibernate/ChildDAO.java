/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Child;

public class ChildDAO {
    public static Long save(Child child) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(child);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Child child) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(child);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Child child = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            child = (Child) session.get(Child.class, id);
            if(child != null) {
                session.delete(child);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Child find(Long id) {     
        Child child = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            child = (Child) session.get(Child.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return child;
    }    

    public static Set<Child> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Child> childs = new HashSet<Child>(0);
        try {
            session.beginTransaction();
            List<Child> list = (List<Child>) session.createQuery("from Child").list();
            session.getTransaction().commit();
            childs = new HashSet<Child>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return childs;        
    }       
}
