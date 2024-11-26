package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScoreCalculator {

  public record Pair<K, V>(K key, V value) {
    public static <K, V> Pair<K, V> of(K key, V value) {
      return new Pair<>(key, value);
    }
  }

  public int calculate(List<String> tiles) {
    var characters = new ArrayList<String>();
    var bamboos = new ArrayList<String>();
    var dots = new ArrayList<String>();
    var winds = new ArrayList<String>();
    var dragons = new ArrayList<String>();

    for (var tile : tiles) {
      if ("Red".equals(tile) || "Green".equals(tile) || "White".equals(tile)) {
        dragons.add(tile);
      } else if ("East".equals(tile)
          || "South".equals(tile)
          || "West".equals(tile)
          || "North".equals(tile)) {
        winds.add(tile);
      } else if (tile.startsWith("C")) {
        characters.add(tile);
      } else if (tile.startsWith("B")) {
        bamboos.add(tile);
      } else {
        dots.add(tile);
      }
    }

    var characterGroupedTiles = construct("Character", characters);
    var bambooGroupedTiles = construct("Bamboo", bamboos);
    var dotGroupedTiles = construct("Dot", dots);
    if (isCommonHand(characterGroupedTiles, bambooGroupedTiles, dotGroupedTiles)) {
      return 1;
    }

    return 0;
  }

  private boolean isCommonHand(
      GroupedTiles characterGroupedTiles,
      GroupedTiles bambooGroupedTiles,
      GroupedTiles dotGroupedTiles) {
    if (characterGroupedTiles.getMelds().size() == 4) {
      return false;
    }
    if (bambooGroupedTiles.getMelds().size() == 4) {
      return false;
    }
    if (dotGroupedTiles.getMelds().size() == 4) {
      return false;
    }
    if (characterGroupedTiles.getMelds().size()
            + bambooGroupedTiles.getMelds().size()
            + dotGroupedTiles.getMelds().size()
        != 4) {
      return false;
    }

    var unusedCharacterTileCounts = new int[10];
    for (var unusedTile : characterGroupedTiles.getUnusedTiles()) {
      unusedCharacterTileCounts[unusedTile.charAt(1) - '0']++;
    }
    var characterEye = 0;
    for (var i = 1; i <= 9; i++) {
      if (unusedCharacterTileCounts[i] != 0 && unusedCharacterTileCounts[i] != 2) {
        return false;
      }
      if (unusedCharacterTileCounts[i] == 2) {
        if (characterEye != 0) {
          return false;
        }
        characterEye = i;
      }
    }
    var unusedBambooTileCounts = new int[10];
    for (var unusedTile : bambooGroupedTiles.getUnusedTiles()) {
      unusedBambooTileCounts[unusedTile.charAt(1) - '0']++;
    }
    var bambooEye = 0;
    for (var i = 1; i <= 9; i++) {
      if (unusedBambooTileCounts[i] != 0 && unusedBambooTileCounts[i] != 2) {
        return false;
      }
      if (unusedBambooTileCounts[i] == 2) {
        if (bambooEye != 0) {
          return false;
        }
        bambooEye = i;
      }
    }
    var unusedDotTileCounts = new int[10];
    for (var unusedTile : dotGroupedTiles.getUnusedTiles()) {
      unusedDotTileCounts[unusedTile.charAt(1) - '0']++;
    }
    var dotEye = 0;
    for (var i = 1; i <= 9; i++) {
      if (unusedDotTileCounts[i] != 0 && unusedDotTileCounts[i] != 2) {
        return false;
      }
      if (unusedDotTileCounts[i] == 2) {
        if (dotEye != 0) {
          return false;
        }
        dotEye = i;
      }
    }

    return characterEye > 0 && bambooEye == 0 && dotEye == 0
        || characterEye == 0 && bambooEye > 0 && dotEye == 0
        || characterEye == 0 && bambooEye == 0 && dotEye > 0;
  }

  @Value
  public static class GroupedTiles {
    String type;
    List<List<String>> melds;
    List<String> unusedTiles;
  }

  public GroupedTiles construct(String type, List<String> tiles) {
    if (tiles.isEmpty()) {
      return new GroupedTiles(type, List.of(), List.of());
    }
    var tileCounts = new int[10];
    for (var tile : tiles) {
      tileCounts[tile.charAt(1) - '0']++;
    }
    var groupedTilesCandidates = new ArrayList<GroupedTiles>();
    groupedTilesCandidates.add(
        constructGroupedTiles(type, Arrays.copyOf(tileCounts, tileCounts.length), List.of()));
    for (var i = 1; i <= 9; i++) {
      if (tileCounts[i] >= 2) {
        var adjustedTileCounts = Arrays.copyOf(tileCounts, tileCounts.length);
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
    var maxMeldsSize = -1;
    var maxUnusedPairs = -1;
    for (var current : groupedTilesCandidates) {
      var currentMeldSize = current.getMelds().size();
      var unusedTileCounts = new int[10];
      for (var unusedTile : current.getUnusedTiles()) {
        unusedTileCounts[unusedTile.charAt(1) - '0']++;
      }
      var currentUnusedPairs = 0;
      for (var unusedTileCount : unusedTileCounts) {
        if (unusedTileCount >= 2) {
          currentUnusedPairs++;
        }
      }
      if (currentMeldSize > maxMeldsSize) {
        bestGroupTilesCandidate = current;
        maxMeldsSize = currentMeldSize;
        maxUnusedPairs = currentUnusedPairs;
      } else if (currentMeldSize == maxMeldsSize && currentUnusedPairs > maxUnusedPairs) {
        bestGroupTilesCandidate = current;
        maxUnusedPairs = currentUnusedPairs;
      }
    }
    return bestGroupTilesCandidate;
  }

  private GroupedTiles constructGroupedTiles(
      String type, int[] tileCounts, List<Integer> reservedTiles) {
    var validMelds = new ArrayList<List<Integer>>();
    for (var i = 1; i <= 7; i++) {
      while (tileCounts[i] > 0) {
        if (tileCounts[i + 1] == 0 || tileCounts[i + 2] == 0) {
          break;
        }
        validMelds.add(List.of(i, i + 1, i + 2));
        tileCounts[i]--;
        tileCounts[i + 1]--;
        tileCounts[i + 2]--;
      }
    }

    var prefix = type.charAt(0);
    var melds =
        validMelds.stream()
            .map(
                validMeld ->
                    validMeld.stream()
                        .map(tileValue -> String.format("%s%d".formatted(prefix, tileValue)))
                        .toList())
            .toList();
    var unusedTiles = new ArrayList<String>();
    for (var i = 1; i <= 9; i++) {
      for (var j = 0; j < tileCounts[i]; j++) {
        unusedTiles.add(String.format("%s%d", prefix, i));
      }
    }
    for (var reservedTile : reservedTiles) {
      unusedTiles.add(String.format("%s%d", prefix, reservedTile));
    }
    unusedTiles.sort(String::compareTo);

    return new GroupedTiles(type, melds, unusedTiles);
  }

}
