package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.CastedSpellEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastedSpellRepository extends JpaRepository<CastedSpellEvent, Long> {
  Optional<List<CastedSpellEvent>> findByMatchIdAndHeroName(Long id, String name);
}
