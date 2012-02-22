package edu.mayo.cts2.framework.plugin.namespace.service;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;

public interface NamespaceMaintenanceService {

	public void save(MultiNameNamespaceReference namespaceReference);

	public void delete(String uri);
}
