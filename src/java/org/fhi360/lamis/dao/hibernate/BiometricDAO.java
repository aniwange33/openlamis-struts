/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Biometric;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

/**
 *
 * @author User10
 */
public class BiometricDAO {
    public static Long save(Biometric biometric) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            session.saveOrUpdate(biometric);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }
}
