/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Patientcasemanager;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class PatientCaseManagerDAO {
    public static Long save(Patientcasemanager casemanager) {
       Long id = 0L;
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(casemanager);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
        }
       return id;
    }
    
    public static void update(Patientcasemanager casemanager) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(casemanager);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Patientcasemanager casemanager = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            casemanager = (Patientcasemanager) session.get(Patientcasemanager.class, id);
            if(casemanager != null) {
                session.delete(casemanager);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }


   public static Patientcasemanager find(Long id) {     
        Patientcasemanager casemanager = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            casemanager = (Patientcasemanager) session.get(Patientcasemanager.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            throw e;
        }
        return casemanager;
    }    

    public static Set<Patientcasemanager> list() {
        Set<Patientcasemanager> casemanagers = new HashSet<Patientcasemanager>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Patientcasemanager> list = (List<Patientcasemanager>) session.createQuery("from CaseManager").list();
            session.getTransaction().commit();
            casemanagers = new HashSet<Patientcasemanager>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return casemanagers;        
    }
}
