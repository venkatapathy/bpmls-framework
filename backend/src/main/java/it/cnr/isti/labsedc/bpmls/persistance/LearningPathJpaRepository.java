package it.cnr.isti.labsedc.bpmls.persistance;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningPathJpaRepository extends JpaRepository<LearningPathInstance, Integer> {

	LearningPathInstance findByLpInstId(int lpInstId);
	
	LearningPathInstance findOneByLpIdAndStatus(String lpId,String Status);
	
	List<LearningPathInstance> findByStatus(String status);
	
	
}
