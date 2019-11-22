/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import java.util.List;
import org.fhi360.lamis.model.Deviceconfig;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class DeviceconfigDAO {
    public static Long save(Deviceconfig deviceconfig) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            session.saveOrUpdate(deviceconfig);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }


    public static void update(Deviceconfig deviceconfig) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(deviceconfig);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }
    
    
    public static long findByDeviceId(String deviceId) {     
        long id = 0L;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List list = session.createSQLQuery("select d.deviceconfig_id from deviceconfig d where d.device_id = :deviceId")
            .setParameter("deviceId", deviceId).list();
            session.getTransaction().commit();           
            id = (Long) list.get(0);  
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return id;
    }    
    
    
    public static Deviceconfig findDeviceconfigByDeviceId(String deviceId) {     
        Deviceconfig deviceconfig = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List list = session.createSQLQuery("select d.deviceconfig_id from deviceconfig d where d.device_id = :deviceId")
            .setParameter("deviceId", deviceId).list();
            session.getTransaction().commit();           
            long id = (Long) list.get(0);  
            if(id != 0L) deviceconfig = (Deviceconfig) session.get(Deviceconfig.class, id);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return deviceconfig;
    }    
    
}
