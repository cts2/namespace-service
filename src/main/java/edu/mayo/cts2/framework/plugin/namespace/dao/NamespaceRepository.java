package edu.mayo.cts2.framework.plugin.namespace.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;

@Repository
@Transactional
public interface NamespaceRepository extends CrudRepository<Namespace, String>{
	
	@Query("SELECT n FROM Namespace n LEFT JOIN n.alternateNames prefix WHERE prefix = ?1 OR n.preferredName = ?1")
	public Namespace findNamespaceByPrefix(String prefix);

}
