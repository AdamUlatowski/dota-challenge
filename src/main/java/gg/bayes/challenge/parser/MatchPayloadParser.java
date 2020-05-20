package gg.bayes.challenge.parser;

import gg.bayes.challenge.database.model.Hero;
import gg.bayes.challenge.database.model.Item;
import gg.bayes.challenge.database.model.Spell;
import java.time.LocalTime;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

@Component
public class MatchPayloadParser {

  public Hero parseFirstHero(String line) {
    return Arrays.stream(line.split("\\s+"))
        .filter(s -> s.contains("npc_dota_hero_"))
        .findFirst()
        .map(h -> Hero.builder().name(StringUtils.removeStart(h,"npc_dota_hero_")).build())
        .orElse(null);
  }

  public Hero parseSecondHero(String line) {
    return Arrays.stream(line.split("\\s+"))
        .filter(s -> s.contains("npc_dota_hero_"))
        .reduce((first, second) -> second)
        .map(h -> Hero.builder().name(StringUtils.removeStart(h,"npc_dota_hero_")).build())
        .orElse(null);
  }

  public Item parseItem(String line) {
    return Arrays.stream(line.split("\\s+"))
        .filter(s -> s.contains("item_"))
        .findFirst()
        .map(h -> Item.builder().name(StringUtils.removeStart(h,"item_")).build())
        .orElse(null);
  }

  public Spell parseSpell(String line) {
    return Spell.builder().name(
        StringUtils.substringBetween(line, "casts ability", "(lvl").trim()).build();
  }

  public LocalTime parseTime(String line) {
    String time = StringUtils.substringBetween(line, "[", "]").trim();
    return LocalTime.parse(time);
  }

  public Integer parseDamage(String line) {
    String temp = StringUtils.substringBetween(line, "for", "damage").trim();
    if (NumberUtils.isDigits(temp)) {
      return Integer.parseInt(temp);
    }
    return null;
  }
}
