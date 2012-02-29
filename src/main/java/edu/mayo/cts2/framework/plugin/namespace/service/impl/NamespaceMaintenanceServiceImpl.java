package edu.mayo.cts2.framework.plugin.namespace.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.service.namespace.NamespaceMaintenanceService;

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
		if(this.namespaceRepository.exists(uri)){
			this.namespaceRepository.delete(uri);
		}
	}

	@Transactional(readOnly = false)
	public void addLocalName(String uri, String localName, boolean setPreferred) {
		Assert.hasText(localName, "'LocalName' must not be empty.");
		
		Namespace namespace = this.namespaceRepository.findOne(uri);
		if(namespace == null){
			namespace = new Namespace(uri);
			namespace.setPreferredName(localName);
			
			this.namespaceRepository.save(namespace);
		} else {
			//check if is already there
			if(! StringUtils.equals(namespace.getPreferredName(), localName)){
				
				//check if it is an alternate name
				if(namespace.getAlternateNames().contains(localName)){
					if(setPreferred){
						namespace.removeName(localName);
						namespace.setPreferredName(localName);
						
						this.namespaceRepository.save(namespace);
					}	
				} else {
					if(setPreferred){
						String currentPreferred = namespace.getPreferredName();
						if(StringUtils.isNotBlank(currentPreferred)){
							namespace.addAlternateName(currentPreferred);
						}
						namespace.setPreferredName(localName);
					} else {
						String currentPreferred = namespace.getPreferredName();
						if(StringUtils.isBlank(currentPreferred)){
							namespace.setPreferredName(localName);
						} else {
							namespace.addAlternateName(localName);
						}
					}
					
					this.namespaceRepository.save(namespace);
				}
			}
		}
	}
}
