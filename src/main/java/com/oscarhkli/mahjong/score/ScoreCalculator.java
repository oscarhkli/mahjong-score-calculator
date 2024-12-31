package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
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
      List<MahjongTileType> exposedChows,
      List<MahjongTileType> exposedPongs,
      List<MahjongTileType> exposedKongs) {
    return new WinningHand(calculateWinningHands(tiles, exposedChows, exposedPongs, exposedKongs));
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
      List<MahjongTileType> exposedChows,
      List<MahjongTileType> exposedPongs,
      List<MahjongTileType> exposedKongs) {
    var mahjongTiles = constructMahjongTiles(tiles);

    if (isAllKongs(mahjongTiles, exposedKongs)) {
      return List.of(WinningHandType.ALL_KONGS);
    }

    if (isThirteenOrphans(mahjongTiles)) {
      return List.of(WinningHandType.THIRTEEN_ORPHANS);
    }

    if (isNineGate(mahjongTiles, exposedChows, exposedPongs, exposedKongs)) {
      return List.of(WinningHandType.NINE_GATES);
    }

    // Wind and Dragon can only have 1 candidate - pongs with/without eyes
    var windMelds =
        meldsFactory
            .construct(MahjongSetType.WIND, mahjongTiles, exposedChows, exposedPongs, exposedKongs)
            .getFirst();
    var dragonMelds =
        meldsFactory
            .construct(
                MahjongSetType.DRAGON, mahjongTiles, exposedChows, exposedPongs, exposedKongs)
            .getFirst();
    var characterMeldsCandidates =
        meldsFactory.construct(
            MahjongSetType.CHARACTER, mahjongTiles, exposedChows, exposedPongs, exposedKongs);
    var bambooMeldsCandidates =
        meldsFactory.construct(
            MahjongSetType.BAMBOO, mahjongTiles, exposedChows, exposedPongs, exposedKongs);
    var dotMeldsCandidates =
        meldsFactory.construct(
            MahjongSetType.DOT, mahjongTiles, exposedChows, exposedPongs, exposedKongs);

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

  private boolean isNineGate(
      int[] mahjongTiles,
      List<MahjongTileType> exposedChows,
      List<MahjongTileType> exposedPongs,
      List<MahjongTileType> exposedKongs) {
    if (exposedChows.size() + exposedPongs.size() + exposedKongs.size() > 0) {
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
    var suitedOrphanPongSize =
        characterMelds.getOrphanPongs().size()
            + bambooMelds.getOrphanPongs().size()
            + dotMelds.getOrphanPongs().size();
    if (suitedOrphanPongSize == 4 && isEither(suitedMelds, Melds::hasOrphanEyes)) {
      return List.of(WinningHandType.ORPHANS);
    }

    var honorPongKongSize = windMelds.getPongKongSize() + dragonMelds.getPongKongSize();
    if (honorPongKongSize
            + characterMelds.getPongKongSize()
            + bambooMelds.getPongKongSize()
            + dotMelds.getPongKongSize()
        == 4) {
      winningHandTypes.add(WinningHandType.ALL_IN_TRIPLETS);
      if (suitedOrphanPongSize + honorPongKongSize == 4
          && (isEither(Set.of(windMelds, dragonMelds), Melds::hasEyes)
              || isEither(suitedMelds, Melds::hasOrphanEyes))) {
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
    for (var i = 0; i < dragonMelds.getPongKongSize(); i++) {
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
