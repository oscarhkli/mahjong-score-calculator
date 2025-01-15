package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ScoreCalculator {

  private final MeldsFactory meldsFactory;

  public WinningHand calculate(
      List<MahjongTileType> tiles,
      ExposedMelds exposedMelds,
      List<MahjongTileType> bonusTiles,
      WindType windSettings) {
    return new WinningHand(calculateWinningHands(tiles, exposedMelds, bonusTiles, windSettings));
  }

  int[] constructMahjongTiles(List<MahjongTileType> tiles) {
    var mahjongTiles = new int[MahjongConstant.MAHJONG_TYPES];
    tiles.forEach(tile -> mahjongTiles[tile.getIndex()]++);
    return mahjongTiles;
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

  List<WinningHandType> calculateWinningHands(
      List<MahjongTileType> tiles,
      ExposedMelds exposedMelds,
      List<MahjongTileType> bonusTiles,
      WindType windSettings) {
    var mahjongTiles = constructMahjongTiles(tiles);

    if (isAllKongs(mahjongTiles, exposedMelds.getKongs())) {
      return List.of(WinningHandType.ALL_KONGS);
    }

    if (isThirteenOrphans(mahjongTiles)) {
      return List.of(WinningHandType.THIRTEEN_ORPHANS);
    }

    if (isNineGate(mahjongTiles, exposedMelds)) {
      return List.of(WinningHandType.NINE_GATES);
    }

    // Wind and Dragon can only have 1 candidate - pongs with/without eyes
    var windMelds =
        meldsFactory.construct(MahjongSetType.WIND, mahjongTiles, exposedMelds).getFirst();
    var dragonMelds =
        meldsFactory.construct(MahjongSetType.DRAGON, mahjongTiles, exposedMelds).getFirst();
    var characterMeldsCandidates =
        meldsFactory.construct(MahjongSetType.CHARACTER, mahjongTiles, exposedMelds);
    var bambooMeldsCandidates =
        meldsFactory.construct(MahjongSetType.BAMBOO, mahjongTiles, exposedMelds);
    var dotMeldsCandidates = meldsFactory.construct(MahjongSetType.DOT, mahjongTiles, exposedMelds);

    var bonusWinningConditions =
        calculateBonusWinningConditions(
            windMelds, dragonMelds, bonusTiles, windSettings, exposedMelds);

    if (!isValidWinningHand(
            windMelds,
            dragonMelds,
            characterMeldsCandidates,
            bambooMeldsCandidates,
            dotMeldsCandidates)
        && !bonusWinningConditions.contains(WinningHandType.FLOWER_HANDS)
        && !bonusWinningConditions.contains(WinningHandType.GREAT_FLOWERS)) {
      return List.of(WinningHandType.TRICK_HAND);
    }
    var winningHandTypes =
        deduceWinningHand(
            windMelds,
            dragonMelds,
            characterMeldsCandidates,
            bambooMeldsCandidates,
            dotMeldsCandidates);
    var results = new ArrayList<>(winningHandTypes);
    results.addAll(bonusWinningConditions);
    if (results.isEmpty()) {
      results.add(WinningHandType.CHICKEN_HAND);
    }
    if (results.size() == 2
        && results.contains(WinningHandType.WIN_FROM_WALL)
        && (results.contains(WinningHandType.GREAT_FLOWERS)
            || results.contains(WinningHandType.FLOWER_HANDS))) {
      results.remove(WinningHandType.WIN_FROM_WALL);
    }
    return results;
  }

  List<WinningHandType> calculateBonusWinningConditions(
      Melds windMelds,
      Melds dragonMelds,
      List<MahjongTileType> bonusTiles,
      WindType windSettings,
      ExposedMelds exposedMelds) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    if (exposedMelds.isEmpty()) {
      bonusWinningHands.add(WinningHandType.WIN_FROM_WALL);
    }
    bonusWinningHands.addAll(calculateWindTilesWinningConditions(windMelds, windSettings));
    bonusWinningHands.addAll(calculateDragonTilesWinningConditions(dragonMelds));
    bonusWinningHands.addAll(calculateBonusTilesWinningConditions(bonusTiles, windSettings));
    return bonusWinningHands;
  }

  private List<WinningHandType> calculateWindTilesWinningConditions(
      Melds windMelds, WindType windSettings) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    if (windMelds.getPongs().contains(windSettings.prevailing())
        || windMelds.getKongs().contains(windSettings.prevailing())) {
      bonusWinningHands.add(WinningHandType.PREVAILING_WIND);
    }
    if (windMelds.getPongs().contains(windSettings.seat())
        || windMelds.getKongs().contains(windSettings.seat())) {
      bonusWinningHands.add(WinningHandType.SEAT_WIND);
    }
    return bonusWinningHands;
  }

  private List<WinningHandType> calculateDragonTilesWinningConditions(Melds dragonMelds) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    for (var i = 0; i < dragonMelds.getPongKongSize(); i++) {
      bonusWinningHands.add(WinningHandType.ONE_DRAGON);
    }
    return bonusWinningHands;
  }

  private List<WinningHandType> calculateBonusTilesWinningConditions(
      List<MahjongTileType> bonusTiles, WindType windSettings) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    var bonusTileSet = new HashSet<>(bonusTiles);
    var hasAllFlowers =
        Stream.of(MahjongTileType.F1, MahjongTileType.F2, MahjongTileType.F3, MahjongTileType.F4)
            .allMatch(bonusTileSet::contains);
    var hasAllSeasons =
        Stream.of(MahjongTileType.S1, MahjongTileType.S2, MahjongTileType.S3, MahjongTileType.S4)
            .allMatch(bonusTileSet::contains);
    if (bonusTiles.size() == 8) {
      bonusWinningHands.add(WinningHandType.GREAT_FLOWERS);
    } else if (bonusTiles.size() == 7 && (hasAllFlowers || hasAllSeasons)) {
      bonusWinningHands.add(WinningHandType.FLOWER_HANDS);
    } else if (bonusTiles.isEmpty()) {
      bonusWinningHands.add(WinningHandType.NO_FLOWERS);
    } else {
      var windIndex =
          windSettings.seat().getIndex() - MahjongSetType.WIND.getStartingTile().getIndex();
      if (hasAllFlowers) {
        bonusWinningHands.add(WinningHandType.ALL_FLOWERS);
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.SEASON)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
      } else if (hasAllSeasons) {
        bonusWinningHands.add(WinningHandType.ALL_FLOWERS);
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.FLOWER)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
      } else {
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.FLOWER)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.SEASON)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
      }
    }
    return bonusWinningHands;
  }

  private boolean isFlowerOfOwnWind(
      List<MahjongTileType> bonusTiles, int windIndex, MahjongSetType mahjongSetType) {
    return bonusTiles.stream()
        .map(MahjongTileType::getIndex)
        .map(bonusTileIndex -> bonusTileIndex - mahjongSetType.getStartingTile().getIndex())
        .anyMatch(bonusWindIndex -> bonusWindIndex == windIndex);
  }

  private boolean isThirteenOrphans(int[] mahjongTiles) {
    var requiredMahjongTileTypes =
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
    for (var mahjongTileType : requiredMahjongTileTypes) {
      var count = mahjongTiles[mahjongTileType.getIndex()];
      if (count == 1) {
        singleCount++;
      } else if (count == 2) {
        eyeCount++;
      }
    }
    return singleCount == 12 && eyeCount == 1;
  }

  private boolean isAllKongs(int[] mahjongTiles, List<MahjongTileType> exposedKongs) {
    return exposedKongs.size() == 4 && IntStream.of(mahjongTiles).anyMatch(m -> m == 2);
  }

  private boolean isNineGate(int[] mahjongTiles, ExposedMelds exposedMelds) {
    if (!exposedMelds.isEmpty()) {
      return false;
    }
    return Stream.of(MahjongTileType.C1, MahjongTileType.B1, MahjongTileType.D1)
        .map(MahjongTileType::getIndex)
        .anyMatch(
            startIndex -> {
              var totalTiles = 0;
              for (var i = 0; i < 9; i++) {
                if (mahjongTiles[startIndex + i] == 0) {
                  return false;
                }
                totalTiles += mahjongTiles[startIndex + i];
              }
              return mahjongTiles[startIndex] >= 3
                  && mahjongTiles[startIndex + 8] >= 3
                  && totalTiles == 14;
            });
  }

  private boolean isValidWinningHand(
      Melds windMelds,
      Melds dragonMelds,
      List<Melds> characterMeldsCandidates,
      List<Melds> bambooMeldsCandidates,
      List<Melds> dotMeldsCandidates) {
    return Stream.of(
                windMelds,
                dragonMelds,
                characterMeldsCandidates.getFirst(),
                bambooMeldsCandidates.getFirst(),
                dotMeldsCandidates.getFirst())
            .allMatch(meld -> meld.getUnusedTileCount() == 0)
        && Stream.of(
                    windMelds,
                    dragonMelds,
                    characterMeldsCandidates.getFirst(),
                    bambooMeldsCandidates.getFirst(),
                    dotMeldsCandidates.getFirst())
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
    if (suitACandidates.getLast().getPongKongSize() == 0) {
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
    if (windMelds.getPongKongSize() == 4) {
      return List.of(WinningHandType.GREAT_WINDS);
    }
    var honorPongSize = windMelds.getPongKongSize() + dragonMelds.getPongKongSize();
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
      Melds windMelds, Melds dragonMelds, Melds characterMelds, Melds bambooMelds, Melds dotMelds) {
    var winningHandTypes = new ArrayList<WinningHandType>();

    if (characterMelds.getChows().size()
            + bambooMelds.getChows().size()
            + dotMelds.getChows().size()
        == 4) {
      winningHandTypes.add(WinningHandType.COMMON_HAND);
    }

    var suitedMelds = Set.of(characterMelds, bambooMelds, dotMelds);
    var suitedOrphanKongPongSize =
        characterMelds.getOrphanPongKongs().size()
            + bambooMelds.getOrphanPongKongs().size()
            + dotMelds.getOrphanPongKongs().size();
    if (suitedOrphanKongPongSize == 4 && isEither(suitedMelds, Melds::hasOrphanEyes)) {
      return List.of(WinningHandType.ORPHANS);
    }

    var honorPongKongSize = windMelds.getPongKongSize() + dragonMelds.getPongKongSize();
    if (honorPongKongSize
            + characterMelds.getPongKongSize()
            + bambooMelds.getPongKongSize()
            + dotMelds.getPongKongSize()
        == 4) {
      winningHandTypes.add(WinningHandType.ALL_IN_TRIPLETS);
      if (isMixedOrphans(
          Set.of(windMelds, dragonMelds),
          suitedMelds,
          suitedOrphanKongPongSize + honorPongKongSize)) {
        winningHandTypes.add(WinningHandType.MIXED_ORPHANS);
      }
    }

    if (windMelds.getPongKongSize() == 3 && windMelds.hasEyes()) {
      winningHandTypes.add(WinningHandType.SMALL_WINDS);
    }

    if (dragonMelds.getPongKongSize() == 3) {
      winningHandTypes.add(WinningHandType.GREAT_DRAGON);
    }
    if (dragonMelds.getPongKongSize() == 2 && dragonMelds.hasEyes()) {
      winningHandTypes.add(WinningHandType.SMALL_DRAGON);
    }

    if (isEither(suitedMelds, Melds::isAllOneSuit)) {
      winningHandTypes.add(WinningHandType.ALL_ONE_SUIT);
    } else if (isEither(suitedMelds, melds -> melds.isMixedOneSuit(windMelds, dragonMelds))) {
      winningHandTypes.add(WinningHandType.MIXED_ONE_SUIT);
    }
    return winningHandTypes;
  }

  private boolean isMixedOrphans(Set<Melds> honorMelds, Set<Melds> suitedMelds, int kongPongSize) {
    return kongPongSize == 4
        && (isEither(honorMelds, Melds::hasEyes) || isEither(suitedMelds, Melds::hasOrphanEyes));
  }

  private boolean isEither(Set<Melds> allMelds, Predicate<Melds> predicate) {
    return allMelds.stream().anyMatch(predicate);
  }
}
