package edu.mayo.cts2.framework.plugin.namespace.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.mayo.cts2.framework.plugin.namespace.model.Namespace;

@Repository
@Transactional
public interface NamespaceRepository extends CrudRepository<Namespace, String>{
	
	@Query("SELECT n FROM Namespace n LEFT JOIN n.prefixes prefixes WHERE prefixes.prefix = ?1")
	public Namespace findNamespaceByPrefix(String prefix);
	
	@Query("SELECT n FROM Namespace n LEFT JOIN n.prefixes prefixes " +
			"WHERE LOWER(n.uri) LIKE ?1 OR LOWER(prefixes.prefix) LIKE ?1 GROUP BY n.uri")
	public Page<Namespace> findByAny(String query, Pageable pageable);
	
	public Page<Namespace> findAll(Pageable pageable);
}
