package edu.mayo.cts2.framework.plugin.namespace.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Prefix {

	private boolean preferred;
	
	@Column(unique=true, nullable=false)
	private String prefix;
	
	public Prefix(){
		super();
	}

	public Prefix(String prefix, boolean preferred) {
		super();
		this.preferred = preferred;
		this.prefix = prefix;
	}

	public boolean isPreferred() {
		return preferred;
	}

	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
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
		Prefix other = (Prefix) obj;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		return true;
	}

}
