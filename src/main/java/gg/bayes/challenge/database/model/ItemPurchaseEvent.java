package gg.bayes.challenge.database.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "item_purchase_event")
public class ItemPurchaseEvent extends Event {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hero_id", referencedColumnName = "id", nullable = false)
  private Hero hero;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id", referencedColumnName = "id", nullable = false)
  private Item item;
}
