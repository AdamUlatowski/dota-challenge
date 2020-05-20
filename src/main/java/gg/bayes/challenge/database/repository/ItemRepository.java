package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.Item;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

  Optional<Item> findByName(String name);
}
