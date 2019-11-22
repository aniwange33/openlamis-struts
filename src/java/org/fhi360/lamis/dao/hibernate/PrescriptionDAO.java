/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Prescription;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author user10
 */
public class PrescriptionDAO {
    public static Long save(Prescription prescription) {
       Long id = 0L;
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(prescription);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
        }
       return id;
    }
    
    public static void saveBatch(List<Prescription> prescriptions) {
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            for(Prescription prescription : prescriptions)
                session.saveOrUpdate(prescription);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    
    public static void update(Prescription prescription) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(prescription);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Prescription prescription = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            prescription = (Prescription) session.get(Prescription.class, id);
            if(prescription != null) {
                session.delete(prescription);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }


   public static Prescription find(Long id) {     
        Prescription prescription = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            prescription = (Prescription) session.get(Prescription.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            throw e;
        }
        return prescription;
    }    

    public static Set<Prescription> list() {
        Set<Prescription> prescriptions = new HashSet<Prescription>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Prescription> list = (List<Prescription>) session.createQuery("from Prescription").list();
            session.getTransaction().commit();
            prescriptions = new HashSet<Prescription>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return prescriptions;        
    }       
}
