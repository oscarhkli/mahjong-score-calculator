package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScoreCalculator {

  int[] constructMahjongTiles(List<String> tileStrings) {
    var mahjongTiles = new int[MahjongConstant.MAHJONG_TYPES];
    for (var tile : tileStrings) {
      mahjongTiles[MahjongTileType.valueOf(tile).getIndex()]++;
    }
    return mahjongTiles;
  }

  public int calculate(List<String> tiles) {
    return calculateScore(calculateWinningHands(tiles));
  }

  int calculateScore(Set<WinningHandType> winningHandTypes) {
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

  Set<WinningHandType> calculateWinningHands(List<String> tiles) {
    var mahjongTiles = constructMahjongTiles(tiles);

    if (isThirteenOrphans(mahjongTiles)) {
      return Set.of(WinningHandType.THIRTEEN_ORPHANS);
    }

    // Wind and Dragon can only have 1 candidate - pongs with/without eyes
    var windMelds = construct(MahjongSetType.WIND, mahjongTiles).getFirst();
    var dragonMelds = construct(MahjongSetType.DRAGON, mahjongTiles).getFirst();
    var characterMeldsCandidates = construct(MahjongSetType.CHARACTER, mahjongTiles);
    var bambooMeldsCandidates = construct(MahjongSetType.BAMBOO, mahjongTiles);
    var dotMeldsCandidates = construct(MahjongSetType.DOT, mahjongTiles);

    if (!hasEyes(
        windMelds,
        dragonMelds,
        characterMeldsCandidates,
        bambooMeldsCandidates,
        dotMeldsCandidates)) {
      return Set.of(WinningHandType.TRICK_HAND);
    }
    return deduceWinningHand(
        windMelds,
        dragonMelds,
        characterMeldsCandidates,
        bambooMeldsCandidates,
        dotMeldsCandidates);
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

  private boolean hasEyes(
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

    var eyes = new HashSet<MahjongTileType>();
    eyes.add(windMelds.getEye());
    eyes.add(dragonMelds.getEye());
    eyes.add(firstCharacterMelds.getEye());
    eyes.add(firstBambooMelds.getEye());
    eyes.add(firstDotMelds.getEye());
    return eyes.stream().filter(Objects::nonNull).count() == 1;
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

  private Set<WinningHandType> deduceWinningHand(
      Melds windMelds,
      Melds dragonMelds,
      List<Melds> characterMeldsCandidates,
      List<Melds> bambooMeldsCandidates,
      List<Melds> dotMeldsCandidates) {
    var characterMelds =
        selectBestMelds(characterMeldsCandidates, bambooMeldsCandidates, dotMeldsCandidates);
    var bambooMelds =
        selectBestMelds(bambooMeldsCandidates, characterMeldsCandidates, dotMeldsCandidates);
    var dotMelds =
        selectBestMelds(dotMeldsCandidates, characterMeldsCandidates, bambooMeldsCandidates);

    log.info("windMelds: {}", windMelds);
    log.info("dragonMelds: {}", dragonMelds);
    log.debug("characterMelds: {}", characterMelds);
    log.debug("bambooMelds: {}", bambooMelds);
    log.debug("dotMelds: {}", dotMelds);

    var winningHandTypes = new HashSet<WinningHandType>();
    if (characterMelds.getChows().size()
            + bambooMelds.getChows().size()
            + dotMelds.getChows().size()
        == 4) {
      winningHandTypes.add(WinningHandType.COMMON_HAND);
    }
    if (windMelds.getPongs().size()
            + dragonMelds.getPongs().size()
            + characterMelds.getPongs().size()
            + bambooMelds.getPongs().size()
            + dotMelds.getPongs().size()
        == 4) {
      winningHandTypes.add(WinningHandType.ALL_IN_TRIPLETS);
    }

    if (windMelds.getPongs().size() == 4) {
      winningHandTypes.add(WinningHandType.GREAT_WINDS);
    } else if (windMelds.getPongs().size() == 3) {
      winningHandTypes.add(WinningHandType.SMALL_WINDS);
    }

    if (dragonMelds.getPongs().size() == 3) {
      winningHandTypes.add(WinningHandType.GREAT_DRAGON);
    } else if (dragonMelds.getPongs().size() == 2 && dragonMelds.getEye() != null) {
      winningHandTypes.add(WinningHandType.SMALL_DRAGON);
    }

    var honorPongSize = windMelds.getPongs().size() + dragonMelds.getPongs().size();
    var characterOrphans = getOrphans(characterMelds);
    var bambooOrphans = getOrphans(bambooMelds);
    var dotOrphans = getOrphans(dotMelds);
    if (characterOrphans.size() + bambooOrphans.size() + dotOrphans.size() + honorPongSize == 4
        && (windMelds.getEye() != null
            || dragonMelds.getEye() != null
            || (MahjongTileType.C1.equals(characterMelds.getEye())
                || MahjongTileType.C9.equals(characterMelds.getEye()))
            || (MahjongTileType.B1.equals(bambooMelds.getEye())
                || MahjongTileType.B9.equals(bambooMelds.getEye()))
            || (MahjongTileType.D1.equals(dotMelds.getEye())
                || MahjongTileType.D9.equals(dotMelds.getEye())))) {
      winningHandTypes.add(WinningHandType.MIXED_ORPHANS);
    }
    if (characterOrphans.size() + bambooOrphans.size() + dotOrphans.size() == 4
        && ((MahjongTileType.C1.equals(characterMelds.getEye())
                || MahjongTileType.C9.equals(characterMelds.getEye()))
            || (MahjongTileType.B1.equals(bambooMelds.getEye())
                || MahjongTileType.B9.equals(bambooMelds.getEye()))
            || (MahjongTileType.D1.equals(dotMelds.getEye())
                || MahjongTileType.D9.equals(dotMelds.getEye())))) {
      winningHandTypes.add(WinningHandType.ORPHANS);
    }

    if (honorPongSize == 4 && (windMelds.getEye() != null || dragonMelds.getEye() != null)) {
      winningHandTypes.add(WinningHandType.ALL_HONOR_TILES);
    } else if ((characterMelds.getChows().size() + characterMelds.getPongs().size() == 4
            && characterMelds.getEye() != null
            && characterMelds.getEye().isMahjongSetTypeEqualTo(MahjongSetType.CHARACTER))
        || (bambooMelds.getChows().size() + bambooMelds.getPongs().size() == 4
            && bambooMelds.getEye() != null
            && bambooMelds.getEye().isMahjongSetTypeEqualTo(MahjongSetType.BAMBOO))
        || (dotMelds.getChows().size() + dotMelds.getPongs().size() == 4
            && dotMelds.getEye() != null
            && dotMelds.getEye().isMahjongSetTypeEqualTo(MahjongSetType.DOT))) {
      if (isNineGates(characterMelds) || isNineGates(bambooMelds) || isNineGates(dotMelds)) {
        winningHandTypes.add(WinningHandType.NINE_GATES);
      } else {
        winningHandTypes.add(WinningHandType.ALL_ONE_SUIT);
      }
    } else if ((characterMelds.getChows().size() + characterMelds.getPongs().size() + honorPongSize
                == 4
            && (characterMelds.getEye() != null
                || windMelds.getEye() != null
                || dragonMelds.getEye() != null))
        || (bambooMelds.getChows().size() + bambooMelds.getPongs().size() + honorPongSize == 4
            && (bambooMelds.getEye() != null
                || windMelds.getEye() != null
                || dragonMelds.getEye() != null))
        || (dotMelds.getChows().size() + dotMelds.getPongs().size() + honorPongSize == 4
            && (dotMelds.getEye() != null
                || windMelds.getEye() != null
                || dragonMelds.getEye() != null))) {
      winningHandTypes.add(WinningHandType.MIXED_ONE_SUIT);
    }

    if (winningHandTypes.contains(WinningHandType.GREAT_WINDS)) {
      winningHandTypes.clear();
      winningHandTypes.add(WinningHandType.GREAT_WINDS);
    }

    if (winningHandTypes.contains(WinningHandType.ALL_HONOR_TILES)) {
      winningHandTypes.clear();
      winningHandTypes.add(WinningHandType.ALL_HONOR_TILES);
    }

    if (winningHandTypes.contains(WinningHandType.ORPHANS)) {
      winningHandTypes.clear();
      winningHandTypes.add(WinningHandType.ORPHANS);
    }

    if (winningHandTypes.isEmpty()) {
      winningHandTypes.add(WinningHandType.CHICKEN_HAND);
    }
    return winningHandTypes;
  }

  private Set<MahjongTileType> getOrphans(Melds melds) {
    var startingTile = melds.getMahjongSetType().getStartingTile();
    var endingTile =
        MahjongTileType.valueOfIndex(melds.getMahjongSetType().getStartingTile().getIndex() + 8);
    return melds.getPongs().stream()
        .filter(
            mahjongTileType ->
                mahjongTileType.equals(startingTile) || mahjongTileType.equals(endingTile))
        .collect(Collectors.toSet());
  }

  private boolean isNineGates(Melds melds) {
    var seen = new HashSet<MahjongTileType>();
    melds.getChows().forEach(seen::addAll);
    seen.addAll(melds.getPongs());
    if (melds.getEye() != null) {
      seen.add(melds.getEye());
    }
    return seen.size() == 9;
  }

  @Value
  public static class Melds {

    MahjongSetType mahjongSetType;
    List<List<MahjongTileType>> chows;
    List<MahjongTileType> pongs;
    List<MahjongTileType> kongs;
    MahjongTileType eye;
    int[] unusedTiles;
    int unusedTileCount;
    int unusedPairs;
  }

  public List<Melds> construct(MahjongSetType mahjongSetType, int[] tiles) {
    var allTiles = 0;
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    var mahjongSetSize = mahjongSetType.getSize();
    for (var i = 0; i < mahjongSetSize; i++) {
      allTiles += tiles[startingTileIndex + i];
    }
    if (allTiles == 0) {
      return List.of(
          new Melds(
              mahjongSetType,
              List.of(),
              List.of(),
              List.of(),
              null,
              new int[mahjongSetSize + 1],
              0,
              0));
    }

    var targetTiles = new int[mahjongSetSize + 1];
    System.arraycopy(tiles, startingTileIndex, targetTiles, 1, mahjongSetSize);
    var meldsCandidates = new HashSet<Melds>();
    meldsCandidates.add(
        constructMelds(
            mahjongSetType, Arrays.copyOf(targetTiles, mahjongSetSize + 1), List.of(), false));
    meldsCandidates.add(
        constructMelds(
            mahjongSetType, Arrays.copyOf(targetTiles, mahjongSetSize + 1), List.of(), true));
    for (var i = 1; i <= mahjongSetSize; i++) {
      if (targetTiles[i] >= 2) {
        var adjustedTileCounts = Arrays.copyOf(targetTiles, mahjongSetSize + 1);
        adjustedTileCounts[i] -= 2;
        meldsCandidates.add(
            constructMelds(mahjongSetType, adjustedTileCounts, List.of(i, i), true));
      }
    }
    return deduceBestMelds(meldsCandidates);
  }

  private List<Melds> deduceBestMelds(Set<Melds> meldsCandidates) {
    var results = new ArrayList<>(meldsCandidates);
    if (meldsCandidates.size() == 1) {
      return results;
    }
    log.info("meldsCandidates: {}", meldsCandidates);
    results.sort(
        Comparator.comparingInt(Melds::getUnusedTileCount)
            .thenComparingInt(Melds::getUnusedPairs)
            .thenComparing(Comparator.<Melds>comparingInt(m -> m.getPongs().size()).reversed())
            .thenComparing(Comparator.<Melds>comparingInt(m -> m.getChows().size()).reversed())
            .thenComparing(Melds::getEye));
    if (results.getFirst().getUnusedTileCount() > 0) {
      // All combination will be Trick Hand anyway. Simply return the first one
      return List.of(results.getFirst());
    }
    results.removeIf(m -> m.getUnusedTileCount() > 0 || m.getUnusedPairs() > 0);
    return results;
  }

  private Melds constructMelds(
      MahjongSetType mahjongSetType,
      int[] tileCounts,
      List<Integer> reservedTiles,
      boolean checkChowFirst) {
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    List<List<MahjongTileType>> chows;
    List<MahjongTileType> pongs;
    if (checkChowFirst) {
      chows = deduceChows(mahjongSetType, tileCounts, startingTileIndex);
      pongs = deducePongs(tileCounts, reservedTiles, startingTileIndex);
    } else {
      pongs = deducePongs(tileCounts, reservedTiles, startingTileIndex);
      chows = deduceChows(mahjongSetType, tileCounts, startingTileIndex);
    }

    MahjongTileType eye = null;
    for (var i = 1; i < tileCounts.length; i++) {
      if (tileCounts[i] == 2) {
        eye = MahjongTileType.valueOfIndex(startingTileIndex - 1 + i);
        tileCounts[i] = 0;
        break;
      }
    }
    var unusedTileCount = 0;
    var unusedPairs = 0;
    for (var unusedTile : tileCounts) {
      unusedTileCount += unusedTile;
      if (unusedTile >= 2) {
        unusedPairs++;
      }
    }
    return new Melds(
        mahjongSetType, chows, pongs, List.of(), eye, tileCounts, unusedTileCount, unusedPairs);
  }

  /**
   * tileCounts may be altered when counting chows
   *
   * @param mahjongSetType Mahjong Set Type, e.g., SUITED, HONOR
   * @param tileCounts Counter of each tile of the current Mahjong Set Type
   * @param startingTileIndex Start index of current Mahjong Set Type
   * @return List of deduced Chows
   */
  private List<List<MahjongTileType>> deduceChows(
      MahjongSetType mahjongSetType, int[] tileCounts, int startingTileIndex) {
    if (!MahjongConstant.SUITED.equals(mahjongSetType.getFamily())) {
      return List.of();
    }
    var validChowStarts = new ArrayList<Integer>();
    for (var i = 1; i < tileCounts.length - 2; i++) {
      while (tileCounts[i] > 0) {
        if (tileCounts[i + 1] == 0 || tileCounts[i + 2] == 0) {
          break;
        }
        validChowStarts.add(i);
        tileCounts[i]--;
        tileCounts[i + 1]--;
        tileCounts[i + 2]--;
      }
    }

    return validChowStarts.stream()
        .map(validChowStart -> validChowStart + startingTileIndex - 1)
        .map(
            index ->
                Stream.of(index, index + 1, index + 2).map(MahjongTileType::valueOfIndex).toList())
        .toList();
  }

  /**
   * tileCounts will be altered when manipulating reserveTiles and counting pongs
   *
   * @param tileCounts Counter of each tile of the current Mahjong Set Type
   * @param reservedTiles For deducing potential eyes while deducing chows
   * @param startingTileIndex Start index of current Mahjong Set Type
   * @return List of deduced Pongs
   */
  private List<MahjongTileType> deducePongs(
      int[] tileCounts, List<Integer> reservedTiles, int startingTileIndex) {
    for (var reservedTile : reservedTiles) {
      tileCounts[reservedTile]++;
    }
    var pongs = new ArrayList<MahjongTileType>();
    for (var i = 1; i < tileCounts.length; i++) {
      if (tileCounts[i] == 3) {
        pongs.add(MahjongTileType.valueOfIndex(startingTileIndex - 1 + i));
        tileCounts[i] -= 3;
      }
    }
    return pongs;
  }
}
