package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.oscarhkli.mahjong.score.ScoreCalculator.GroupedTiles;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@ExtendWith(SoftAssertionsExtension.class)
@Slf4j
class ScoreCalculatorTest {

  ScoreCalculator scoreCalculator = new ScoreCalculator();

  @ParameterizedTest
  @MethodSource
  void constructGroupedType(List<String> tiles, String type, List<GroupedTiles> expected) {
    var mahjongTiles = scoreCalculator.constructMahjongTiles(tiles);
    var groupedTiles = scoreCalculator.construct(type, mahjongTiles);
    then(groupedTiles).usingRecursiveComparison().isIn(expected);
  }

  private static Stream<Arguments> constructGroupedType() {
    return Stream.of(
        Arguments.of(
            List.of("D1", "D2", "D3"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT", List.of(List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3)), new int[10]))),
        Arguments.of(
            List.of("D1", "D1", "D2", "D3", "D3"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3)),
                    new int[] {0, 1, 0, 1, 0, 0, 0, 0, 0, 0}))),
        Arguments.of(
            List.of(
                "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D3", "D4", "D4", "D5", "D5", "D5"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    new int[] {0, 0, 0, 0, 0, 2, 0, 0, 0, 0}))),
        Arguments.of(
            List.of("D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D4", "D5", "D5", "D5"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4)),
                    new int[] {0, 0, 0, 0, 1, 3, 0, 0, 0, 0}))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    new int[] {0, 0, 2, 0, 0, 0, 0, 0, 0, 0}))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "D5"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    new int[] {0, 0, 2, 0, 0, 1, 0, 0, 0, 0}),
                new GroupedTiles(
                    "DOT",
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4)),
                    new int[] {0, 0, 1, 0, 0, 2, 0, 0, 0, 0}))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "D5", "D5", "D5"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    new int[] {0, 0, 2, 0, 0, 3, 0, 0, 0, 0}))),
        Arguments.of(
            List.of("D1", "D3", "D5"),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(),
                    new int[] {0, 1, 0, 1, 0, 1, 0, 0, 0, 0}))),
        Arguments.of(
            List.of(),
            "DOT",
            List.of(
                new GroupedTiles(
                    "DOT",
                    List.of(),
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}))));
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
  @DisplayName("Test Common Hand with dragon eyes")
  void testCommonHandWithDragonEyes() {
    var tiles =
        List.of("D1", "D2", "RED", "RED", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5", "C6");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Common Hand with wind eyes")
  void testCommonHandWithWindEyes() {
    var tiles =
        List.of("D1", "D2", "WEST", "WEST", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5", "C6");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(1);
  }

  @Test
  @DisplayName("Test Invalid Common Hand")
  void testNonCommonHand() {
    var tiles =
        List.of("D1", "D2", "D3", "B1", "B2", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5");
    var score = scoreCalculator.calculate(tiles);
    then(score).isEqualTo(0);
  }

  @ParameterizedTest
  @MethodSource
  void constructMahjongTiles(
      List<String> tileStrings, Map<Integer, Integer> expected, BDDSoftAssertions softly) {
    var mahjongTiles = scoreCalculator.constructMahjongTiles(tileStrings);

    assertAll(
        () -> {
          for (var i = 0; i < 42; i++) {
            var expectedCount = expected.getOrDefault(i, 0);
            softly
                .then(mahjongTiles[i])
                .as("Expected mahjongTiles[%d] to be %d".formatted(i, expectedCount))
                .isEqualTo(expectedCount);
          }
        });
  }

  private static Stream<Arguments> constructMahjongTiles() {
    return Stream.of(
        Arguments.of(List.of(), Map.of()),
        Arguments.of(
            List.of("C1", "C2", "C2"),
            Map.of(
                7, 1,
                8, 2)),
        Arguments.of(
            List.of("F1", "C2", "WEST", "S1", "D1", "C1", "WEST", "F2"),
            Map.of(
                2, 2,
                7, 1,
                8, 1,
                25, 1,
                34, 1,
                35, 1,
                38, 1)));
  }
}
