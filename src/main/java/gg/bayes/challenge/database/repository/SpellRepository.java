package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.Spell;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpellRepository extends JpaRepository<Spell, Long> {

  Optional<Spell> findByName(String name);
}
