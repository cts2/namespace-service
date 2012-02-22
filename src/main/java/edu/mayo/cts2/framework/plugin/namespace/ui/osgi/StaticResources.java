package edu.mayo.cts2.framework.plugin.namespace.ui.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

public class StaticResources extends HttpServlet implements InitializingBean, BundleContextAware {

	private static final long serialVersionUID = -7634371906912420723L;

	@Resource
	private HttpService httpService;
	
	private String alias = "/VAADIN";

	private Bundle bundle;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String path = req.getPathInfo();
		String resourcePath = alias + path;

		URL u = bundle.getResource(resourcePath);
		
		if (null == u) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		InputStream in = u.openStream();
		OutputStream out = resp.getOutputStream();

		byte[] buffer = new byte[1024];
		int read = 0;
		while (-1 != (read = in.read(buffer))) {
			out.write(buffer, 0, read);
		}
	}

	public void afterPropertiesSet() throws Exception {
		httpService.registerServlet(alias, this, null, null);
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundle = bundleContext.getBundle();
	}

}