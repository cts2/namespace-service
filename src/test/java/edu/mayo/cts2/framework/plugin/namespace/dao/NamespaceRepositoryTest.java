package edu.mayo.cts2.framework.plugin.namespace.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value="classpath:/META-INF/spring/namespace-manager-context.xml")
public class NamespaceRepositoryTest {
	
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
	@Transactional
	public void testOrder(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.getAlternateNames().add("one");
		ns1.getAlternateNames().add("two");
		this.namespaceRepository.save(ns1);
		
		Namespace ns2 = this.namespaceRepository.findOne("http://my/uri");
		assertEquals(ns2.getAlternateNames().get(0), "one");
		assertEquals(ns2.getAlternateNames().get(1), "two");
		
		ns2.getAlternateNames().clear();
		ns2.getAlternateNames().add("two");
		ns2.getAlternateNames().add("one");
		this.namespaceRepository.save(ns2);
		
		Namespace ns3 = this.namespaceRepository.findOne("http://my/uri");
		assertEquals(ns3.getAlternateNames().get(0), "two");
		assertEquals(ns3.getAlternateNames().get(1), "one");
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
