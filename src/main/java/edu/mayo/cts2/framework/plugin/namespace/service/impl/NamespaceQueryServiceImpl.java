package edu.mayo.cts2.framework.plugin.namespace.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.service.namespace.NamespaceQueryService;

@Component
public class NamespaceQueryServiceImpl implements NamespaceQueryService {

	@Autowired
	private NamespaceRepository namespaceRepository;

	@Transactional(readOnly = true)
	public Collection<MultiNameNamespaceReference> getAllNamespaces() {
		List<MultiNameNamespaceReference> returnList = new ArrayList<MultiNameNamespaceReference>();

		for (Namespace namespace : this.namespaceRepository.findAll()) {
			returnList.add(namespace.getMultiNameNamespaceReference());
		}

		return returnList;
	}

	@Transactional(readOnly = true)
	public Collection<DocumentedNamespaceReference> getAllPreferredNamespaces() {
		List<DocumentedNamespaceReference> returnList = new ArrayList<DocumentedNamespaceReference>();

		for (Namespace namespace : this.namespaceRepository.findAll()) {
			returnList.add(namespace.getDocumentedNamespaceReference());
		}

		return returnList;
	}

}
