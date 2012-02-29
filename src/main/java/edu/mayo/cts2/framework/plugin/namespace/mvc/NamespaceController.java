package edu.mayo.cts2.framework.plugin.namespace.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.mayo.cts2.framework.core.plugin.PluginConfigManager;
import edu.mayo.cts2.framework.model.core.Directory;
import edu.mayo.cts2.framework.model.core.Parameter;
import edu.mayo.cts2.framework.model.core.RESTResource;
import edu.mayo.cts2.framework.model.core.types.CompleteDirectory;
import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.NamespaceDirectory;
import edu.mayo.cts2.framework.model.service.namespace.NamespaceList;
import edu.mayo.cts2.framework.service.namespace.NamespaceMaintenanceService;
import edu.mayo.cts2.framework.service.namespace.NamespaceQueryService;
import edu.mayo.cts2.framework.service.namespace.NamespaceReadService;
import edu.mayo.cts2.framework.webapp.rest.extensions.controller.ControllerProvider;

@Controller("namespaceControllerProvider")
public class NamespaceController implements ControllerProvider {
	
	@Resource
	private NamespaceReadService namespaceReadService;
	
	@Resource
	private NamespaceMaintenanceService namespaceMaintenanceService;
	
	@Resource
	private NamespaceQueryService namespaceQueryService;
	
	@Autowired(required=false)
	private PluginConfigManager pluginConfigManager;
	
	@RequestMapping(value = { "/namespacebyuri" }, method = RequestMethod.GET)
	@ResponseBody
	public Object getNamespaceByUri(
			HttpServletResponse response, 
			@RequestParam(required=true) String uri,
			@RequestParam(defaultValue="false") boolean all) {
		Object returnObj;
		if(all){
			returnObj = this.namespaceReadService.readByUri(uri);
		} else {
			returnObj =  this.namespaceReadService.readPreferredByUri(uri);
		}
		
		return this.handleResponse(returnObj, response);
	}
	
	@RequestMapping(value = { "/namespaces" }, method = RequestMethod.GET)
	@ResponseBody
	public Directory getNamespaces(
			HttpServletRequest request, 
			@RequestParam(defaultValue="false") boolean all) {
		Directory dir;
		if(all){
			NamespaceList list = new NamespaceList();
			list.setEntry(
					new ArrayList<MultiNameNamespaceReference>(
							this.namespaceQueryService.getAllNamespaces()));
			
			list.setNumEntries((long) list.getEntryCount());
			dir = list;
		} else {
			NamespaceDirectory directory = new NamespaceDirectory();
			directory.setEntry(
					new ArrayList<DocumentedNamespaceReference>(this.namespaceQueryService.getAllPreferredNamespaces()));
		
			directory.setNumEntries((long) directory.getEntryCount());
			dir = directory;
		}
		
		
		return this.populateDirectory("/namespaces", dir, request);
	}
	
	@RequestMapping(value = { "/namespacebyuri" }, method = RequestMethod.POST)
	@ResponseBody
	public void addLocalName(
			HttpServletResponse response, 
			@RequestBody String localName,
			@RequestParam(required=true) String uri,
			@RequestParam(defaultValue="false") boolean preferred) {
		
		this.namespaceMaintenanceService.addLocalName(uri, localName, preferred);
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
			int status = HttpServletResponse.SC_NOT_FOUND;
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
	
	private Directory populateDirectory(
			String url,
			Directory directory, 
			HttpServletRequest request){
		directory.setComplete(CompleteDirectory.COMPLETE);
		
		@SuppressWarnings("unchecked")
		RESTResource heading = this.getHeading(request.getParameterMap(), url);
		
		directory.setHeading(heading);
		
		return directory;
	}
	
	private RESTResource getHeading(Map<Object, Object> parameterMap,
			String resourceUrl) {
		RESTResource resource = new RESTResource();

		for (Entry<Object, Object> param : parameterMap.entrySet()) {
			Parameter headingParam = new Parameter();
			headingParam.setArg(param.getKey().toString());
			headingParam.setVal(getParamValue(param.getValue()));

			resource.addParameter(headingParam);
		}

		resource.setAccessDate(new Date());

		String urlRoot;
		if(this.pluginConfigManager != null){
			urlRoot = this.pluginConfigManager.getServerContext()
					.getServerRootWithAppName();
	
			if (!urlRoot.endsWith("/")) {
				urlRoot = urlRoot + "/";
			}
		} else {
			urlRoot = "";
		}

		String resourceRoot = StringUtils.removeStart(resourceUrl, "/");

		String resourceURI = urlRoot + resourceRoot;

		resource.setResourceURI(resourceURI);

		resource.setResourceRoot(resourceRoot);

		return resource;
	}
	
	private String parameterValueToString(Object param) {
		String paramString;

		if (param.getClass().isArray()) {
			paramString = ArrayUtils.toString(param);
		} else {
			paramString = param.toString().trim();
		}

		if (paramString.startsWith("{")) {
			paramString = paramString.substring(1);
		}

		if (paramString.endsWith("}")) {
			paramString = paramString.substring(0, paramString.length() - 1);
		}

		return paramString;
	}

	/**
	 * Gets the param value.
	 * 
	 * @param value
	 *            the value
	 * @return the param value
	 */
	private String getParamValue(Object value) {
		if (value == null) {
			return null;
		}

		return parameterValueToString(value);
	}

	
}
