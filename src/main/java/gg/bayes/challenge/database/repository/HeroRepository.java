package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.Hero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
  Optional<Hero> findByName(String name);
}
