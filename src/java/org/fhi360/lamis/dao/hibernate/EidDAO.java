/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Eid;

public class EidDAO {

    public static Long save(Eid eid) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(eid);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        } 
       return id;
    }

    public static void saveOrUpdate(Eid eid) {
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            session.saveOrUpdate(eid);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }
    
    public static void update(Eid eid) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(eid);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long id) {
        Eid eid = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            eid = (Eid) session.get(Eid.class, id);
            if(eid != null) {
                session.delete(eid);
            }
            session.getTransaction().commit();
        }
        catch (HibernateException e){
            throw e;            
        }
    }

    public static Eid find(Long id) {     
        Eid eid = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            eid = (Eid) session.get(Eid.class, id);
            session.getTransaction().commit();
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return eid;
    }    

    public static Eid findEidByLabno(String labno) {     
        Eid eid = null;        
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List list = session.createSQLQuery("select e.eid_id from eid e where e.labno = :labno")
            .setParameter("labno", labno).list();
            session.getTransaction().commit();           
            long id = (Long) list.get(0);  //Cannot cast bigint into long, this line throws an error
            if(id != 0L) eid = (Eid) session.get(Eid.class, id);
        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return eid;
    }    
    
    public static Set<Eid> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Set<Eid> eids = new HashSet<Eid>(0);
        try {
            session.beginTransaction();
            List<Eid> list = (List<Eid>) session.createQuery("from Eid").list();
            session.getTransaction().commit();
            eids = new HashSet<Eid>(list);

        } 
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return eids;        
    }       
    
}
