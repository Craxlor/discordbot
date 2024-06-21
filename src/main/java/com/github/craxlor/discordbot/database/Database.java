package com.github.craxlor.discordbot.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.craxlor.discordbot.database.entity.AutoroomChannel;
import com.github.craxlor.discordbot.database.entity.AutoroomTrigger;
import com.github.craxlor.discordbot.database.entity.Guild;
import com.github.craxlor.discordbot.database.entity.RedditTask;
import com.github.craxlor.discordbot.database.entity.YouTubeSearchData;
import com.github.craxlor.discordbot.database.entity.YouTubeVideoData;

public class Database {
	private static SessionFactory sessionFactory;
	private static final Logger logger = LoggerFactory.getLogger("org.hibernate");

	static {
		try {
			Configuration configuration = new Configuration();
			configuration.configure();
			configuration.addAnnotatedClass(AutoroomChannel.class);
			configuration.addAnnotatedClass(AutoroomTrigger.class);
			configuration.addAnnotatedClass(Guild.class);
			configuration.addAnnotatedClass(RedditTask.class);
			configuration.addAnnotatedClass(YouTubeSearchData.class);
			configuration.addAnnotatedClass(YouTubeVideoData.class);

			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			logger.info("SessionFactory created");
		} catch (Exception e) {
			logger.error("SessionFactory couldn't be created.", e);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
