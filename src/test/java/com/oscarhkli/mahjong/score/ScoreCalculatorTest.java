package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;

import com.oscarhkli.mahjong.score.ScoreCalculator.GroupedTiles;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ScoreCalculatorTest {

  ScoreCalculator scoreCalculator = new ScoreCalculator();

  @ParameterizedTest
  @MethodSource
  void constructGroupedType(List<String> tiles, String type, List<GroupedTiles> expected) {
    var groupedTiles = scoreCalculator.construct(type, tiles);
    then(groupedTiles).usingRecursiveComparison().isIn(expected);
  }

  private static Stream<Arguments> constructGroupedType() {
    return Stream.of(
        Arguments.of(
            List.of("D1", "D2", "D3"),
            "Dot",
            List.of(new GroupedTiles("Dot", List.of(List.of("D1", "D2", "D3")), List.of()))),
        Arguments.of(
            List.of("D1", "D1", "D2", "D3", "D3"),
            "Dot",
            List.of(
                new GroupedTiles("Dot", List.of(List.of("D1", "D2", "D3")), List.of("D1", "D3")))),
        Arguments.of(
            List.of(
                "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D3", "D4", "D4", "D5", "D5", "D5"),
            "Dot",
            List.of(
                new GroupedTiles(
                    "Dot",
                    List.of(
                        List.of("D1", "D2", "D3"),
                        List.of("D1", "D2", "D3"),
                        List.of("D2", "D3", "D4"),
                        List.of("D3", "D4", "D5")),
                    List.of("D5", "D5")))),
        Arguments.of(
            List.of("D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D4", "D5", "D5", "D5"),
            "Dot",
            List.of(
                new GroupedTiles(
                    "Dot",
                    List.of(
                        List.of("D1", "D2", "D3"),
                        List.of("D1", "D2", "D3"),
                        List.of("D2", "D3", "D4")),
                    List.of("D4", "D5", "D5", "D5")))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5"),
            "Dot",
            List.of(
                new GroupedTiles(
                    "Dot",
                    List.of(List.of("D1", "D2", "D3"), List.of("D3", "D4", "D5")),
                    List.of("D2", "D2")))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "D5"),
            "Dot",
            List.of(
                new GroupedTiles(
                    "Dot",
                    List.of(List.of("D1", "D2", "D3"), List.of("D3", "D4", "D5")),
                    List.of("D2", "D2", "D5")),
                new GroupedTiles(
                    "Dot",
                    List.of(List.of("D1", "D2", "D3"), List.of("D2", "D3", "D4")),
                    List.of("D2", "D5", "D5")))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "D5", "D5", "D5"),
            "Dot",
            List.of(
                new GroupedTiles(
                    "Dot",
                    List.of(List.of("D1", "D2", "D3"), List.of("D3", "D4", "D5")),
                    List.of("D2", "D2", "D5", "D5", "D5")))),
        Arguments.of(
            List.of("D1", "D3", "D5"),
            "Dot",
            List.of(new GroupedTiles("Dot", List.of(), List.of("D1", "D3", "D5")))),
        Arguments.of(List.of(), "Dot", List.of(new GroupedTiles("Dot", List.of(), List.of()))));
  }

  @Test
  @DisplayName("Test Common Hand without winds, dragons nor bonus tiles")
  void testCommonHand() {
    var tiles =
        List.of("D1", "D2", "D3", "B2", "B3", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Common Hand with duplicate tiles")
  void testCommonHandWithDuplicateTiles() {
    var tiles =
        List.of("D1", "D2", "D3", "D2", "D3", "D4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Common Hand with smaller eyes")
  void testCommonHandWithSmallerEyes() {
    var tiles =
        List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5", "C6");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Invalid Common Hand")
  void testNonCommonHand() {
    var tiles =
        List.of("D1", "D2", "D3", "B1", "B2", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(1);
  }
}
