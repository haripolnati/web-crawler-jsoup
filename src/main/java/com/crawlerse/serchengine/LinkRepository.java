package com.crawlerse.serchengine;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
@Repository
public interface LinkRepository
        extends JpaRepository<LinkModel, Long> {
	
	@Query("from LinkModel c where c.link like %:word%")
	List<LinkModel> findByLinkLikeIgnoreCase(@Param("word") String word);
	
	@Query("from LinkModel c where c.link like %:fword% and c.link like %:sword%" )
	List<LinkModel> findByLinkLikeMultipleIgnoreCase(@Param("fword") String fword,  @Param("sword") String sword);
 
}
