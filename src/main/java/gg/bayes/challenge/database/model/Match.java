package gg.bayes.challenge.database.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "dota_match")
public class Match {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
//  @OneToMany(mappedBy = "match", cascade = CascadeType.PERSIST)
//  private Set<CastedSpellEvent> castedSpellEvents = new HashSet<>();
//  @OneToMany(mappedBy = "match", cascade = CascadeType.PERSIST)
//  private Set<DamageEvent> damageEvents = new HashSet<>();
//  @OneToMany(mappedBy = "match", cascade = CascadeType.PERSIST)
//  private Set<HeroKillEvent> heroKillEvents = new HashSet<>();
//  @OneToMany(mappedBy = "match", cascade = CascadeType.PERSIST)
//  private Set<ItemPurchaseEvent> itemPurchaseEvents = new HashSet<>();
}
