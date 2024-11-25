package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScoreCalculator {

  public record Pair<K, V>(K key, V value) {
    public static <K, V> Pair<K, V> of(K key, V value) {
      return new Pair<>(key, value);
    }
  }

  public int calculateTiles(List<String> tiles) {
    var sortedTiles = tiles.stream().sorted().toList();
    var used = new boolean[tiles.size()];
    var validTriplets = new ArrayList<List<String>>();
    calculateCommonHand(sortedTiles, 0, new ArrayList<>(), validTriplets, used);

    log.info("Valid triplets: {}", validTriplets);
    if (validTriplets.size() == 4) {
      String first = null;
      for (var i = 0; i < used.length; i++) {
        if (!used[i]) {
          if (first == null) {
            first = tiles.get(i);
          } else {
            log.info("first: {}, second: {}", first, tiles.get(i));
            return isPair(first, tiles.get(i)) ? 1 : 0;
          }
        }
      }
    }
    return 0;
  }

  private boolean isPair(String a, String b) {
    return a.equals(b);
  }

  private boolean isSameTileAndConsecutive(String a, String b) {
    return a.charAt(0) == b.charAt(0) && a.charAt(1) + 1 == b.charAt(1);
  }

  private void calculateCommonHand(
      List<String> tiles,
      int index,
      List<Pair<String, Integer>> subset,
      List<List<String>> validTriplets,
      boolean[] used) {
    // Validate
    for (var selected : subset) {
      if (used[selected.value]) {
        return;
      }
    }
    if (subset.size() == 2) {
      if (!isSameTileAndConsecutive(subset.get(0).key(), subset.get(1).key())) {
        return;
      }
    } else if (subset.size() == 3) {
      if (!isSameTileAndConsecutive(subset.get(1).key(), subset.get(2).key())) {
        return;
      }
      // Add to validTriplets
      var triplet = new ArrayList<String>();
      for (var selected : subset) {
        log.info("add to valid triplet: {}", selected);
        triplet.add(selected.key());
        used[selected.value()] = true;
      }
      validTriplets.add(triplet);
      return;
    }
    // Backtracking
    for (var i = index; i < tiles.size(); i++) {
      if (used[i]) {
        continue;
      }
      subset.add(new Pair<>(tiles.get(i), i));
      calculateCommonHand(tiles, i + 1, subset, validTriplets, used);
      subset.removeLast();
    }
  }
}
