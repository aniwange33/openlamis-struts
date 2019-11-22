/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Motherinformation;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class MotherInformationDAO {
    
    public static Long save(Motherinformation motherInfo) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(motherInfo);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            //throw e;
            e.printStackTrace();
        } 
       return id;
    }

    public static void update(Motherinformation appInfo) {
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
        Motherinformation motherInfo;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            motherInfo = (Motherinformation) session.get(Motherinformation.class, id);
            if(motherInfo != null) {
                session.delete(motherInfo);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Motherinformation find(Long id) {     
        Motherinformation appInfo = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            appInfo = (Motherinformation) session.get(Motherinformation.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return appInfo;
    }

    public static Motherinformation findByPatientId(Long patientId) {     
        Motherinformation motherInfo = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            Query query = session.createQuery("FROM Motherinformation mf where mf.patientId = "+patientId);
            motherInfo = (Motherinformation) query.list().get(0);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return motherInfo;
    }    

    public static Set<Motherinformation> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Motherinformation> motherInfos = new HashSet<>();
        try {
            session.beginTransaction();
            List<Motherinformation> list = (List<Motherinformation>) session.createQuery("from Motherinformation").list();
            session.getTransaction().commit();
            motherInfos = new HashSet<>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return motherInfos;        
    } 
}
