package it.cnr.isti.labsedc.bpmls.persistance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningStatementJpaRepository extends JpaRepository<LearningStatementRef, Integer> {
	List<LearningStatementRef> findByInstanceIdAndLearningType(String instanceId, String learningType);
}
