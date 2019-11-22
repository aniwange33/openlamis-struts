/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.fhi360.lamis.model.Appointment;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class AppointmentDAO {
    public static Long save(Appointment appointment) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(appointment);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void update(Appointment appointment) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(appointment);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Appointment appointment = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            appointment = (Appointment) session.get(Appointment.class, id);
            if(appointment != null) {
                session.delete(appointment);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Appointment find(Long id) {     
        Appointment appointment = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            appointment = (Appointment) session.get(Appointment.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return appointment;
    }    

    public static Set<Appointment> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Appointment> Appointments = new HashSet<Appointment>(0);
        try {
            session.beginTransaction();
            List<Appointment> list = (List<Appointment>) session.createQuery("from Appointment").list();
            session.getTransaction().commit();
            Appointments = new HashSet<Appointment>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return Appointments;        
    }       
    
}
