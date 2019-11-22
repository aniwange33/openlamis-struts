/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Dmscreenhistory;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

public class DmscreenhistoryDAO {
    public static Long save(Dmscreenhistory dmscreenhistory) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(dmscreenhistory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }

    public static void update(Dmscreenhistory dmscreenhistory) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(dmscreenhistory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

}
