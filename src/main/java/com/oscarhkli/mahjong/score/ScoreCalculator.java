package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    var mahjongTiles = constructMahjongTiles(tiles);
    var windGroupedTiles = construct(MahjongSetType.WIND, mahjongTiles);
    var dragonGroupedTiles = construct(MahjongSetType.DRAGON, mahjongTiles);
    var characterGroupedTiles = construct(MahjongSetType.CHARACTER, mahjongTiles);
    var bambooGroupedTiles = construct(MahjongSetType.BAMBOO, mahjongTiles);
    var dotGroupedTiles = construct(MahjongSetType.DOT, mahjongTiles);

    if (!hasEyes(
        windGroupedTiles,
        dragonGroupedTiles,
        characterGroupedTiles,
        bambooGroupedTiles,
        dotGroupedTiles)) {
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
      Melds windMelds, Melds dragonMelds, Melds characterMelds, Melds bambooMelds, Melds dotMelds) {
    for (var i = 1; i <= MahjongSetType.WIND.getSize(); i++) {
      if (windMelds.getUnusedTiles()[i] > 0) {
        return false;
      }
    }
    for (var i = 1; i <= MahjongSetType.DRAGON.getSize(); i++) {
      if (dragonMelds.getUnusedTiles()[i] > 0) {
        return false;
      }
    }
    for (var i = 1; i <= MahjongSetType.DOT.getSize(); i++) {
      if (characterMelds.getUnusedTiles()[i] > 0) {
        return false;
      }
      if (bambooMelds.getUnusedTiles()[i] > 0) {
        return false;
      }
      if (dotMelds.getUnusedTiles()[i] > 0) {
        return false;
      }
    }
    var eyes = new HashSet<MahjongTileType>();
    eyes.add(windMelds.getEye());
    eyes.add(dragonMelds.getEye());
    eyes.add(characterMelds.getEye());
    eyes.add(bambooMelds.getEye());
    eyes.add(dotMelds.getEye());
    return eyes.stream().filter(Objects::nonNull).count() == 1;
  }

  private boolean isCommonHand(Melds characterMelds, Melds bambooMelds, Melds dotMelds) {
    return characterMelds.getChows().size()
            + bambooMelds.getChows().size()
            + dotMelds.getChows().size()
        == 4;
  }

  private boolean isAllInTriplets(
      Melds windMelds, Melds dragonMelds, Melds characterMelds, Melds bambooMelds, Melds dotMelds) {
    return windMelds.getPongs().size()
            + dragonMelds.getPongs().size()
            + characterMelds.getPongs().size()
            + bambooMelds.getPongs().size()
            + dotMelds.getPongs().size()
        == 4;
  }

  @Value
  public static class Melds {

    MahjongSetType mahjongSetType;
    List<List<MahjongTileType>> chows;
    List<MahjongTileType> pongs;
    List<MahjongTileType> kongs;
    MahjongTileType eye;
    int[] unusedTiles;
  }

  public Melds construct(MahjongSetType mahjongSetType, int[] tiles) {
    var allTiles = 0;
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    var mahjongSetSize = mahjongSetType.getSize();
    for (var i = 0; i < mahjongSetSize; i++) {
      allTiles += tiles[startingTileIndex + i];
    }
    if (allTiles == 0) {
      return new Melds(
          mahjongSetType, List.of(), List.of(), List.of(), null, new int[mahjongSetSize + 1]);
    }

    var targetTiles = new int[mahjongSetSize + 1];
    System.arraycopy(tiles, startingTileIndex, targetTiles, 1, mahjongSetSize);
    var groupedTilesCandidates = new HashSet<Melds>();
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

  private Melds deduceBestGroupedTiles(Set<Melds> meldsCandidates) {
    if (meldsCandidates.size() == 1) {
      return meldsCandidates.iterator().next();
    }
    Melds bestGroupTilesCandidate = null;
    var maxChowsSize = -1;
    var minUnusedTiles = 999;
    var maxUnusedPairs = -1;
    for (var current : meldsCandidates) {
      var currentChowSize = current.getChows().size();
      var currentUnusedTiles = 0;
      var currentUnusedPairs = 0;
      for (var unusedTileCount : current.getUnusedTiles()) {
        currentUnusedTiles += unusedTileCount;
        if (unusedTileCount >= 2) {
          currentUnusedPairs++;
        }
      }
      log.debug(
          "currentChowSize: {}, currentUnusedTiles: {}, currentUnusedPairs: {}",
          currentChowSize,
          currentUnusedTiles,
          currentUnusedPairs);
      if (currentUnusedTiles < minUnusedTiles) {
        bestGroupTilesCandidate = current;
        maxChowsSize = currentChowSize;
        minUnusedTiles = currentUnusedTiles;
        maxUnusedPairs = currentUnusedPairs;
        log.debug("currentUnusedTiles < minUnusedTiles");
      } else if (currentUnusedTiles == minUnusedTiles) {
        if (currentChowSize > maxChowsSize) {
          bestGroupTilesCandidate = current;
          maxChowsSize = currentChowSize;
          maxUnusedPairs = currentUnusedPairs;
          log.debug("currentChowSize > maxChowsSize");
        } else if (currentChowSize == maxChowsSize && currentUnusedPairs > maxUnusedPairs) {
          bestGroupTilesCandidate = current;
          maxUnusedPairs = currentUnusedPairs;
          log.debug("currentUnusedPairs > maxUnusedPairs");
        }
      }
    }
    return bestGroupTilesCandidate;
  }

  private Melds constructGroupedTiles(
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
    return new Melds(mahjongSetType, chows, pongs, List.of(), eye, tileCounts);
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
