package gg.bayes.challenge.database.model;

import java.time.LocalTime;
import javax.persistence.Basic;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Basic
  public LocalTime created;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "match_id", referencedColumnName = "id", nullable = false)
  private Match match;
}
