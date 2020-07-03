package com.paymybuddy.factory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	   private static SessionFactory sessionFactory;
	   
	   public static SessionFactory getSessionFactory() {
	     if (sessionFactory == null)
	       throw new IllegalStateException("Le sessionFactory n'a pas été initialisé.");
	     return sessionFactory;
	   }
	   
	   public static void initialize() {
	     Configuration config = new Configuration();
	     config.configure("persistence.xml");
	     sessionFactory = config.buildSessionFactory();
	   }
	 }