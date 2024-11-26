package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
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
    var characterGroupedTiles = construct("CHARACTER", mahjongTiles);
    var bambooGroupedTiles = construct("BAMBOO", mahjongTiles);
    var dotGroupedTiles = construct("DOT", mahjongTiles);
    var score = 0;
    if (isCommonHand(characterGroupedTiles, bambooGroupedTiles, dotGroupedTiles, mahjongTiles)) {
      score++;
    }
    return score;
  }

  private boolean isCommonHand(
      GroupedTiles characterGroupedTiles,
      GroupedTiles bambooGroupedTiles,
      GroupedTiles dotGroupedTiles,
      int[] mahjongTiles
  ) {
    if (characterGroupedTiles.chows().size()
            + bambooGroupedTiles.chows().size()
            + dotGroupedTiles.chows().size()
        != 4) {
      return false;
    }

    for (var i = 1; i <= 9; i++) {
      if (characterGroupedTiles.unusedTileArr()[i] != 0
          && characterGroupedTiles.unusedTileArr()[i] != 2) {
        return false;
      }
      if (bambooGroupedTiles.unusedTileArr()[i] != 0
          && bambooGroupedTiles.unusedTileArr()[i] != 2) {
        return false;
      }
      if (dotGroupedTiles.unusedTileArr()[i] != 0 && dotGroupedTiles.unusedTileArr()[i] != 2) {
        return false;
      }
    }

    var characterEyes = findEyes(characterGroupedTiles.unusedTileArr(), 1, 10);
    var bambooEyes = findEyes(bambooGroupedTiles.unusedTileArr(), 1, 10);
    var dotEyes = findEyes(dotGroupedTiles.unusedTileArr(), 1, 10);
    var windEyes = findEyes(mahjongTiles, 0, 4);
    var dragonEyes = findEyes(mahjongTiles, 4, 6);

    return characterEyes.size() + bambooEyes.size() + dotEyes.size() + windEyes.size() + dragonEyes.size() == 1;
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

  public record GroupedTiles(String type, List<List<MahjongTileType>> chows, int[] unusedTileArr) {}

  public GroupedTiles construct(String type, int[] tiles) {
    var allTiles = 0;
    var startIndex = MahjongTileType.getStartTileByType(type).getIndex();
    for (var i = 0; i < 9; i++) {
      allTiles += tiles[startIndex + i];
    }
    if (allTiles == 0) {
      return new GroupedTiles(type, List.of(), new int[10]);
    }

    var targetTiles = new int[10];
    System.arraycopy(tiles, startIndex, targetTiles, 1, 9);
    var groupedTilesCandidates = new ArrayList<GroupedTiles>();
    groupedTilesCandidates.add(
        constructGroupedTiles(type, Arrays.copyOf(targetTiles, 10), List.of()));
    for (var i = 1; i <= 9; i++) {
      if (targetTiles[i] >= 2) {
        var adjustedTileCounts = Arrays.copyOf(targetTiles, 10);
        adjustedTileCounts[i] -= 2;
        groupedTilesCandidates.add(constructGroupedTiles(type, adjustedTileCounts, List.of(i, i)));
      }
    }
    log.debug("groupedTilesCandidates: {}", groupedTilesCandidates);
    return deduceBestGroupedTiles(groupedTilesCandidates);
  }

  private GroupedTiles deduceBestGroupedTiles(List<GroupedTiles> groupedTilesCandidates) {
    if (groupedTilesCandidates.size() == 1) {
      return groupedTilesCandidates.getFirst();
    }
    GroupedTiles bestGroupTilesCandidate = null;
    var maxChowsSize = -1;
    var maxUnusedPairs = -1;
    for (var current : groupedTilesCandidates) {
      var currentChowSize = current.chows().size();
      var currentUnusedPairs = 0;
      for (var unusedTileCount : current.unusedTileArr()) {
        if (unusedTileCount >= 2) {
          currentUnusedPairs++;
        }
      }
      if (currentChowSize > maxChowsSize) {
        bestGroupTilesCandidate = current;
        maxChowsSize = currentChowSize;
        maxUnusedPairs = currentUnusedPairs;
      } else if (currentChowSize == maxChowsSize && currentUnusedPairs > maxUnusedPairs) {
        bestGroupTilesCandidate = current;
        maxUnusedPairs = currentUnusedPairs;
      }
    }
    return bestGroupTilesCandidate;
  }

  private GroupedTiles constructGroupedTiles(
      String type, int[] tileCounts, List<Integer> reservedTiles) {
    var validChowStarts = new ArrayList<Integer>();
    for (var i = 1; i <= 7; i++) {
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

    var startIndex = MahjongTileType.getStartTileByType(type).getIndex();
    var chows =
        validChowStarts.stream()
            .map(validChowStart -> validChowStart + startIndex - 1)
            .map(
                index ->
                    Stream.of(index, index + 1, index + 2)
                        .map(MahjongTileType::valueOfIndex)
                        .toList())
            .toList();
    for (var reservedTile : reservedTiles) {
      tileCounts[reservedTile]++;
    }
    return new GroupedTiles(type, chows, tileCounts);
  }
}
