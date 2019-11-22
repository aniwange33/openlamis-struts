/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.classic.Session;
import org.fhi360.lamis.model.Monitor;

public class MonitorDAO {
    public static Long save(Monitor monitor) {
       Long id = 0L;
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(monitor);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
       }
       return id;
    }
    
}
