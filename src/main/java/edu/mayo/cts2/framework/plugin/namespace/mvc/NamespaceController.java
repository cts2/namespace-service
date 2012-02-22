package edu.mayo.cts2.framework.plugin.namespace.mvc;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.plugin.namespace.service.NamespaceMaintenanceService;
import edu.mayo.cts2.framework.plugin.namespace.service.NamespaceReadService;
import edu.mayo.cts2.framework.webapp.rest.extensions.controller.ControllerProvider;

@Controller("namespaceControllerProvider")
public class NamespaceController implements ControllerProvider {
	
	@Resource
	private NamespaceReadService namespaceReadService;
	
	@Resource
	private NamespaceMaintenanceService namespaceMaintenanceService;
	
	@RequestMapping(value = { "/namespacebyuri" }, method = RequestMethod.GET)
	@ResponseBody
	public Object getNamespaceByUri(
			HttpServletResponse response, 
			@RequestParam(required=false) String uri,
			@RequestParam(defaultValue="false") boolean all) {
		Object returnObj;
		if(all){
			returnObj = this.namespaceReadService.readByUri(uri);
		} else {
			returnObj =  this.namespaceReadService.readPreferredByUri(uri);
		}
		
		return this.handleResponse(returnObj, response);
	}
	
	@RequestMapping(value = { "/namespace/{prefix}" }, method = RequestMethod.GET)
	@ResponseBody
	public Object getNamespaceByPrefix(
			HttpServletResponse response, 
			@PathVariable String prefix,
			@RequestParam(defaultValue="false") boolean all) {
		Object returnObj;
		if(all){
			returnObj = this.namespaceReadService.readByLocalName(prefix);
		} else {
			returnObj = this.namespaceReadService.readPreferredByLocalName(prefix);
		}
		
		return this.handleResponse(returnObj, response);
	}
	
	@RequestMapping(value = { "/namespace" }, method = RequestMethod.POST)
	public ResponseEntity<Void> createNamespace(
			HttpServletResponse response, 
			MultiNameNamespaceReference multiNameNamespaceReference) {
		this.namespaceMaintenanceService.save(multiNameNamespaceReference);
		
		response.setStatus(HttpServletResponse.SC_CREATED);
		
		return this.createResponseEntity("/namespacebyuri?uri=" + multiNameNamespaceReference.getUri());
	}
	
	private Object handleResponse(Object result, HttpServletResponse response){
		if(result == null){
			int status = HttpServletResponse.SC_NOT_IMPLEMENTED;
			response.setStatus(status);
		}
		
		return result;
	}

	protected <R> ResponseEntity<Void> createResponseEntity(
			String location){
		HttpHeaders responseHeaders = new HttpHeaders();
		
		location = StringUtils.removeStart(location, "/");
		
		responseHeaders.set("Location", location);
		
		return new ResponseEntity<Void>(responseHeaders, HttpStatus.CREATED);
	}
	
	@Override
	public Object getController() {
		return this;
	}
}
