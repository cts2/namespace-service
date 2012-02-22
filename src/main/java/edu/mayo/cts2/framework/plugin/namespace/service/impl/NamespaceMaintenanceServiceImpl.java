package edu.mayo.cts2.framework.plugin.namespace.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.plugin.namespace.service.NamespaceMaintenanceService;

@Component
public class NamespaceMaintenanceServiceImpl implements NamespaceMaintenanceService {

	@Autowired
	private NamespaceRepository namespaceRepository;

	@Transactional(readOnly = false)
	public void save(MultiNameNamespaceReference namespaceReference) {
		this.namespaceRepository.save(new Namespace(namespaceReference));
	}

	@Transactional(readOnly = false)
	public void delete(String uri) {
		this.namespaceRepository.delete(uri);
	}

}
