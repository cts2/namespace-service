package edu.mayo.cts2.framework.plugin.namespace.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Namespace ns2 = this.namespaceRepository.findOne("http://my/uri");
		assertTrue(ns2.getAlternateNames().contains("one"));
		assertTrue(ns2.getAlternateNames().contains("two"));
		
		ns2.getAlternateNames().clear();
		ns2.addAlternateName("two");
		ns2.addAlternateName("one");
		this.namespaceRepository.save(ns2);
		
		Namespace ns3 = this.namespaceRepository.findOne("http://my/uri");
		assertTrue(ns3.getAlternateNames().contains("two"));
		assertTrue(ns3.getAlternateNames().contains("one"));
	}
	
	@Test
	public void testQueryByPrefix(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("prefName");
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("one");
		
		assertEquals(foundNamespace.getPreferredName(), "prefName");
		
	}
	
	@Test
	public void testQueryByPrefixLeftJoinIsCorrect(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("pname");
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("one");
		
		assertNotNull(foundNamespace);
		
		assertEquals(2, foundNamespace.getAlternateNames().size());
		assertTrue(foundNamespace.getAlternateNames().contains("one"));
		assertTrue(foundNamespace.getAlternateNames().contains("two"));
		
		assertEquals("pname", foundNamespace.getPreferredName());
	}
	
	@Test
	public void testQueryByPreferredPrefix(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("prefName");
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
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
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Namespace foundNamespace = this.namespaceRepository.findNamespaceByPrefix("__INVALID");
		
		assertNull(foundNamespace);
		
	}

	@Test
	public void testQueryByAny(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("pname");
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Page<Namespace> foundNamespaces = this.namespaceRepository.findByAny("%o%", new PageRequest(0,10));
		
		assertNotNull(foundNamespaces);
		assertEquals(1, foundNamespaces.getContent().size());
		
	}
	
	@Test
	public void testQueryByAnyLeftJoinIsCorrect(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("pname");
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Page<Namespace> foundNamespaces = this.namespaceRepository.findByAny("%o%", new PageRequest(0,10));
		
		assertNotNull(foundNamespaces);
		assertEquals(1, foundNamespaces.getContent().size());
		
		assertEquals(2, foundNamespaces.getContent().get(0).getAlternateNames().size());
		assertTrue(foundNamespaces.getContent().get(0).getAlternateNames().contains("one"));
		assertTrue(foundNamespaces.getContent().get(0).getAlternateNames().contains("two"));
		
		assertEquals("pname", foundNamespaces.getContent().get(0).getPreferredName());
	}
	
	@Test
	public void testQueryByAnyCaseInsensitive(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.addAlternateName("oNe");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Page<Namespace> foundNamespaces = this.namespaceRepository.findByAny("%ONE%".toLowerCase(), new PageRequest(0,10));
		
		assertNotNull(foundNamespaces);
		assertEquals(1, foundNamespaces.getContent().size());
		
	}
	
	@Test
	public void testQueryByAnyCorrectPage(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("pname");
		ns1.addAlternateName("oNe");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Page<Namespace> foundNamespaces = this.namespaceRepository.findByAny("%o%".toLowerCase(), new PageRequest(0,1));
		
		assertNotNull(foundNamespaces);
		assertEquals(1, foundNamespaces.getContent().size());
		
		assertEquals(2, foundNamespaces.getContent().get(0).getAlternateNames().size());
		assertTrue(foundNamespaces.getContent().get(0).getAlternateNames().contains("oNe"));
		assertTrue(foundNamespaces.getContent().get(0).getAlternateNames().contains("two"));
		
		assertEquals("pname", foundNamespaces.getContent().get(0).getPreferredName());
		
	}
	
	@Test
	public void testFindAllCorrectPage(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("pname");
		ns1.addAlternateName("oNe");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Page<Namespace> foundNamespaces = this.namespaceRepository.findAll(new PageRequest(0,1));
		
		assertNotNull(foundNamespaces);
		assertEquals(1, foundNamespaces.getContent().size());
		
		assertEquals(2, foundNamespaces.getContent().get(0).getAlternateNames().size());
		assertTrue(foundNamespaces.getContent().get(0).getAlternateNames().contains("oNe"));
		assertTrue(foundNamespaces.getContent().get(0).getAlternateNames().contains("two"));
		
		assertEquals("pname", foundNamespaces.getContent().get(0).getPreferredName());
		
	}

}
