package it.cnr.isti.labsedc.bpmls.persistance;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningScenarioJpaRepository extends JpaRepository<LearningScenarioInstance, Integer>{
	List<LearningScenarioInstance> findBylpInstanceAndStatusOrderByOrderinLP(LearningPathInstance lpInst,String status);
	LearningScenarioInstance findOneByProcessInstanceId(String processInstanceId);
}
