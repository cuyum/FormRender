package ar.com.cuyum.cnc.utils;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;

import net.sf.ehcache.CacheManager;

@Singleton
public class CacheManagerProducer {

	private static CacheManager instance;
	
	@PostConstruct
	private void init() {
		URL url = getClass().getResource("/ehcache-listasremotas.xml");
		instance=CacheManager.newInstance(url);
		
	}
	
	@Produces
	public CacheManager getCacheManager() {
		return instance;
	}
	
	@PreDestroy
	private void close() {
		instance.shutdown();
	}
	
}
