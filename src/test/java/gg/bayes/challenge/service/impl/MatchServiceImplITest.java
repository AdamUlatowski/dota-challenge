package gg.bayes.challenge.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.io.ByteStreams;
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
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MatchServiceImplITest {

  @Autowired
  private MatchPayloadParser payloadParser;
  @Autowired
  private MatchRepository matchRepository;
  @Autowired
  private HeroRepository heroRepository;
  @Autowired
  private ItemRepository itemRepository;
  @Autowired
  private SpellRepository spellRepository;
  @Autowired
  private DamageEventRepository damageEventRepository;
  @Autowired
  private CastedSpellRepository castedSpellRepository;
  @Autowired
  private KillEventRepository killEventRepository;
  @Autowired
  private ItemPurchaseRepository itemPurchaseRepository;
  @Autowired
  private TestRestTemplate testRestTemplate;

  @BeforeEach
  void setUp() {
    clearDB();
  }

  private void clearDB() {
    damageEventRepository.deleteAll();
    castedSpellRepository.deleteAll();
    killEventRepository.deleteAll();
    itemPurchaseRepository.deleteAll();
    itemRepository.deleteAll();
    spellRepository.deleteAll();
    heroRepository.deleteAll();
    matchRepository.deleteAll();
  }

  @Test
  void testIngestMatch() throws IOException {
    // arrange
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    byte [] matchFile = ByteStreams.toByteArray(getClass().getResourceAsStream(
        "/combatlog_short.log.txt"));
    HttpEntity<byte[]> fileEntity = new HttpEntity<>(matchFile, headers);

    // act
    var responseEntity = testRestTemplate.postForEntity(
        "/api/match", fileEntity, Long.class);
    var responseEntity2 = testRestTemplate.postForEntity(
        "/api/match", fileEntity, Long.class);

    // assert
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isEqualTo(1L);
    assertThat(responseEntity2).isNotNull();
    assertThat(responseEntity2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity2.getBody()).isEqualTo(2L);
    assertThat(heroRepository.findAll()).hasSize(10);
    assertThat(itemRepository.findAll()).hasSize(20);
    assertThat(spellRepository.findAll()).hasSize(13);
  }

  @Nested
  class TestWithLoadedFile {

    private Long setupDB() throws IOException {
      clearDB();
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.TEXT_PLAIN);
      byte [] matchFile = ByteStreams.toByteArray(getClass().getResourceAsStream(
          "/combatlog_short.log.txt"));
      HttpEntity<byte[]> fileEntity = new HttpEntity<>(matchFile, headers);
      return testRestTemplate.postForEntity(
          "/api/match", fileEntity, Long.class).getBody();
    }

    @Test
    void testGetItems() throws IOException {
      // arrange
      Long matchId = setupDB();

      // act
      var responseEntity = testRestTemplate.exchange(
          MessageFormat.format("/api/match/{0}/abyssal_underlord/items", matchId),
          HttpMethod.GET, null, new ParameterizedTypeReference<List<HeroItems>>() {});

      assertThat(responseEntity).isNotNull();
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(responseEntity.getBody()).hasSize(1);
      assertThat(responseEntity.getBody().get(0).getItem()).isEqualTo("magic_stick");
    }

    @Test
    void testGetKills() throws IOException {
      // arrange
      Long matchId = setupDB();

      // act
      var responseEntity = testRestTemplate.exchange(
          MessageFormat.format("/api/match/{0}", matchId),
          HttpMethod.GET, null, new ParameterizedTypeReference<List<HeroKills>>() {});

      assertThat(responseEntity).isNotNull();
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(responseEntity.getBody()).hasSize(2);
      assertThat(responseEntity.getBody()).containsAnyOf(HeroKills.builder()
          .hero("mars").kills(1).build());
    }

    @Test
    void testGetSpells() throws IOException {
      // arrange
      Long matchId = setupDB();

      // act
      var responseEntity = testRestTemplate.exchange(
          MessageFormat.format("/api/match/{0}/abyssal_underlord/spells", matchId),
          HttpMethod.GET, null, new ParameterizedTypeReference<List<HeroSpells>>() {});

      assertThat(responseEntity).isNotNull();
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(responseEntity.getBody()).hasSize(1);
      assertThat(responseEntity.getBody()).containsExactly(HeroSpells.builder()
          .spell("abyssal_underlord_firestorm").casts(4).build());
    }

    @Test
    void testGetDamage() throws IOException {
      // arrange
      Long matchId = setupDB();

      // act
      var responseEntity = testRestTemplate.exchange(
          MessageFormat.format("/api/match/{0}/abyssal_underlord/damage", matchId),
          HttpMethod.GET, null, new ParameterizedTypeReference<List<HeroDamage>>() {});

      assertThat(responseEntity).isNotNull();
      assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(responseEntity.getBody()).hasSize(2);
      assertThat(responseEntity.getBody()).containsAnyOf(HeroDamage.builder()
          .target("bane").damageInstances(4).totalDamage(33).build());
    }
  }

}