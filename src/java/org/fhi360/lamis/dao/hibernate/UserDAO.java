/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.User;

public class UserDAO {

    public static Long save(User user) {
       Long id = 0L;
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(user);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }
    
    public static void update(User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        User user = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            user = (User) session.get(User.class, id);
            if(user != null) {
                session.delete(user);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }


   public static User find(Long id) {     
        User user = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            user = (User) session.get(User.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            throw e;
        }
        return user;
    }    

    public static Set<User> list() {
        Set<User> users = new HashSet<User>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<User> list = (List<User>) session.createQuery("from User").list();
            session.getTransaction().commit();
            users = new HashSet<User>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return users;        
    }       

    public static void updateFacilityId(Long id, Long facilityId) {
        User user = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            user = (User) session.get(User.class, id);
            if(user != null) {
                user.setFacilityId(facilityId);
                session.update(user);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

}
 