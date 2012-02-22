package edu.mayo.cts2.framework.plugin.namespace.service;

import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;

public interface NamespaceReadService {

	public DocumentedNamespaceReference readPreferredByUri(String uri);
	
	public DocumentedNamespaceReference readPreferredByLocalName(String localName);
	
	public MultiNameNamespaceReference readByUri(String uri);
	
	public MultiNameNamespaceReference readByLocalName(String localName);
}
