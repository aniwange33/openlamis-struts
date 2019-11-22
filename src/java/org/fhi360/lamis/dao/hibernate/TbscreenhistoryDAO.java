/**
 *
 * @author Alex
 */

package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Tbscreenhistory;
import org.hibernate.HibernateException;
import org.hibernate.classic.Session;

public class TbscreenhistoryDAO {
    public static Long save(Tbscreenhistory tbscreenhistory) {
       Long id = 0L; 
       Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
       try {
            session.beginTransaction();
            id = (Long) session.save(tbscreenhistory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
       return id;
    }

    public static void update(Tbscreenhistory tbscreenhistory) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.update(tbscreenhistory);
            session.getTransaction().commit();
        } 
        catch (HibernateException e){
            session.getTransaction().rollback();
            throw e;
        }
    }

}
