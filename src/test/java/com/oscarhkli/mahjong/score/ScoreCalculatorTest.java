package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.oscarhkli.mahjong.score.ScoreCalculator.Melds;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
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
  void constructMelds(List<String> tiles, MahjongSetType mahjongSetType, List<Melds> expected) {
    var mahjongTiles = scoreCalculator.constructMahjongTiles(tiles);
    var melds = scoreCalculator.construct(mahjongSetType, mahjongTiles);
    then(melds)
        .as("Tiles %s".formatted(tiles))
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> constructMelds() {
    return Stream.of(
        Arguments.of(
            List.of("D1", "D2", "D3"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3)),
                    List.of(),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            List.of("D1", "D1", "D2", "D3", "D3"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3)),
                    List.of(),
                    List.of(),
                    null,
                    new int[] {0, 1, 0, 1, 0, 0, 0, 0, 0, 0},
                    2,
                    0))),
        Arguments.of(
            List.of(
                "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D3", "D4", "D4", "D5", "D5", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    List.of(),
                    List.of(),
                    MahjongTileType.D5,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            List.of("D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D4", "D5", "D5", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4)),
                    List.of(MahjongTileType.D5),
                    List.of(),
                    null,
                    new int[] {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                    1,
                    0))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    List.of(),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    List.of(),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                    1,
                    0))),
        Arguments.of(
            List.of("D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "D5", "D5", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    List.of(MahjongTileType.D5),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "D1", "D2", "D3", "D2", "D3", "D4", "D2", "D3", "D4", "D5", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4),
                        List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4)),
                    List.of(),
                    List.of(),
                    MahjongTileType.D5,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0),
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5),
                        List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5)),
                    List.of(),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            List.of("D1", "D3", "D5"),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(),
                    List.of(),
                    List.of(),
                    null,
                    new int[] {0, 1, 0, 1, 0, 1, 0, 0, 0, 0},
                    3,
                    0))),
        Arguments.of(
            List.of(),
            MahjongSetType.DOT,
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(),
                    List.of(),
                    List.of(),
                    null,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            List.of("B1", "B1", "B1"),
            "BAMBOO",
            List.of(
                new Melds(
                    MahjongSetType.BAMBOO,
                    List.of(),
                    List.of(MahjongTileType.B1),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            List.of("SOUTH", "SOUTH", "SOUTH", "EAST", "EAST", "EAST", "NORTH", "NORTH"),
            "WIND",
            List.of(
                new Melds(
                    MahjongSetType.WIND,
                    List.of(),
                    List.of(MahjongTileType.EAST, MahjongTileType.SOUTH),
                    List.of(),
                    MahjongTileType.NORTH,
                    new int[] {0, 0, 0, 0, 0},
                    0,
                    0))));
  }

  @ParameterizedTest
  @MethodSource
  void calculateScore(Set<WinningHandType> fakeWinningHand, int expected) {
    var score = scoreCalculator.calculateScore(fakeWinningHand);
    then(score).isEqualTo(expected);
  }

  private static Stream<Arguments> calculateScore() {
    return Stream.of(
        Arguments.of(Set.of(WinningHandType.TRICK_HAND), -1),
        Arguments.of(Set.of(WinningHandType.TRICK_HAND, WinningHandType.ALL_ONE_SUIT), -1),
        Arguments.of(Set.of(WinningHandType.CHICKEN_HAND, WinningHandType.ALL_ONE_SUIT), 7),
        Arguments.of(Set.of(WinningHandType.COMMON_HAND, WinningHandType.ALL_ONE_SUIT), 8),
        Arguments.of(Set.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.ALL_ONE_SUIT), 10),
        Arguments.of(Set.of(WinningHandType.ALL_HONOR_TILES, WinningHandType.ALL_ONE_SUIT), 10),
        Arguments.of(
            Set.of(
                WinningHandType.SMALL_WINDS,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS),
            9));
  }

  @ParameterizedTest
  @MethodSource
  void deduceWinningHands(List<String> tileStrings, Set<WinningHandType> expected) {
    var winningHandTypes = scoreCalculator.calculateWinningHands(tileStrings);
    then(winningHandTypes)
        .as("tiles: %s".formatted(tileStrings))
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> deduceWinningHands() {
    return Stream.of(
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "B2", "B3", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5"),
            Set.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "D2", "D3", "D4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5"),
            Set.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5", "C6"),
            Set.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D5", "D6", "D9", "D9"),
            Set.of(WinningHandType.COMMON_HAND, WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D1", "D2", "WEST", "WEST", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5",
                "C6"),
            Set.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B2", "B2", "B2", "C5", "C5", "C5", "C7", "C7", "C7", "D5", "D5"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B2", "B2", "B2", "C5", "C5", "C5", "C7", "C7", "C7", "EAST",
                "EAST"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B2", "B2", "B2", "C5", "C5", "C5", "C7", "C7", "C7", "GREEN",
                "GREEN"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "WEST", "WEST", "WEST", "B2", "B2", "B2", "WHITE", "WHITE", "WHITE", "C7", "C7",
                "C7", "D5", "D5"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "C2", "C2", "C2", "D3", "D4", "D5", "D7", "D7", "D7", "B9", "B9"),
            Set.of(WinningHandType.CHICKEN_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D3", "D4", "D5", "D5", "D5", "D7", "D7", "D7", "D9", "D9"),
            Set.of(WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D1", "D1", "D2", "D2", "D2", "D2", "D3", "D4", "D7", "D8", "D9", "D9", "D9", "D9"),
            Set.of(WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "B9", "B9", "B9"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "D9", "D9", "D9"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "D1", "D2", "D3"),
            Set.of(WinningHandType.COMMON_HAND)), // Edge cass
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B9", "B9", "B9", "WEST",
                "WEST"),
            Set.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B7", "B8", "B9", "WEST",
                "WEST"),
            Set.of(WinningHandType.COMMON_HAND, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B4", "B5", "B6", "B7", "B8", "B9", "WEST",
                "WEST"),
            Set.of(WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "RED",
                "RED", "RED", "GREEN", "GREEN"),
            Set.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "NORTH",
                "NORTH", "NORTH", "GREEN", "GREEN"),
            Set.of(WinningHandType.GREAT_WINDS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WHITE", "WHITE", "WHITE", "RED",
                "RED", "RED", "GREEN", "GREEN"),
            Set.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "D5",
                "D6", "D7", "NORTH", "NORTH"),
            Set.of(WinningHandType.SMALL_WINDS, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "D5",
                "D5", "D5", "NORTH", "NORTH"),
            Set.of(
                WinningHandType.SMALL_WINDS,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "RED",
                "RED", "RED", "NORTH", "NORTH"),
            Set.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "NORTH",
                "RED", "RED", "NORTH", "NORTH"),
            Set.of(WinningHandType.GREAT_WINDS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "NORTH",
                "D1", "D1", "NORTH", "NORTH"),
            Set.of(WinningHandType.GREAT_WINDS)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "D1", "D1", "D1",
                "D2", "D2", "D2"),
            Set.of(
                WinningHandType.SMALL_DRAGON,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "D1", "D2", "D3",
                "D2", "D2", "D2"),
            Set.of(WinningHandType.SMALL_DRAGON, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "D1", "D2", "D3",
                "C1", "C2", "C3"),
            Set.of(WinningHandType.SMALL_DRAGON)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "D1",
                "D1", "D2", "D2", "D2"),
            Set.of(
                WinningHandType.GREAT_DRAGON,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "D1",
                "D3", "D2", "D2", "D2"),
            Set.of(WinningHandType.GREAT_DRAGON, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "D2",
                "D3", "C1", "C2", "C3"),
            Set.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "C3",
                "C3", "C2", "C2", "C2"),
            Set.of(
                WinningHandType.GREAT_DRAGON,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "B1", "B2", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5"),
            Set.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "D1", "WEST", "D3", "B1", "GREEN", "B4", "C1", "C2", "C3", "WHITE", "C5", "C6",
                "D5", "D5"),
            Set.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D3", "D3", "GREEN", "GREEN", "C1", "C1", "C3", "C3", "C5", "C5", "C6",
                "C6"),
            Set.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "C1"),
            Set.of(WinningHandType.THIRTEEN_ORPHANS)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "EAST"),
            Set.of(WinningHandType.THIRTEEN_ORPHANS)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "WHITE"),
            Set.of(WinningHandType.THIRTEEN_ORPHANS)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "D3"),
            Set.of(WinningHandType.TRICK_HAND)));
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
