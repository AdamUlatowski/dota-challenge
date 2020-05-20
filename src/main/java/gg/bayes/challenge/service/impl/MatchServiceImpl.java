package gg.bayes.challenge.service.impl;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import gg.bayes.challenge.database.model.CastedSpellEvent;
import gg.bayes.challenge.database.model.DamageEvent;
import gg.bayes.challenge.database.model.Hero;
import gg.bayes.challenge.database.model.HeroKillEvent;
import gg.bayes.challenge.database.model.Item;
import gg.bayes.challenge.database.model.ItemPurchaseEvent;
import gg.bayes.challenge.database.model.Match;
import gg.bayes.challenge.database.model.Spell;
import gg.bayes.challenge.database.repository.CastedSpellRepository;
import gg.bayes.challenge.database.repository.DamageEventRepository;
import gg.bayes.challenge.database.repository.HeroRepository;
import gg.bayes.challenge.database.repository.ItemPurchaseRepository;
import gg.bayes.challenge.database.repository.ItemRepository;
import gg.bayes.challenge.database.repository.KillEventRepository;
import gg.bayes.challenge.database.repository.MatchRepository;
import gg.bayes.challenge.database.repository.SpellRepository;
import gg.bayes.challenge.parser.MatchPayloadParser;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItems;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

  public static final String CAST_SPELL = "casts ability";
  public static final String HIT = "hits";
  public static final String PURCHASE_ITEM = "buys item";
  public static final String HERO_KILL = "is killed by";
  private final MatchPayloadParser payloadParser;
  private final MatchRepository matchRepository;
  private final HeroRepository heroRepository;
  private final ItemRepository itemRepository;
  private final SpellRepository spellRepository;
  private final DamageEventRepository damageEventRepository;
  private final CastedSpellRepository castedSpellRepository;
  private final KillEventRepository killEventRepository;
  private final ItemPurchaseRepository itemPurchaseRepository;
  private List<String> matchers = List.of(CAST_SPELL, HIT, PURCHASE_ITEM, HERO_KILL);

  @Override
  public Long ingestMatch(String payload) {
    Set<CastedSpellEvent> castedSpellEvents = new HashSet<>();
    Set<DamageEvent> damageEvents = new HashSet<>();
    Set<HeroKillEvent> heroKillEvents = new HashSet<>();
    Set<ItemPurchaseEvent> itemPurchaseEvents = new HashSet<>();
    Match match = matchRepository.save(Match.builder().build());
    payload.lines()
        .filter(line -> matchers.stream().anyMatch(line::contains))
        .forEach(line -> {
          if (line.contains(CAST_SPELL)) {
            ingestSpellEvent(castedSpellEvents, match, line);
          } else if (line.contains(HIT)) {
            ingestHitEvent(damageEvents, match, line);
          } else if (line.contains(PURCHASE_ITEM)) {
            ingestItemEvent(itemPurchaseEvents, match, line);
          } else if (line.contains(HERO_KILL)) {
            ingestKillEvent(heroKillEvents, match, line);
          }
        });

    castedSpellRepository.saveAll(castedSpellEvents);
    damageEventRepository.saveAll(damageEvents);
    killEventRepository.saveAll(heroKillEvents);
    itemPurchaseRepository.saveAll(itemPurchaseEvents);

    return match.getId();
  }

  @Override
  public List<HeroKills> getHeroKills(Long matchId) {
    Optional<List<HeroKillEvent>> kills = killEventRepository.findByMatchId(matchId);
    if (kills.isPresent() && kills.get().size() > 0) {
      Map<String, Long> assailantWithKillCount = kills.get().stream()
          .map(HeroKillEvent::getAssailant)
          .collect(groupingBy(Hero::getName, counting()));
      return assailantWithKillCount.entrySet().stream()
          .map(e -> HeroKills.builder()
              .hero(e.getKey())
              .kills(e.getValue().intValue())
              .build())
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<HeroItems> getHeroItems(Long matchId, String heroName) {
    Optional<List<ItemPurchaseEvent>> items =
        itemPurchaseRepository.findByMatchIdAndHeroNameOrderByCreatedAsc(matchId, heroName);
    if (items.isPresent() && items.get().size() > 0) {
      return items.get().stream()
          .map(e -> HeroItems.builder()
              .item(e.getItem().getName())
              .timestamp((long) e.getCreated().toSecondOfDay())
              .build())
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<HeroSpells> getHeroSpells(Long matchId, String heroName) {
    Optional<List<CastedSpellEvent>> spells =
        castedSpellRepository.findByMatchIdAndHeroName(matchId, heroName);
    if (spells.isPresent() && spells.get().size() > 0) {
      Map<String, Long> spellsWithCount = spells.get().stream()
          .map(CastedSpellEvent::getSpell)
          .collect(groupingBy(Spell::getName, counting()));
      return spellsWithCount.entrySet().stream()
          .map(e -> HeroSpells.builder()
              .spell(e.getKey())
              .casts(e.getValue().intValue())
              .build())
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<HeroDamage> getHeroDamages(Long matchId, String heroName) {
    Optional<List<DamageEvent>> damages =
        damageEventRepository.findByMatchIdAndAssailantName(matchId, heroName);
    if (damages.isPresent() && damages.get().size() > 0) {
      Map<Hero, List<DamageEvent>> victimWithDamages = damages.get().stream()
          .collect(groupingBy(DamageEvent::getVictim));
      return victimWithDamages.entrySet().stream()
          .map(e -> HeroDamage.builder()
              .target(e.getKey().getName())
              .damageInstances(e.getValue().size())
              .totalDamage(e.getValue().stream().mapToInt(DamageEvent::getDamage).sum())
              .build())
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  private void ingestKillEvent(Set<HeroKillEvent> heroKillEvents, Match match, String line) {
    Hero assailant = payloadParser.parseLastHero(line);
    Hero victim = payloadParser.parseFirstHero(line);
    if (assailant == null || victim == null) {
      return;
    }
    assailant = persistHero(assailant);
    victim = persistHero(victim);
    HeroKillEvent event = HeroKillEvent.builder()
        .assailant(assailant)
        .victim(victim)
        .build();
    event.setCreated(payloadParser.parseTime(line));
    event.setMatch(match);
    heroKillEvents.add(event);
  }

  private void ingestItemEvent(Set<ItemPurchaseEvent> itemPurchaseEvents,
      Match match, String line) {
    Item item = payloadParser.parseItem(line);
    Hero hero = payloadParser.parseFirstHero(line);
    if (item == null || hero == null) {
      return;
    }
    hero = persistHero(hero);
    item = persistItem(item);
    ItemPurchaseEvent event = ItemPurchaseEvent.builder()
        .hero(hero)
        .item(item)
        .build();
    event.setCreated(payloadParser.parseTime(line));
    event.setMatch(match);
    itemPurchaseEvents.add(event);
  }

  private void ingestHitEvent(Set<DamageEvent> damageEvents, Match match, String line) {
    Hero assailant = payloadParser.parseFirstHero(line);
    Hero victim = payloadParser.parseLastHero(line);
    Integer damage = payloadParser.parseDamage(line);
    if (assailant == null || victim == null || damage == null) {
      return;
    }
    assailant = persistHero(assailant);
    victim = persistHero(victim);
    DamageEvent event = DamageEvent.builder()
        .assailant(assailant)
        .victim(victim)
        .damage(damage)
        .build();
    event.setCreated(payloadParser.parseTime(line));
    event.setMatch(match);
    damageEvents.add(event);
  }

  private void ingestSpellEvent(Set<CastedSpellEvent> castedSpellEvents,
      Match match, String line) {
    Hero firstHero = payloadParser.parseFirstHero(line);
    Spell spell = payloadParser.parseSpell(line);
    if (firstHero == null || spell == null) {
      return;
    }
    firstHero = persistHero(firstHero);
    spell = persistSpell(spell);
    CastedSpellEvent event = CastedSpellEvent.builder()
        .hero(firstHero)
        .spell(spell)
        .build();
    event.setCreated(payloadParser.parseTime(line));
    event.setMatch(match);
    castedSpellEvents.add(event);
  }

  private Hero persistHero(Hero hero) {
    Optional<Hero> dbHero = heroRepository.findByName(hero.getName());
    return dbHero.orElseGet(() -> heroRepository.save(hero));
  }

  private Item persistItem(Item item) {
    Optional<Item> dbItem = itemRepository.findByName(item.getName());
    return dbItem.orElseGet(() -> itemRepository.save(item));
  }

  private Spell persistSpell(Spell spell) {
    Optional<Spell> dbSpell = spellRepository.findByName(spell.getName());
    return dbSpell.orElseGet(() -> spellRepository.save(spell));
  }
}
