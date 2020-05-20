package gg.bayes.challenge.database.repository;

import gg.bayes.challenge.database.model.ItemPurchaseEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPurchaseRepository extends JpaRepository<ItemPurchaseEvent, Long> {
  Optional<List<ItemPurchaseEvent>> findByMatchIdAndHeroNameOrderByCreatedAsc(Long id, String name);
}
