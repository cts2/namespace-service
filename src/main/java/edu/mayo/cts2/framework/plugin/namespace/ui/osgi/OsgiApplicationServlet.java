package edu.mayo.cts2.framework.plugin.namespace.ui.osgi;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.http.HttpService;
import org.springframework.beans.factory.InitializingBean;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

public class OsgiApplicationServlet extends AbstractApplicationServlet
		implements InitializingBean {

	private static final long serialVersionUID = 6500019134108546540L;

	@Resource
	private Application application;

	@Resource
	private HttpService httpService;

	@Override
	protected Application getNewApplication(HttpServletRequest request)
			throws ServletException {
		return this.application;
	}

	@Override
	protected Class<? extends Application> getApplicationClass() {
		return this.application.getClass();
	}

	public void afterPropertiesSet() throws Exception {
		this.httpService.registerServlet("/namespace/console", this, null, null);
	}

}
