package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.DamageEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DamageEventRepository extends JpaRepository<DamageEvent, Long> {
  Optional<List<DamageEvent>> findByMatchIdAndAssailantName(Long id, String name);
}
