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
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "hero_kill_event")
public class HeroKillEvent extends Event {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="assailant_id", referencedColumnName = "id", nullable = false)
  private Hero assailant;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="victim_id", referencedColumnName = "id", nullable = false)
  private Hero victim;
}
