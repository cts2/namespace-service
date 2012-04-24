package edu.mayo.cts2.framework.plugin.namespace.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.service.namespace.NamespaceReadService;

@Component("namespaceReadServiceImpl")
public class NamespaceReadServiceImpl implements NamespaceReadService {

	@Autowired
	private NamespaceRepository namespaceRepository;
	
	@Transactional(readOnly = true)
	public MultiNameNamespaceReference readByUri(String uri) {
		Namespace ns = this.doReadByUri(uri);
		if(ns != null){
			return ns.getMultiNameNamespaceReference();
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public MultiNameNamespaceReference readByLocalName(String localName) {
		Namespace ns = this.doReadByLocalName(localName);
		if(ns != null){
			return ns.getMultiNameNamespaceReference();
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public DocumentedNamespaceReference readPreferredByUri(String uri) {
		Namespace ns = this.doReadByUri(uri);
		if(ns != null){
			return ns.getDocumentedNamespaceReference();
		} else {
			return null;
		}
	}

	public DocumentedNamespaceReference readPreferredByLocalName(String localName) {
		Namespace ns = this.doReadByLocalName(localName);
		if(ns != null){
			return ns.getDocumentedNamespaceReference();
		} else {
			return null;
		}
	}
	
	public Namespace doReadByUri(String uri) {
		Namespace namespace = this.namespaceRepository.findOne(uri);

		if (namespace == null) {
			namespace = this.namespaceRepository.findOne(uri+"#");
		}
		
		if (namespace == null) {
			return null;
		}

		return namespace;
	}

	public Namespace doReadByLocalName(String localName) {
		Namespace namespace = this.namespaceRepository
				.findNamespaceByPrefix(localName);

		if (namespace == null) {
			return null;
		}

		return namespace;
	}
}
