package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MeldsFactory {

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
      var chowCount = Math.min(tileCounts[i], Math.min(tileCounts[i + 1], tileCounts[i + 2]));
      for (var j = 0; j < chowCount; j++) {
        validChowStarts.add(i);
      }
      tileCounts[i] -= chowCount;
      tileCounts[i + 1] -= chowCount;
      tileCounts[i + 2] -= chowCount;
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
