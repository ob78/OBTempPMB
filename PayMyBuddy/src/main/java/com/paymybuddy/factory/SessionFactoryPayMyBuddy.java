package com.paymybuddy.factory;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionFactoryPayMyBuddy {

	private static Session session = null;
	
	public static Session getSession() {
		if(session == null) {
			session = getSessionInstance();
		}
		return session;
	}	
	
	private static Session getSessionInstance() {
		
		File configFile = new File("hibernateAnnotation.cfg.xml");

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
														.configure(configFile)
														.build();
		
		SessionFactory sessionFactory = new MetadataSources(registry)
				                            .buildMetadata()
				                            .buildSessionFactory();

		session = sessionFactory.openSession();
	
		return session;
	}
	
}
