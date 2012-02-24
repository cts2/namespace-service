package edu.mayo.cts2.framework.plugin.namespace.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;

public class NamespaceRepositoryTest extends DBClearingTestBase {
	
	@Autowired
	NamespaceRepository namespaceRepository;
	
	@Test
	public void testNotNull(){
		assertNotNull(this.namespaceRepository);
	}
	
	@Test
	public void testInsert(){
		this.namespaceRepository.save(new Namespace("http://my/uri"));
	}
	
	@Test
	public void testContains(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.getAlternateNames().add("one");
		ns1.getAlternateNames().add("two");
		this.namespaceRepository.save(ns1);
		
		Namespace ns2 = this.namespaceRepository.findOne("http://my/uri");
		assertTrue(ns2.getAlternateNames().contains("one"));
		assertTrue(ns2.getAlternateNames().contains("two"));
		
		ns2.getAlternateNames().clear();
		ns2.getAlternateNames().add("two");
		ns2.getAlternateNames().add("one");
		this.namespaceRepository.save(ns2);
		
		Namespace ns3 = this.namespaceRepository.findOne("http://my/uri");
		assertTrue(ns3.getAlternateNames().contains("two"));
		assertTrue(ns3.getAlternateNames().contains("one"));
	}
	
	@Test
	public void testQueryByPrefix(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("prefName");
		ns1.getAlternateNames().add("one");
		ns1.getAlternateNames().add("two");
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("one");
		
		assertEquals(foundNamespace.getPreferredName(), "prefName");
		
	}
	
	@Test
	public void testQueryByPreferredPrefix(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("prefName");
		ns1.getAlternateNames().add("one");
		ns1.getAlternateNames().add("two");
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("prefName");
		
		assertEquals(foundNamespace.getPreferredName(), "prefName");	
	}
	
	@Test
	public void testQueryByPreferredPrefixNoAlternates(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("prefName");
		
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("prefName");
		
		assertEquals(foundNamespace.getPreferredName(), "prefName");
		
	}
	
	@Test
	public void testQueryByPrefixNoneFound(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.getAlternateNames().add("one");
		ns1.getAlternateNames().add("two");
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("__INVALID");
		
		assertNull(foundNamespace);
		
	}

}
