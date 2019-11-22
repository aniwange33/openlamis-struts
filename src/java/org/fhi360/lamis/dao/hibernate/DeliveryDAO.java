/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Delivery;

public class DeliveryDAO {
    public static Long save(Delivery delivery) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(delivery);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Delivery delivery) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(delivery);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Delivery delivery = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            delivery = (Delivery) session.get(Delivery.class, id);
            if(delivery != null) {
                session.delete(delivery);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Delivery find(Long id) {     
        Delivery delivery = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            delivery = (Delivery) session.get(Delivery.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return delivery;
    }    

    public static Set<Delivery> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Delivery> deliveries = new HashSet<Delivery>(0);
        try {
            session.beginTransaction();
            List<Delivery> list = (List<Delivery>) session.createQuery("from Delivery").list();
            session.getTransaction().commit();
            deliveries = new HashSet<Delivery>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return deliveries;        
    }       
}
