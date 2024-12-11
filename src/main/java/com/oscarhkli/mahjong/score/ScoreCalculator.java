package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ScoreCalculator {

  private final MeldsFactory meldsFactory;

  int[] constructMahjongTiles(List<MahjongTileType> tiles) {
    var mahjongTiles = new int[MahjongConstant.MAHJONG_TYPES];
    for (var tile : tiles) {
      mahjongTiles[tile.getIndex()]++;
    }
    return mahjongTiles;
  }

  public WinningHand calculate(List<MahjongTileType> tiles) {
    return new WinningHand(calculateWinningHands(tiles));
  }

  int calculateScore(List<WinningHandType> winningHandTypes) {
    if (winningHandTypes.contains(WinningHandType.TRICK_HAND)) {
      return WinningHandType.TRICK_HAND.getScore();
    }
    if (winningHandTypes.contains(WinningHandType.ALL_HONOR_TILES)) {
      return WinningHandType.ALL_HONOR_TILES.getScore();
    }
    var score = 0;
    for (var winningHandType : winningHandTypes) {
      score += winningHandType.getScore();
    }
    return score;
  }

  List<WinningHandType> calculateWinningHands(List<MahjongTileType> tiles) {
    var mahjongTiles = constructMahjongTiles(tiles);

    if (isThirteenOrphans(mahjongTiles)) {
      return List.of(WinningHandType.THIRTEEN_ORPHANS);
    }

    if (isNineGate(mahjongTiles)) {
      return List.of(WinningHandType.NINE_GATES);
    }

    // Wind and Dragon can only have 1 candidate - pongs with/without eyes
    var windMelds = meldsFactory.construct(MahjongSetType.WIND, mahjongTiles).getFirst();
    var dragonMelds = meldsFactory.construct(MahjongSetType.DRAGON, mahjongTiles).getFirst();
    var characterMeldsCandidates = meldsFactory.construct(MahjongSetType.CHARACTER, mahjongTiles);
    var bambooMeldsCandidates = meldsFactory.construct(MahjongSetType.BAMBOO, mahjongTiles);
    var dotMeldsCandidates = meldsFactory.construct(MahjongSetType.DOT, mahjongTiles);

    if (!isValidWinningHand(
        windMelds,
        dragonMelds,
        characterMeldsCandidates,
        bambooMeldsCandidates,
        dotMeldsCandidates)) {
      return List.of(WinningHandType.TRICK_HAND);
    }
    var winningHandTypes =
        deduceWinningHand(
            windMelds,
            dragonMelds,
            characterMeldsCandidates,
            bambooMeldsCandidates,
            dotMeldsCandidates);
    return winningHandTypes.isEmpty() ? List.of(WinningHandType.CHICKEN_HAND) : winningHandTypes;
  }

  private boolean isThirteenOrphans(int[] mahjongTiles) {
    var mahjongTileTypes =
        new MahjongTileType[] {
          MahjongTileType.EAST,
          MahjongTileType.SOUTH,
          MahjongTileType.WEST,
          MahjongTileType.NORTH,
          MahjongTileType.RED,
          MahjongTileType.GREEN,
          MahjongTileType.WHITE,
          MahjongTileType.C1,
          MahjongTileType.C9,
          MahjongTileType.B1,
          MahjongTileType.B9,
          MahjongTileType.D1,
          MahjongTileType.D9
        };
    var singleCount = 0;
    var eyeCount = 0;
    for (var mahjongTileType : mahjongTileTypes) {
      if (mahjongTiles[mahjongTileType.getIndex()] == 1) {
        singleCount++;
      } else if (mahjongTiles[mahjongTileType.getIndex()] == 2) {
        eyeCount++;
      }
    }
    return singleCount == 12 && eyeCount == 1;
  }

  private boolean isNineGate(int[] mahjongTiles) {
    return Stream.of(MahjongTileType.C1, MahjongTileType.B1, MahjongTileType.D1)
        .map(MahjongTileType::getIndex)
        .anyMatch(startIndex -> {
          var size = 0;
          for (var i = 0; i < 9; i++) {
            if (mahjongTiles[startIndex + i] == 0) {
              return false;
            }
            size += mahjongTiles[startIndex + i];
          }
          return mahjongTiles[startIndex] >= 3 && mahjongTiles[startIndex + 8] >= 3 && size == 14;
        });
  }

  private boolean isValidWinningHand(
      Melds windMelds,
      Melds dragonMelds,
      List<Melds> characterMeldsCandidates,
      List<Melds> bambooMeldsCandidates,
      List<Melds> dotMeldsCandidates) {
    if (windMelds.getUnusedTileCount() > 0) {
      return false;
    }
    if (dragonMelds.getUnusedTileCount() > 0) {
      return false;
    }
    // Check first candidate is already enough as all the remaining candidates must either contain
    // only those with eyes or contain 1 without eyes
    var firstCharacterMelds = characterMeldsCandidates.getFirst();
    if (firstCharacterMelds.getUnusedTileCount() > 0) {
      return false;
    }
    var firstBambooMelds = bambooMeldsCandidates.getFirst();
    if (firstBambooMelds.getUnusedTileCount() > 0) {
      return false;
    }
    var firstDotMelds = dotMeldsCandidates.getFirst();
    if (firstDotMelds.getUnusedTileCount() > 0) {
      return false;
    }

    return Stream.of(windMelds, dragonMelds, firstCharacterMelds, firstBambooMelds, firstDotMelds)
            .filter(Melds::hasEyes)
            .count()
        == 1;
  }

  /**
   * If Suit A has multiple candidates, each of Suit B and C must have only 1 candidate.<br>
   * If all the other Suit Melds contain Chows, the best choice for Suit A must be the one with
   * fewer Pongs, i.e., the last candidate.<br>
   * Otherwise, the Winning Hand will be either All in Triplets or Trick Hand.<br>
   * The best choice for Suit A would be the one with the one with only Pongs then with only Chows
   * then otherwise.<br>
   *
   * @param suitACandidates target suit to check
   * @param suitBCandidates the neighbor suit
   * @param suitCCandidates the neighbor suit
   * @return best Melds
   */
  private Melds selectBestMelds(
      List<Melds> suitACandidates, List<Melds> suitBCandidates, List<Melds> suitCCandidates) {
    if (suitACandidates.size() == 1) {
      return suitACandidates.getFirst();
    }
    if (!suitBCandidates.getFirst().getChows().isEmpty()
        || !suitCCandidates.getFirst().getChows().isEmpty()) {
      return suitACandidates.getLast();
    }
    if (suitACandidates.getFirst().getChows().isEmpty()) {
      return suitACandidates.getFirst();
    }
    if (suitACandidates.getLast().getPongs().isEmpty()) {
      return suitACandidates.getLast();
    }
    return suitACandidates.getFirst();
  }

  private List<WinningHandType> deduceWinningHand(
      Melds windMelds,
      Melds dragonMelds,
      List<Melds> characterMeldsCandidates,
      List<Melds> bambooMeldsCandidates,
      List<Melds> dotMeldsCandidates) {
    if (windMelds.getPongs().size() == 4) {
      return List.of(WinningHandType.GREAT_WINDS);
    }
    var honorPongSize = windMelds.getPongs().size() + dragonMelds.getPongs().size();
    var honorMelds = Set.of(windMelds, dragonMelds);
    if (honorPongSize == 4 && isEither(honorMelds, Melds::hasEyes)) {
      return List.of(WinningHandType.ALL_HONOR_TILES);
    }

    var characterMelds =
        selectBestMelds(characterMeldsCandidates, bambooMeldsCandidates, dotMeldsCandidates);
    var bambooMelds =
        selectBestMelds(bambooMeldsCandidates, characterMeldsCandidates, dotMeldsCandidates);
    var dotMelds =
        selectBestMelds(dotMeldsCandidates, characterMeldsCandidates, bambooMeldsCandidates);
    return deduceWinningHand(windMelds, dragonMelds, characterMelds, bambooMelds, dotMelds);
  }

  private List<WinningHandType> deduceWinningHand(
      Melds windMelds,
      Melds dragonMelds,
      Melds characterMelds,
      Melds bambooMelds,
      Melds dotMelds) {
    var honorPongSize = windMelds.getPongs().size() + dragonMelds.getPongs().size();
    var suitedMelds = Set.of(characterMelds, bambooMelds, dotMelds);

    var characterOrphans = characterMelds.getOrphanPongs();
    var bambooOrphans = bambooMelds.getOrphanPongs();
    var dotOrphans = dotMelds.getOrphanPongs();
    if (characterOrphans.size() + bambooOrphans.size() + dotOrphans.size() == 4
        && isEither(suitedMelds, Melds::hasOrphanEyes)) {
      return List.of(WinningHandType.ORPHANS);
    }

    var winningHandTypes = new ArrayList<WinningHandType>();

    if (characterMelds.getChows().size()
            + bambooMelds.getChows().size()
            + dotMelds.getChows().size()
        == 4) {
      winningHandTypes.add(WinningHandType.COMMON_HAND);
    }

    if (honorPongSize
            + characterMelds.getPongs().size()
            + bambooMelds.getPongs().size()
            + dotMelds.getPongs().size()
        == 4) {
      winningHandTypes.add(WinningHandType.ALL_IN_TRIPLETS);
      var honorMelds = Set.of(windMelds, dragonMelds);
      if (characterOrphans.size() + bambooOrphans.size() + dotOrphans.size() + honorPongSize == 4
          && (isEither(honorMelds, Melds::hasEyes)
              || isEither(suitedMelds, Melds::hasOrphanEyes))) {
        winningHandTypes.add(WinningHandType.MIXED_ORPHANS);
      }
    }

    if (windMelds.getPongs().size() == 3 && windMelds.hasEyes()) {
      winningHandTypes.add(WinningHandType.SMALL_WINDS);
    }

    if (dragonMelds.getPongs().size() == 3) {
      winningHandTypes.add(WinningHandType.GREAT_DRAGON);
    }
    if (dragonMelds.getPongs().size() == 2 && dragonMelds.hasEyes()) {
      winningHandTypes.add(WinningHandType.SMALL_DRAGON);
    }
    for (var i = 0; i < dragonMelds.getPongs().size(); i++) {
      winningHandTypes.add(WinningHandType.ONE_DRAGON);
    }

    if (isEither(suitedMelds, Melds::isAllOneSuit)) {
        winningHandTypes.add(WinningHandType.ALL_ONE_SUIT);
    } else if (isEither(suitedMelds, melds -> melds.isMixedOneSuit(windMelds, dragonMelds))) {
      winningHandTypes.add(WinningHandType.MIXED_ONE_SUIT);
    }
    return winningHandTypes;
  }

  private boolean isEither(Set<Melds> allMelds, Predicate<Melds> predicate) {
    return allMelds.stream().anyMatch(predicate);
  }
}
