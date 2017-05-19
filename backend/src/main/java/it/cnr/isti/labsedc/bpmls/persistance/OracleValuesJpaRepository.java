package it.cnr.isti.labsedc.bpmls.persistance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OracleValuesJpaRepository extends JpaRepository<OracleValue, Integer>{
	OracleValue findBylsInstanceAndBpmnCamId(LearningScenarioInstance lsInstance,String bpmnCamId);
	List<OracleValue> findAllByLsInstance(LearningScenarioInstance lsInstance);
}
