package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.Hero;
import gg.bayes.challenge.database.model.HeroKillEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KillEventRepository extends JpaRepository<HeroKillEvent, Long> {
  Optional<List<HeroKillEvent>> findByMatchId(Long id);
}
