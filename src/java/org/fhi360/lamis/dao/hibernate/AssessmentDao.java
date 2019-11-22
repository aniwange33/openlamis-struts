/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Assessment;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author user10
 */
public class AssessmentDao {
     public static Long save(Assessment assessment) {
         
       Long id = 0L; 
       Session session = HibernateUtil.getSessionFactory().getCurrentSession();
       
       try {
          
            session.beginTransaction();
        
            id = (Long)session.save(assessment);
             
            session.getTransaction().commit();
            
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            //Log.error("DORCAS "+session);
            throw e;
        } 
       return id;
    }
     
      public static void update(Assessment assessment) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(assessment);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

}
