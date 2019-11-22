/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Mhtc;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class MhtcDAO {
    public static Long save(Mhtc mhtc) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(mhtc);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Mhtc mhtc) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(mhtc);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Mhtc mhtc = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            mhtc = (Mhtc) session.get(Mhtc.class, id);
            if(mhtc != null) {
                session.delete(mhtc);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Mhtc  find(Long id) {     
        Mhtc mhtc = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            mhtc = (Mhtc) session.get(Mhtc.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return mhtc;
    }    

    public static Set<Mhtc> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Mhtc> mhtcs = new HashSet<Mhtc>(0);
        try {
            session.beginTransaction();
            List<Mhtc> list = (List<Mhtc>) session.createQuery("from Mhtc").list();
            session.getTransaction().commit();
            mhtcs = new HashSet<Mhtc>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return mhtcs;        
    }       
    
    
}
