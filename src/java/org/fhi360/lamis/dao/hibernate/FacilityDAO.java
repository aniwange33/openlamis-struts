/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Facility;

public class FacilityDAO {

    public static Long save(Facility facility) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(facility);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }
    
    public static void update(Facility facility) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(facility);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void saveOrUpdate(Facility facility) {
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            session.saveOrUpdate(facility);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }
    
    public static void delete(Long id) {
        Facility facility = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            facility = (Facility) session.get(Facility.class, id);
            if(facility != null) {
                session.delete(facility);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

   public static Facility find(Long id) {     
        Facility facility = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            facility = (Facility) session.get(Facility.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            throw e;
        }
        return facility;
    }    

    public static Set<Facility> list() {
        Set<Facility> facilities = new HashSet<Facility>(0);
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Facility> list = (List<Facility>) session.createQuery("from Facility").list();
            session.getTransaction().commit();
            facilities = new HashSet<Facility>(list);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return facilities;        
    }
    
    public static boolean getPaddingStatus(long facilityId) {
        int pad = 1;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List list = session.createSQLQuery("select f.pad_hospital_num from facility f where f.facility_id = :facilityId")
            .setParameter("facilityId", facilityId).list();
            session.getTransaction().commit();
            pad = (Integer) list.get(0);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return pad == 1 ? true : false;                
    }

    public static int getDayDqa(long facilityId) {
        int dayDqa = 0;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List list = session.createSQLQuery("select f.day_dqa from facility f where f.facility_id = :facilityId")
            .setParameter("facilityId", facilityId).list();
            session.getTransaction().commit();
            dayDqa = (Integer) list.get(0);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return dayDqa;                
    }
    
}
