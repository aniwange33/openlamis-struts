/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Communitypharm;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user1
 */
public class CommunitypharmDAO {
    public static Long save(Communitypharm communitypharm) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(communitypharm);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }
    
    public static void update(Communitypharm communitypharm) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(communitypharm);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void saveOrUpdate(Communitypharm communitypharm) {
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            session.saveOrUpdate(communitypharm);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }
    
    public static void delete(Long id) {
        Communitypharm communitypharm = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            communitypharm = (Communitypharm) session.get(Communitypharm.class, id);
            if(communitypharm != null) {
                session.delete(communitypharm);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

   public static Communitypharm find(Long id) {     
        Communitypharm communitypharm = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            communitypharm = (Communitypharm) session.get(Communitypharm.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            throw e;
        }
        return communitypharm;
    }    

    public static Set<Communitypharm> list() {
        Set<Communitypharm> Communitypharms = new HashSet<Communitypharm>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Communitypharm> list = (List<Communitypharm>) session.createQuery("from Communitypharm").list();
            session.getTransaction().commit();
            Communitypharms = new HashSet<Communitypharm>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return Communitypharms;        
    }
    
}
