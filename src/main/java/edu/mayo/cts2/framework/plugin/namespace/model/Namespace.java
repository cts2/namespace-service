package edu.mayo.cts2.framework.plugin.namespace.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

import edu.mayo.cts2.framework.model.service.core.DocumentedNamespaceReference;
import edu.mayo.cts2.framework.model.service.namespace.MultiNameNamespaceReference;

@Entity
public class Namespace {
	
	@Id
	private String uri;
	
	@Column(unique=true)
	private String preferredName;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> alternateNames = new HashSet<String>();
	
	public Namespace(){
		super();
	}

	public Namespace(MultiNameNamespaceReference multiNameNamespaceReference){
		super();
		this.setUri(multiNameNamespaceReference.getUri());
		this.preferredName = multiNameNamespaceReference.getPreferredName();
		this.alternateNames.addAll(Arrays.asList(multiNameNamespaceReference.getAlternateName()));
	}
	
	public Namespace(String uri){
		super();
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public Set<String> getAlternateNames() {
		return alternateNames;
	}

	public void setAlternateNames(Set<String> alternateNames) {
		this.alternateNames = alternateNames;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Namespace other = (Namespace) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	public DocumentedNamespaceReference getDocumentedNamespaceReference() {
		DocumentedNamespaceReference ref = new DocumentedNamespaceReference();
		ref.setContent(this.getPreferredName());
		ref.setUri(this.getUri());
		
		return ref;
	}
	
	public MultiNameNamespaceReference getMultiNameNamespaceReference(){
		MultiNameNamespaceReference ref = new MultiNameNamespaceReference();
		ref.setUri(this.getUri());
		ref.setPreferredName(this.getPreferredName());
		ref.setAlternateName(this.getAlternateNames().toArray(new String[this.getAlternateNames().size()]));
		
		return ref;
	}
	
	
}
