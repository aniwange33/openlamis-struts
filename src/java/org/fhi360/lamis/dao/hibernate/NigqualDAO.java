
/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Nigqual;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

public class NigqualDAO {
    public static Long save(Nigqual nigqual) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(nigqual);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }

    public static void update(Nigqual nigqual) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(nigqual);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }
    
}
