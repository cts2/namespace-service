package edu.mayo.cts2.framework.plugin.namespace.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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

	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Prefix> prefixes = new HashSet<Prefix>();
	
	public Namespace(){
		super();
	}

	public Namespace(MultiNameNamespaceReference multiNameNamespaceReference){
		super();
		this.setUri(multiNameNamespaceReference.getUri());
		this.setPreferredName(multiNameNamespaceReference.getPreferredName());
		this.setAlternateNames(new HashSet<String>(Arrays.asList(multiNameNamespaceReference.getAlternateName())));
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
		for(Prefix prefix : this.prefixes){
			if(prefix.isPreferred()){
				return prefix.getPrefix();
			}
		}
		
		return null;
	}

	public void setPreferredName(String preferredName) {
		for(Prefix prefix : this.prefixes){
			prefix.setPreferred(false);
		}
		this.prefixes.add(new Prefix(preferredName, true));	
	}

	public Set<String> getAlternateNames() {
		Set<String> alternameNames = new HashSet<String>();
		for(Prefix prefix : this.prefixes){
			if(! prefix.isPreferred()){
				alternameNames.add(prefix.getPrefix());
			}
		}
		
		return alternameNames;
	}
	
	public void addAlternateName(String alternateName) {
		this.prefixes.add(new Prefix(alternateName,false));
	}

	public void setAlternateNames(Set<String> alternateNames) {
		Set<Prefix> prefixes = new HashSet<Prefix>();
		for(Prefix prefix : this.prefixes){
			if(prefix.isPreferred()){
				prefixes.add(prefix);
			}
		}
		
		for(String altName : alternateNames){
			prefixes.add(new Prefix(altName, false));
		}
		
		this.prefixes = prefixes;
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

	public void removeName(String localName) {
		this.prefixes.remove(new Prefix(localName, false));
	}
	
	
}
