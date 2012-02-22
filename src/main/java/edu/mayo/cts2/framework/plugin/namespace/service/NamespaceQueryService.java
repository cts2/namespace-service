package edu.mayo.cts2.framework.plugin.namespace.service;

import java.util.Collection;

import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;

public interface NamespaceQueryService {

	public Collection<MultiNameNamespaceReference> getAllNamespaces();
	
	public Collection<DocumentedNamespaceReference> getAllPreferredNamespaces();
		
}
