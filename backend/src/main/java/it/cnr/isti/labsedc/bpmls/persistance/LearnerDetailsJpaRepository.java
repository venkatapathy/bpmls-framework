package it.cnr.isti.labsedc.bpmls.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearnerDetailsJpaRepository extends JpaRepository<LearnerDetails, Integer> {
	LearnerDetails findByUsername(String username);
}
