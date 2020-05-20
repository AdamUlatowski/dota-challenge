package gg.bayes.challenge.database.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@Entity(name = "casted_spell_event")
public class CastedSpellEvent extends Event {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="hero_id", referencedColumnName = "id", nullable = false)
  private Hero hero;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="spell_id", referencedColumnName = "id", nullable = false)
  private Spell spell;
}
