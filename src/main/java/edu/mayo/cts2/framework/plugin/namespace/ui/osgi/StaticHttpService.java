/*
 * Copyright: (c) 2004-2011 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.cts2.framework.plugin.namespace.ui.osgi;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.InitializingBean;

/**
 * A simple Servlet for serving up static content.
 *
 * @author <a href="mailto:kevin.peterson@mayo.edu">Kevin Peterson</a>
 */
public class StaticHttpService extends GenericServlet implements InitializingBean {

	private static final long serialVersionUID = -420835724412643550L;
	
	@Resource
	private HttpService httpService;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.httpService.registerServlet("/namespaces", this, null, null);
		this.httpService.registerResources("/namespaces/resources", "/web/resources", null);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void service(final ServletRequest req, final ServletResponse res)
			throws ServletException, IOException {
		// don't really expect to be called within a non-HTTP environment
		service((HttpServletRequest) req, (HttpServletResponse) res);

		// ensure response has been sent back and response is committed
		// (we are authorative for our URL space and no other servlet should
		// interfere)
		res.flushBuffer();
	}

	/**
	 * Service the request. The only path recognized by this servlet is
	 * \/editor/console - all others will be delegated to OSGi resource handling.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// check whether we are not at .../{webManagerRoot}
		final String pathInfo = request.getPathInfo();

		if (pathInfo.equals("/console")) {
			InputStream in = this.getClass().getResourceAsStream("/web/index.html");
			
			response.setContentType("text/html");

			IOUtils.copy(in, response.getWriter());
		}
	}
}
