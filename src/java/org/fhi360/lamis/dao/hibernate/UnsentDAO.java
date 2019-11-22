/**
 *
 * @author AALOZIE
 */
package org.fhi360.lamis.dao.hibernate;

import org.fhi360.lamis.model.Unsent;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class UnsentDAO {

    public static void save(Unsent unsent) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            session.save(unsent);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
    }

    public static void delete(Long unsentId) {
        Unsent unsent = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            unsent = (Unsent) session.get(Unsent.class, unsentId);
            if (unsent != null) {
                session.delete(unsent);
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw e;
        }
    }

    public static Unsent find(Long unsentId) {
        Unsent unsent = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();
            unsent = (Unsent) session.get(Unsent.class, unsentId);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw e;
        }
        return unsent;
    }

    public static List<Unsent> list() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<Unsent> messages = new ArrayList<Unsent>();
        try {
            session.beginTransaction();
            messages = (List<Unsent>) session.createQuery("from Unsent").list();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return messages;
    }
}
