/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Patient;

public class PatientDAO {
    public static Long save(Patient patient) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(patient);
            session.getTransaction().commit();
       } 
       catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
       }
       return id;
    }
    
    public static void update(Patient patient) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(patient);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Patient patient = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            patient = (Patient) session.get(Patient.class, id);
            if(patient != null) {
                session.delete(patient);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

   public static Patient find(Long id) {     
        Patient patient = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            patient = (Patient) session.get(Patient.class, id);
            session.getTransaction().commit();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return patient;
    }    

    public static Set<Patient> list() {
        Set<Patient> patients = new HashSet<Patient>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Patient> list = (List<Patient>) session.createQuery("from Patient").list();
            session.getTransaction().commit();
            patients = new HashSet<Patient>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return patients;        
    }  
        
    public static String findPhone(Long patientId) {
        String phone = null;
        String query = "select p.phone from Patient p where p.patientId = :id";
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            phone = (String) session.createQuery(query).setLong("id", patientId).uniqueResult();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw e;
        }
        return phone;
    }
}
