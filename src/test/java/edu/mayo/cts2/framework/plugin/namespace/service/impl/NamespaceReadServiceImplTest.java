package edu.mayo.cts2.framework.plugin.namespace.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.plugin.namespace.dao.DBClearingTestBase;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.service.namespace.NamespaceReadService;

public class NamespaceReadServiceImplTest extends DBClearingTestBase {
	
	@Autowired
	NamespaceReadService namespaceReadServiceImpl;
	
	@Autowired
	NamespaceRepository namespaceRepository;
	
	@Before
	@Transactional
	public void insert(){
		Namespace ns1 = new Namespace("http://my/uri");
		ns1.getAlternateNames().add("one");
		ns1.getAlternateNames().add("two");
		this.namespaceRepository.save(ns1);
	}
	
	@Test
	public void testNotNull(){
		assertNotNull(this.namespaceReadServiceImpl);
	}
	
	@Test
	public void testReadByUri(){
		assertNotNull(namespaceReadServiceImpl.readPreferredByUri("http://my/uri"));
	}
	
	@Test
	public void testReadByUriNotFound(){
		assertNull(namespaceReadServiceImpl.readPreferredByUri("__INVALID__"));
	}
	
	@Test
	public void testReadByLocalName() throws Exception{
		assertNotNull(namespaceReadServiceImpl.readPreferredByLocalName("one"));
	}
	
	@Test
	public void testReadByLocalNameNotFound(){
		assertNull(namespaceReadServiceImpl.readPreferredByLocalName("__INVALID"));
	}

}
