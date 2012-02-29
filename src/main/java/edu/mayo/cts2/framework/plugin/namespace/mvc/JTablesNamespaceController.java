package edu.mayo.cts2.framework.plugin.namespace.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.dao.NamespaceRepository;
import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;
import edu.mayo.cts2.framework.service.namespace.NamespaceMaintenanceService;
import edu.mayo.cts2.framework.service.namespace.NamespaceReadService;
import edu.mayo.cts2.framework.webapp.rest.extensions.controller.ControllerProvider;

@Controller("jtablesNamespaceControllerProvider")
public class JTablesNamespaceController implements ControllerProvider, InitializingBean {
	
	@Resource
	private NamespaceRepository namespaceRepository;
	
	@Resource
	private NamespaceReadService namespaceReadService;
	
	@Resource
	private NamespaceMaintenanceService namespaceMaintenanceService;

	@Override
	public void afterPropertiesSet() throws Exception {
		//no-op
	}

	@RequestMapping(value = { "/jtables/deletenamespace" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesResponse deleteNamespaceForJTables(
			@RequestParam("uri") String uri) {
		
		this.namespaceMaintenanceService.delete(uri);

		return new JTablesResponse();
	}
	
	@RequestMapping(value = { "/jtables/createnamespace" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesResponse createNamespaceForJTables(
			@RequestParam("uri") String uri,
			@RequestParam("preferredName") String preferredName) {
		
		if(this.namespaceRepository.exists(uri)){
			return new JTablesErrorResponse("A namespace with this URI already exists.");
		}
		
		if(this.namespaceRepository.findNamespaceByPrefix(preferredName) != null){
			return new JTablesErrorResponse("A namespace with this name already exists.");
		}
	
		MultiNameNamespaceReference namespace = new MultiNameNamespaceReference();
		namespace.setPreferredName(preferredName);
		namespace.setUri(uri);
			
		this.namespaceMaintenanceService.save(namespace);

		return new JTablesSingleResponse(namespace);
	}
	
	@RequestMapping(value = { "/jtables/updatenamespace" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesResponse updateNamespaceForJTables(
			@RequestParam("uri") String uri,
			@RequestParam("preferredName") String preferredName) {
	
		Namespace namespace = this.namespaceRepository.findOne(uri);
		namespace.setPreferredName(preferredName);
		
		if(this.namespaceRepository.findNamespaceByPrefix(preferredName) != null){
			return new JTablesErrorResponse("A namespace with this name already exists.");
		}
			
		this.namespaceRepository.save(namespace);

		return new JTablesSingleResponse(namespace.getMultiNameNamespaceReference());
	}
	
	@RequestMapping(value = { "/jtables/listnamespaces" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesListResponse getNamespacesForJTables(
			@RequestParam(required=false, value="searchText") String searchText,
			@RequestParam("jtStartIndex") int jtStartIndex,
			@RequestParam("jtPageSize") int jtPageSize) {
		
		Pageable page = new PageRequest(jtStartIndex / jtPageSize,jtPageSize);
		
		Page<Namespace> result;
		
		if(StringUtils.isBlank(searchText)){
			result = this.namespaceRepository.
				findAll(page);
		} else {
			result = this.namespaceRepository.
				findByAny("%"+searchText.toLowerCase()+"%", page);
		}
		
		List<Object> namespaces = new ArrayList<Object>();
		for(Namespace namespace : result){
			namespaces.add(namespace.getMultiNameNamespaceReference());
		}

		return new JTablesListResponse(result.getTotalElements(), namespaces);
	}
	
	@RequestMapping(value = { "/jtables/listalternatenames" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesListResponse getAlternateNamesForJTables(
			@RequestParam("uri") String uri) {
	
		List<Object> list = new ArrayList<Object>();
		
		MultiNameNamespaceReference namespace = this.namespaceReadService.readByUri(uri);
		for(String alternateName : namespace.getAlternateName()){
			list.add(new AlternateNameWrapper(alternateName));
		}
		
		return new JTablesListResponse(list.size(), list);
	}
	
	@RequestMapping(value = { "/jtables/deletealternatename" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesResponse deleteAlternateNameForJTables(
			@RequestParam("uri") String uri,
			@RequestParam("originalAlternateName") String alternateName) {
		
		MultiNameNamespaceReference namespace = this.namespaceReadService.readByUri(uri);
		
		namespace.removeAlternateName(alternateName);
		
		this.namespaceMaintenanceService.save(namespace);

		return new JTablesResponse();
	}
	
	@RequestMapping(value = { "/jtables/updatealternatename" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesResponse updateAlternateNameForJTables(
			@RequestParam("uri") String uri,
			@RequestParam("originalAlternateName") String alternateName,
			@RequestParam("newAlternateName") String newAlternateName) {
		
		MultiNameNamespaceReference namespace = this.namespaceReadService.readByUri(uri);
		
		namespace.removeAlternateName(alternateName);
		namespace.addAlternateName(newAlternateName);
		
		if(namespace.getAlternateNameAsReference().contains(newAlternateName)){
			return new JTablesErrorResponse("This alternate name already exists for this Namespace.");
		}
		
		try {
			this.namespaceMaintenanceService.save(namespace);
		} catch (DataIntegrityViolationException e) {
			return new JTablesErrorResponse("This alternate name already exists for another Namespace.");
		}

		return new JTablesSingleResponse(new AlternateNameWrapper(newAlternateName));
	}
	
	@RequestMapping(value = { "/jtables/createalternatename" }, method = RequestMethod.POST)
	@ResponseBody
	public JTablesResponse createAlternateNameForJTables(
			@RequestParam("uri") String uri,
			@RequestParam("newAlternateName") String newAlternateName) {
		
		MultiNameNamespaceReference namespace = this.namespaceReadService.readByUri(uri);

		if(namespace.getAlternateNameAsReference().contains(newAlternateName)){
			return new JTablesErrorResponse("This alternate name already exists for this Namespace.");
		}
		
		namespace.addAlternateName(newAlternateName);
		
		try {
			this.namespaceMaintenanceService.save(namespace);
		} catch (DataIntegrityViolationException e) {
			return new JTablesErrorResponse("This alternate name already exists for another Namespace.");
		}

		return new JTablesSingleResponse(new AlternateNameWrapper(newAlternateName));
	}

	public static class AlternateNameWrapper {
		
		private String originalAlternateName;
		private String newAlternateName;
		
	
		public AlternateNameWrapper(String alternateName) {
			super();
			this.originalAlternateName = alternateName;
			this.newAlternateName = alternateName;
		}

		public String getOriginalAlternateName() {
			return originalAlternateName;
		}
	
		public void setOriginalAlternateName(String originalAlternateName) {
			this.originalAlternateName = originalAlternateName;
		}
		
		public String getNewAlternateName() {
			return newAlternateName;
		}
	
		public void setNewAlternateName(String newAlternateName) {
			this.newAlternateName = newAlternateName;
		}

	}


	@Override
	public Object getController() {
		return this;
	}

	
}
