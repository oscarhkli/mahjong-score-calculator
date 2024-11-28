package com.oscarhkli.mahjong.score;

import static com.oscarhkli.mahjong.score.MahjongConstant.SUITED;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScoreCalculator {

  public static final int MAHJONG_TYPES = 42;

  int[] constructMahjongTiles(List<String> tileStrings) {
    var mahjongTiles = new int[MAHJONG_TYPES];
    for (var tile : tileStrings) {
      mahjongTiles[MahjongTileType.valueOf(tile).getIndex()]++;
    }
    return mahjongTiles;
  }

  public int calculate(List<String> tiles) {
    var mahjongTiles = constructMahjongTiles(tiles);
    var windGroupedTiles = construct(MahjongSetType.WIND, mahjongTiles);
    var dragonGroupedTiles = construct(MahjongSetType.DRAGON, mahjongTiles);
    var characterGroupedTiles = construct(MahjongSetType.CHARACTER, mahjongTiles);
    var bambooGroupedTiles = construct(MahjongSetType.BAMBOO, mahjongTiles);
    var dotGroupedTiles = construct(MahjongSetType.DOT, mahjongTiles);

    if (!hasEyes(characterGroupedTiles, bambooGroupedTiles, dotGroupedTiles, mahjongTiles)) {
      return -1;
    }
    var score = 0;
    if (isCommonHand(characterGroupedTiles, bambooGroupedTiles, dotGroupedTiles)) {
      score++;
    }
    if (isAllInTriplets(
        windGroupedTiles,
        dragonGroupedTiles,
        characterGroupedTiles,
        bambooGroupedTiles,
        dotGroupedTiles)) {
      score += 3;
    }
    return score;
  }

  private boolean hasEyes(
      GroupedTiles characterGroupedTiles,
      GroupedTiles bambooGroupedTiles,
      GroupedTiles dotGroupedTiles,
      int[] mahjongTiles) {
    for (var i = 1; i <= 9; i++) {
      if (characterGroupedTiles.getUnusedTileArr()[i] != 0
          && characterGroupedTiles.getUnusedTileArr()[i] != 2) {
        return false;
      }
      if (bambooGroupedTiles.getUnusedTileArr()[i] != 0
          && bambooGroupedTiles.getUnusedTileArr()[i] != 2) {
        return false;
      }
      if (dotGroupedTiles.getUnusedTileArr()[i] != 0
          && dotGroupedTiles.getUnusedTileArr()[i] != 2) {
        return false;
      }
    }
    var characterEyes = findEyes(characterGroupedTiles.getUnusedTileArr(), 1, 10);
    var bambooEyes = findEyes(bambooGroupedTiles.getUnusedTileArr(), 1, 10);
    var dotEyes = findEyes(dotGroupedTiles.getUnusedTileArr(), 1, 10);
    var windEyes = findEyes(mahjongTiles, 0, 4);
    var dragonEyes = findEyes(mahjongTiles, 4, 6);

    return characterEyes.size()
            + bambooEyes.size()
            + dotEyes.size()
            + windEyes.size()
            + dragonEyes.size()
        == 1;
  }

  private boolean isCommonHand(
      GroupedTiles characterGroupedTiles,
      GroupedTiles bambooGroupedTiles,
      GroupedTiles dotGroupedTiles) {
    return characterGroupedTiles.getChows().size()
            + bambooGroupedTiles.getChows().size()
            + dotGroupedTiles.getChows().size()
        == 4;
  }

  private boolean isAllInTriplets(
      GroupedTiles windGroupedTiles,
      GroupedTiles dragonGroupedTiles,
      GroupedTiles characterGroupedTiles,
      GroupedTiles bambooGroupedTiles,
      GroupedTiles dotGroupedTiles) {
    return windGroupedTiles.getPongs().size()
            + dragonGroupedTiles.getPongs().size()
            + characterGroupedTiles.getPongs().size()
            + bambooGroupedTiles.getPongs().size()
            + dotGroupedTiles.getPongs().size()
        == 4;
  }

  public Set<Integer> findEyes(int[] tiles, int from, int toExclusive) {
    var eyes = new HashSet<Integer>();
    for (var i = from; i < toExclusive; i++) {
      if (tiles[i] == 2) {
        eyes.add(i);
      }
    }
    return eyes;
  }

  @Value
  public static class GroupedTiles {

    MahjongSetType mahjongSetType;
    List<List<MahjongTileType>> chows;
    List<MahjongTileType> pongs;
    List<MahjongTileType> kongs;
    int[] unusedTileArr;
  }

  public GroupedTiles construct(MahjongSetType mahjongSetType, int[] tiles) {
    var allTiles = 0;
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    var mahjongSetSize = mahjongSetType.getSize();
    for (var i = 0; i < mahjongSetSize; i++) {
      allTiles += tiles[startingTileIndex + i];
    }
    if (allTiles == 0) {
      return new GroupedTiles(
          mahjongSetType, List.of(), List.of(), List.of(), new int[mahjongSetSize + 1]);
    }

    var targetTiles = new int[mahjongSetSize + 1];
    System.arraycopy(tiles, startingTileIndex, targetTiles, 1, mahjongSetSize);
    var groupedTilesCandidates = new HashSet<GroupedTiles>();
    groupedTilesCandidates.add(
        constructGroupedTiles(
            mahjongSetType, Arrays.copyOf(targetTiles, mahjongSetSize + 1), List.of(), false));
    groupedTilesCandidates.add(
        constructGroupedTiles(
            mahjongSetType, Arrays.copyOf(targetTiles, mahjongSetSize + 1), List.of(), true));
    for (var i = 1; i <= mahjongSetSize; i++) {
      if (targetTiles[i] >= 2) {
        var adjustedTileCounts = Arrays.copyOf(targetTiles, mahjongSetSize + 1);
        adjustedTileCounts[i] -= 2;
        groupedTilesCandidates.add(
            constructGroupedTiles(mahjongSetType, adjustedTileCounts, List.of(i, i), true));
      }
    }
    log.info("groupedTilesCandidates: {}", groupedTilesCandidates);
    return deduceBestGroupedTiles(groupedTilesCandidates);
  }

  private GroupedTiles deduceBestGroupedTiles(Set<GroupedTiles> groupedTilesCandidates) {
    if (groupedTilesCandidates.size() == 1) {
      return groupedTilesCandidates.iterator().next();
    }
    GroupedTiles bestGroupTilesCandidate = null;
    var maxChowsSize = -1;
    var minUnusedTiles = 999;
    var maxUnusedPairs = -1;
    for (var current : groupedTilesCandidates) {
      var currentChowSize = current.getChows().size();
      var currentUnusedTiles = 0;
      var currentUnusedPairs = 0;
      for (var unusedTileCount : current.getUnusedTileArr()) {
        currentUnusedTiles += unusedTileCount;
        if (unusedTileCount >= 2) {
          currentUnusedPairs++;
        }
      }
      log.info(
          "currentChowSize: {}, currentUnusedTiles: {}, currentUnusedPairs: {}",
          currentChowSize,
          currentUnusedTiles,
          currentUnusedPairs);
      if (currentUnusedTiles < minUnusedTiles) {
        bestGroupTilesCandidate = current;
        maxChowsSize = currentChowSize;
        minUnusedTiles = currentUnusedTiles;
        maxUnusedPairs = currentUnusedPairs;
        log.info("currentUnusedTiles < minUnusedTiles");
      } else if (currentUnusedTiles == minUnusedTiles) {
        if (currentChowSize > maxChowsSize) {
          bestGroupTilesCandidate = current;
          maxChowsSize = currentChowSize;
          maxUnusedPairs = currentUnusedPairs;
          log.info("currentChowSize > maxChowsSize");
        } else if (currentChowSize == maxChowsSize && currentUnusedPairs > maxUnusedPairs) {
          bestGroupTilesCandidate = current;
          maxUnusedPairs = currentUnusedPairs;
          log.info("currentUnusedPairs > maxUnusedPairs");
        }
      }
    }
    return bestGroupTilesCandidate;
  }

  private GroupedTiles constructGroupedTiles(
      MahjongSetType mahjongSetType,
      int[] tileCounts,
      List<Integer> reservedTiles,
      boolean checkChowFirst) {
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    List<List<MahjongTileType>> chows;
    List<MahjongTileType> pongs;
    if (checkChowFirst) {
      chows = deduceChows(mahjongSetType, tileCounts, startingTileIndex);
      pongs = deducePongs(mahjongSetType, tileCounts, reservedTiles, startingTileIndex);
    } else {
      pongs = deducePongs(mahjongSetType, tileCounts, reservedTiles, startingTileIndex);
      chows = deduceChows(mahjongSetType, tileCounts, startingTileIndex);
    }
    return new GroupedTiles(mahjongSetType, chows, pongs, List.of(), tileCounts);
  }

  /**
   * tileCounts may be altered when counting chows
   * @param mahjongSetType
   * @param tileCounts
   * @param startingTileIndex
   * @return List of deduced Chows
   */
  private List<List<MahjongTileType>> deduceChows(
      MahjongSetType mahjongSetType, int[] tileCounts, int startingTileIndex) {
    if (!SUITED.equals(mahjongSetType.getFamily())) {
      return List.of();
    }
    var validChowStarts = new ArrayList<Integer>();
    for (var i = 1; i <= mahjongSetType.getSize() - 2; i++) {
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
   * @param mahjongSetType
   * @param tileCounts
   * @param reservedTiles
   * @param startingTileIndex
   * @return List of deduced Pongs
   */
  private List<MahjongTileType> deducePongs(
      MahjongSetType mahjongSetType, int[] tileCounts, List<Integer> reservedTiles, int startingTileIndex) {
    for (var reservedTile : reservedTiles) {
      tileCounts[reservedTile]++;
    }
    var pongs = new ArrayList<MahjongTileType>();
    for (var i = 1; i <= mahjongSetType.getSize(); i++) {
      if (tileCounts[i] == 3) {
        pongs.add(MahjongTileType.valueOfIndex(startingTileIndex - 1 + i));
        tileCounts[i] -= 3;
      }
    }
    return pongs;
  }
}
