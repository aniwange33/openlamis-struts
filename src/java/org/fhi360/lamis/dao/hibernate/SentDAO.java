/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Sent;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class SentDAO {

    public static void save(Sent sent) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.save(sent);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long sentId) {
        Sent sent = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            sent = (Sent) session.get(Sent.class, sentId);
            if (sent != null) {
                session.delete(sent);
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw e;
        }
    }

    public static void delete(String phone) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            List<Sent> sentMessages = (List<Sent>) session.createQuery("from Sent where phone = :p").setString("p", phone).list();
            if (!sentMessages.isEmpty()) {
                for (Sent message : sentMessages) {
                    session.delete(message);
                }
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw e;
        }
    }
    
    public static int count() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        int count = 0;
        try {
            session.beginTransaction();
            count = (Integer) session.createQuery("from Sent").list().size();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return count;
    }
}
