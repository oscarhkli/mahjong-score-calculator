package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScoreCalculatorTest {

  ScoreCalculator scoreCalculator = new ScoreCalculator();

  @Test
  @DisplayName("Test Common Hand without winds, dragons nor bonus tiles")
  void testCommonHand() {
    var tiles = List.of("D1", "D2", "D3", "B2", "B3", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculateTiles(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Common Hand with duplicate tiles")
  void testCommonHandWithDuplicateTiles() {
    var tiles = List.of("D1", "D2", "D3", "D2", "D3", "D4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculateTiles(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Common Hand with smaller eyes")
  void testCommonHandWithSmallerEyes() {
    var tiles = List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5", "C6");
    var score = scoreCalculator.calculateTiles(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Invalid Common Hand")
  void testNonCommonHand() {
    var tiles = List.of("D1", "D2", "D3", "B1", "B2", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculateTiles(tiles);
    then(score).isEqualTo(0);
  }

}
