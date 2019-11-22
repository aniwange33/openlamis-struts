/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Devolve;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user1
 */
public class DevolveDAO {
    public static Long save(Devolve devolve) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(devolve);
            session.getTransaction().commit();
       } 
       catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
       }
       return id;
    }
    
    public static void update(Devolve devolve) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(devolve);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Devolve devolve = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            devolve = (Devolve) session.get(Devolve.class, id);
            if(devolve != null) {
                session.delete(devolve);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }
    
}
