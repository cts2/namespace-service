package edu.mayo.cts2.framework.plugin.namespace.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.plugin.namespace.dao.DBClearingTestBase;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.service.namespace.NamespaceMaintenanceService;

public class NamespaceMaintenanceServiceImplTest extends DBClearingTestBase {
	
	@Autowired
	NamespaceMaintenanceService namespaceMaintenanceServiceImpl;
	
	@Autowired
	NamespaceRepository namespaceRepository;
	
	@Before
	@Transactional
	public void insert(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.setPreferredName("prefname");
		ns1.addAlternateName("one");
		ns1.addAlternateName("two");
		this.namespaceRepository.save(ns1);
		
		Namespace ns2 = new Namespace("http://my/uri2");
		ns2.addAlternateName("three");
		ns2.addAlternateName("four");
		this.namespaceRepository.save(ns2);
	}
	
	@Test
	public void testNotNull(){
		assertNotNull(this.namespaceMaintenanceServiceImpl);
	}
	
	@Test
	public void TestAddLocalNamePreferredTrue(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri", "ln2", true);
		
		Namespace ns = this.namespaceRepository.findOne("http://my/uri");
		
		assertEquals("ln2", ns.getPreferredName());
		assertEquals(3, ns.getAlternateNames().size());
		assertTrue(ns.getAlternateNames().containsAll(Arrays.asList("prefname", "one", "two")));
	}
	
	@Test
	public void TestAddLocalNameIdempotency(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri", "ln2", false);
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri", "ln2", false);
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri", "ln2", false);
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri", "ln2", false);
		
		Namespace ns = this.namespaceRepository.findOne("http://my/uri");
		
		assertEquals("prefname", ns.getPreferredName());
		assertEquals(3, ns.getAlternateNames().size());
		assertTrue(ns.getAlternateNames().containsAll(Arrays.asList("ln2", "one", "two")));
	}
	
	@Test
	public void TestAddLocalNamePreferredNoneLoadedPrefFalse(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri_NONE", "name", false);
		
		Namespace ns = this.namespaceRepository.findOne("http://my/uri_NONE");
		
		assertEquals("name", ns.getPreferredName());
		assertEquals(0, ns.getAlternateNames().size());
	}
	
	@Test
	public void TestAddLocalNamePreferredNoneLoadedPrefTrue(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri_NONE", "name", true);
		
		Namespace ns = this.namespaceRepository.findOne("http://my/uri_NONE");
		
		assertEquals("name", ns.getPreferredName());
		assertEquals(0, ns.getAlternateNames().size());
	}
	
	@Test
	public void TestAddLocalNamePreferredNoPrefNamePrefTrue(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri2", "newPrefName", true);
		
		Namespace ns = this.namespaceRepository.findOne("http://my/uri2");
		
		assertEquals("newPrefName", ns.getPreferredName());
		assertEquals(2, ns.getAlternateNames().size());
	}
	
	@Test
	public void TestAddLocalNamePreferredNoPrefNamePrefFalse(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri2", "newPrefName", false);
		
		Namespace ns = this.namespaceRepository.findOne("http://my/uri2");
		
		assertEquals("newPrefName", ns.getPreferredName());
		assertEquals(2, ns.getAlternateNames().size());
	}
	
	@Test(expected=DataIntegrityViolationException.class)
	public void TestAddLocalNameDuplicate(){
		this.namespaceMaintenanceServiceImpl.addLocalName("http://my/uri2", "one", false);
		
	}
	
	
}
