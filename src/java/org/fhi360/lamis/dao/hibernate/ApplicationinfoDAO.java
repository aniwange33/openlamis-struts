/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Anc;
import org.fhi360.lamis.model.Applicationinfo;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class ApplicationinfoDAO {
    
    public static Long save(Applicationinfo appInfo) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(appInfo);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Applicationinfo appInfo) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(appInfo);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Applicationinfo appInfo = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            appInfo = (Applicationinfo) session.get(Applicationinfo.class, id);
            if(appInfo != null) {
                session.delete(appInfo);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Applicationinfo find(Long id) {     
        Applicationinfo appInfo = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            appInfo = (Applicationinfo) session.get(Applicationinfo.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return appInfo;
    }    

    public static Set<Applicationinfo> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Applicationinfo> appInfos = new HashSet<>();
        try {
            session.beginTransaction();
            List<Applicationinfo> list = (List<Applicationinfo>) session.createQuery("from ApplicationInfo").list();
            session.getTransaction().commit();
            appInfos = new HashSet<>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return appInfos;        
    } 
}
