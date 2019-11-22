/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

/**
 * Hibernate Utility class with a convenient method to get Session Factory object.
 *
 * @author user
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;
   
    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception ex) {
            // Log the exception. 
//            System.err.println("Initial SessionFactory creation failed." + ex);
            ex.printStackTrace();
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
