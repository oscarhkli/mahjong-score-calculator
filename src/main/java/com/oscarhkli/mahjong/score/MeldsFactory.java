package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MeldsFactory {

  public List<Melds> construct(
      MahjongSetType mahjongSetType, int[] tiles, ExposedMelds exposedMelds) {
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    var mahjongSetSize = mahjongSetType.getSize();
    var endingTileIndex = mahjongSetType.getEndingTile().getIndex();

    var matchedExposedMelds = new ExposedMelds(exposedMelds, mahjongSetType);

    // Count total tiles
    var allTiles = Arrays.stream(tiles, startingTileIndex, endingTileIndex + 1).sum();
    if (allTiles == 0 && matchedExposedMelds.isEmpty()) {
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
    addMeldsCandidates(meldsCandidates, mahjongSetType, targetTiles, matchedExposedMelds);

    return deduceBestMelds(meldsCandidates);
  }

  private void addMeldsCandidates(
      Set<Melds> meldsCandidates,
      MahjongSetType mahjongSetType,
      int[] targetTiles,
      ExposedMelds exposedMelds) {
    meldsCandidates.add(
        constructMelds(mahjongSetType, targetTiles.clone(), exposedMelds, List.of(), false));
    meldsCandidates.add(
        constructMelds(mahjongSetType, targetTiles.clone(), exposedMelds, List.of(), true));

    for (int i = 1; i < targetTiles.length; i++) {
      if (targetTiles[i] >= 2) {
        var adjustedTileCounts = targetTiles.clone();
        adjustedTileCounts[i] -= 2;
        meldsCandidates.add(
            constructMelds(mahjongSetType, adjustedTileCounts, exposedMelds, List.of(i, i), true));
      }
    }
  }

  private Melds constructMelds(
      MahjongSetType mahjongSetType,
      int[] tileCounts,
      ExposedMelds exposedMelds,
      List<Integer> reservedTiles,
      boolean checkChowFirst) {
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    var chows = new ArrayList<>(exposedMelds.getChows());
    var pongs = new ArrayList<>(exposedMelds.getPongs());
    if (checkChowFirst) {
      chows.addAll(deduceChows(mahjongSetType, tileCounts, startingTileIndex));
      pongs.addAll(deducePongs(tileCounts, reservedTiles, startingTileIndex));
    } else {
      pongs.addAll(deducePongs(tileCounts, reservedTiles, startingTileIndex));
      chows.addAll(deduceChows(mahjongSetType, tileCounts, startingTileIndex));
    }

    chows.sort(Comparator.comparingInt(MahjongTileType::getIndex));
    pongs.sort(Comparator.comparingInt(MahjongTileType::getIndex));

    var eye = deduceEye(tileCounts, startingTileIndex);
    var unusedTileCount = 0;
    var unusedPairs = 0;
    for (var unusedTile : tileCounts) {
      unusedTileCount += unusedTile;
      if (unusedTile >= 2) {
        unusedPairs++;
      }
    }
    return new Melds(
        mahjongSetType,
        chows,
        pongs,
        exposedMelds.getKongs(),
        eye,
        tileCounts,
        unusedTileCount,
        unusedPairs);
  }

  private List<Melds> deduceBestMelds(Set<Melds> meldsCandidates) {
    // Sort candidates based on the given criteria
    var priorityQueue =
        new PriorityQueue<>(
            Comparator.comparingInt(Melds::getUnusedTileCount)
                .thenComparingInt(Melds::getUnusedPairs)
                .thenComparing((Melds m) -> -m.getPongKongSize())
                .thenComparing((Melds m) -> -m.getChows().size())
                .thenComparing(Melds::getEye));

    priorityQueue.addAll(meldsCandidates);
    Objects.requireNonNull(priorityQueue.peek());

    // If no valid melds, return the best Trick Hand candidate
    if (priorityQueue.size() == 1 || priorityQueue.peek().getUnusedTileCount() > 0) {
      return List.of(priorityQueue.poll());
    }

    // Collect only valid melds with unusedTileCount == 0 and unusedPairs == 0
    var bestMelds = new ArrayList<Melds>();
    while (!priorityQueue.isEmpty()) {
      var candidate = priorityQueue.poll();
      if (candidate.getUnusedTileCount() == 0 && candidate.getUnusedPairs() == 0) {
        bestMelds.add(candidate);
      }
    }

    return bestMelds;
  }

  /**
   * tileCounts may be altered when counting chows
   *
   * @param mahjongSetType Mahjong Set Type, e.g., SUITED, HONOR
   * @param tileCounts Counter of each tile of the current Mahjong Set Type
   * @param startingTileIndex Start index of current Mahjong Set Type
   * @return List of deduced Chows
   */
  private List<MahjongTileType> deduceChows(
      MahjongSetType mahjongSetType, int[] tileCounts, int startingTileIndex) {
    if (!MahjongConstant.SUITED.equals(mahjongSetType.getFamily())) {
      return List.of();
    }

    var chows = new ArrayList<MahjongTileType>();
    for (int i = 1; i < tileCounts.length - 2; i++) {
      while (tileCounts[i] > 0 && tileCounts[i + 1] > 0 && tileCounts[i + 2] > 0) {
        chows.add(MahjongTileType.valueOfIndex(startingTileIndex - 1 + i));
        tileCounts[i]--;
        tileCounts[i + 1]--;
        tileCounts[i + 2]--;
      }
    }
    return chows;
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

  private MahjongTileType deduceEye(int[] tileCounts, int startingTileIndex) {
    for (var i = 1; i < tileCounts.length; i++) {
      if (tileCounts[i] == 2) {
        tileCounts[i] = 0;
        return MahjongTileType.valueOfIndex(startingTileIndex - 1 + i);
      }
    }
    return null;
  }
}
