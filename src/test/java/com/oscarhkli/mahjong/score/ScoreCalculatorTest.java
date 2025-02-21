package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(SoftAssertionsExtension.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
class ScoreCalculatorTest {

  @InjectMocks @Spy ScoreCalculator scoreCalculator;
  @Spy MeldsFactory meldsFactory;
  @Mock BonusWinningConditionCalculator bonusWinningConditionCalculator;
  @Mock WinningConditionCalculator winningConditionCalculator;

  @ParameterizedTest
  @MethodSource
  void deduceWinningHands(
      List<String> tileStrings,
      List<MahjongTileType> exposedChows,
      List<MahjongTileType> exposedPongs,
      List<MahjongTileType> exposedKongs,
      List<WinningHandType> expected) {
    var tiles = tileStrings.stream().map(MahjongTileType::valueOf).toList();
    var winningHandTypes =
        scoreCalculator.calculateWinningHands(
            tiles,
            new ExposedMelds(exposedChows, exposedPongs, exposedKongs),
            List.of(),
            new WindType(null, null),
            new WinningConditions(false, false,  false,false, false));
    then(winningHandTypes)
        .as("tiles: %s".formatted(tileStrings))
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> deduceWinningHands() {
    return Stream.of(
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "B2", "B3", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "D2", "D3", "D4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D2", "D2", "D2", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5", "C6"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D5", "D6", "D9", "D9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND, WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D1", "D2", "WEST", "WEST", "D3", "D3", "D4", "D5", "C1", "C2", "C3", "C4", "C5",
                "C6"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B2", "B2", "B2", "C5", "C5", "C5", "C7", "C7", "C7", "D5", "D5"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B2", "B2", "B2", "C5", "C5", "C5", "C7", "C7", "C7", "EAST",
                "EAST"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B2", "B2", "B2", "C5", "C5", "C5", "C7", "C7", "C7", "GREEN",
                "GREEN"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "WEST", "WEST", "WEST", "B2", "B2", "B2", "WHITE", "WHITE", "WHITE", "C7", "C7",
                "C7", "D5", "D5"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "C2", "C2", "C2", "D3", "D4", "D5", "D7", "D7", "D7", "B9", "B9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.CHICKEN_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D3", "D4", "D5", "D5", "D5", "D7", "D7", "D7", "D9", "D9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D1", "D1", "D2", "D2", "D2", "D2", "D3", "D4", "D7", "D8", "D9", "D9", "D9", "D9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "B9", "B9", "B9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of("B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "B9", "B9", "B9"),
            List.of(),
            List.of(),
            List.of(MahjongTileType.B1),
            List.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of("B1", "B1", "B2", "B2", "B3", "B3", "B8", "B8", "B9", "B9", "B9"),
            List.of(MahjongTileType.B1),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "D9", "D9", "D9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B8", "B8", "D1", "D2", "D3"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND)), // Edge cass
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B9", "B9", "B9", "WEST",
                "WEST"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of("B1", "B1", "B2", "B2", "B3", "B3", "B9", "B9", "B9", "WEST", "WEST"),
            List.of(MahjongTileType.B1),
            List.of(),
            List.of(),
            List.of(WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of("B3", "B3", "B3", "B9", "B9", "B9", "WEST", "WEST"),
            List.of(),
            List.of(MahjongTileType.B1, MahjongTileType.B2),
            List.of(),
            List.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B3", "B3", "B3", "B7", "B8", "B9", "WEST",
                "WEST"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.COMMON_HAND, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "B1", "B1", "B1", "B2", "B2", "B2", "B4", "B5", "B6", "B7", "B8", "B9", "WEST",
                "WEST"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "RED",
                "RED", "RED", "GREEN", "GREEN"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of("GREEN", "GREEN"),
            List.of(),
            List.of(MahjongTileType.WEST, MahjongTileType.RED),
            List.of(MahjongTileType.EAST, MahjongTileType.SOUTH),
            List.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "NORTH",
                "NORTH", "NORTH", "GREEN", "GREEN"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.GREAT_WINDS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WHITE", "WHITE", "WHITE", "RED",
                "RED", "RED", "GREEN", "GREEN"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "D5",
                "D6", "D7", "NORTH", "NORTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.SMALL_WINDS, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "D5",
                "D5", "D5", "NORTH", "NORTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.SMALL_WINDS,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "D5",
                "D5", "D5", "D4", "D4"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.MIXED_ONE_SUIT, WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "RED",
                "RED", "RED", "NORTH", "NORTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_HONOR_TILES)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "NORTH",
                "RED", "RED", "NORTH", "NORTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.GREAT_WINDS)),
        Arguments.of(
            List.of(
                "EAST", "EAST", "EAST", "SOUTH", "SOUTH", "SOUTH", "WEST", "WEST", "WEST", "NORTH",
                "D1", "D1", "NORTH", "NORTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.GREAT_WINDS)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "D1", "D1", "D1",
                "D2", "D2", "D2"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.SMALL_DRAGON,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "D1", "D2", "D3",
                "D2", "D2", "D2"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.SMALL_DRAGON, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "D1", "D2", "D3",
                "C1", "C2", "C3"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.SMALL_DRAGON)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "D1",
                "D1", "D2", "D2", "D2"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.GREAT_DRAGON,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "D1",
                "D3", "D2", "D2", "D2"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.GREAT_DRAGON, WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "D2",
                "D3", "C1", "C2", "C3"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "RED", "RED", "RED", "WHITE", "WHITE", "WHITE", "GREEN", "GREEN", "GREEN", "C3",
                "C3", "C2", "C2", "C2"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.GREAT_DRAGON,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D9", "D9", "D9", "EAST", "EAST", "EAST", "RED", "RED", "RED",
                "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.MIXED_ORPHANS,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D9", "D9", "D9", "EAST", "EAST", "EAST", "RED", "RED", "RED", "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(MahjongTileType.D1),
            List.of(
                WinningHandType.MIXED_ORPHANS,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D9", "D9", "EAST", "EAST", "EAST", "RED", "RED", "RED", "SOUTH",
                "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.MIXED_ORPHANS,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "WHITE", "WHITE", "WHITE", "D9", "D9", "EAST", "EAST", "EAST", "RED", "RED", "RED",
                "SOUTH", "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.MIXED_ORPHANS,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT)),
        Arguments.of(
            List.of(
                "WHITE", "WHITE", "WHITE", "D9", "D9", "GREEN", "GREEN", "GREEN", "RED", "RED",
                "RED", "SOUTH", "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.MIXED_ORPHANS,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.GREAT_DRAGON)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "C9", "C9", "EAST", "EAST", "EAST", "RED", "RED", "RED", "SOUTH",
                "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.MIXED_ORPHANS, WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "WHITE", "WHITE", "D9", "D9", "D9", "GREEN", "GREEN", "GREEN", "RED", "RED", "RED",
                "SOUTH", "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                WinningHandType.MIXED_ORPHANS,
                WinningHandType.ALL_IN_TRIPLETS,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.SMALL_DRAGON)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B9", "B9", "B9", "EAST", "EAST", "EAST", "RED", "RED", "RED",
                "SOUTH", "SOUTH"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.MIXED_ORPHANS, WinningHandType.ALL_IN_TRIPLETS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "B9", "B9", "B9", "D9", "D9", "D9", "C1", "C1", "C1", "C9", "C9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ORPHANS)),
        Arguments.of(
            List.of("D1", "D1", "D1", "B9", "B9", "B9", "D9", "D9", "D9", "C9", "C9"),
            List.of(),
            List.of(),
            List.of(MahjongTileType.C1),
            List.of(WinningHandType.ORPHANS)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D9", "D9", "D8"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.NINE_GATES)),
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D1", "D2", "D3", "D4", "D4"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.ALL_ONE_SUIT, WinningHandType.COMMON_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D9", "D9", "D9"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.NINE_GATES)),
        Arguments.of(
            List.of("D5", "D6", "D7", "D8", "D9", "D9", "D9", "D9"),
            List.of(MahjongTileType.D2),
            List.of(MahjongTileType.D1),
            List.of(),
            List.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of("D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D9", "D9", "D2"),
            List.of(),
            List.of(MahjongTileType.D1),
            List.of(),
            List.of(WinningHandType.ALL_ONE_SUIT)),
        Arguments.of(
            List.of(
                "D1", "D1", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "D9", "D9", "D1"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.NINE_GATES)),
        Arguments.of(
            List.of(
                "D1", "D2", "D3", "B1", "B2", "B4", "C1", "C2", "C3", "C4", "C5", "C6", "D5", "D5"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "D1", "WEST", "D3", "B1", "GREEN", "B4", "C1", "C2", "C3", "WHITE", "C5", "C6",
                "D5", "D5"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "D1", "D1", "D3", "D3", "GREEN", "GREEN", "C1", "C1", "C3", "C3", "C5", "C5", "C6",
                "C6"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.TRICK_HAND)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "C1"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.THIRTEEN_ORPHANS)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "EAST"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.THIRTEEN_ORPHANS)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "WHITE"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.THIRTEEN_ORPHANS)),
        Arguments.of(
            List.of("D5", "D5"),
            List.of(),
            List.of(),
            List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4),
            List.of(WinningHandType.ALL_KONGS)),
        Arguments.of(
            List.of("D5", "D5"),
            List.of(),
            List.of(),
            List.of(
                MahjongTileType.B1, MahjongTileType.WEST, MahjongTileType.D3, MahjongTileType.C1),
            List.of(WinningHandType.ALL_KONGS)),
        Arguments.of(
            List.of(
                "C1", "C9", "B1", "B9", "D1", "D9", "EAST", "SOUTH", "WEST", "NORTH", "RED",
                "GREEN", "WHITE", "D3"),
            List.of(),
            List.of(),
            List.of(),
            List.of(WinningHandType.TRICK_HAND)));
  }

  @ParameterizedTest
  @MethodSource
  void constructMahjongTiles(
      List<MahjongTileType> tiles, Map<Integer, Integer> expected, BDDSoftAssertions softly) {
    var mahjongTiles = scoreCalculator.constructMahjongTiles(tiles);

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
            List.of(MahjongTileType.C1, MahjongTileType.C2, MahjongTileType.C2),
            Map.of(
                7, 1,
                8, 2)),
        Arguments.of(
            List.of(
                MahjongTileType.F1,
                MahjongTileType.C2,
                MahjongTileType.WEST,
                MahjongTileType.S1,
                MahjongTileType.D1,
                MahjongTileType.C1,
                MahjongTileType.WEST,
                MahjongTileType.F2),
            Map.of(
                2, 2,
                7, 1,
                8, 1,
                25, 1,
                34, 1,
                35, 1,
                38, 1)));
  }

  @Test
  void testSelfPickWithAllInTriplets() {
    // Test case where ExposedMelds is empty, SelfPick is true, and contains ALL_IN_TRIPLETS
    WinningConditions conditions = new WinningConditions(true, false, false, false, false);
    ExposedMelds exposedMelds = new ExposedMelds(List.of(), List.of(), List.of()); // Empty melds
    List<WinningHandType> winningHands = List.of(WinningHandType.ALL_IN_TRIPLETS);
    List<WinningHandType> bonusWinningHands = List.of();

    List<WinningHandType> result = scoreCalculator.constructFinalWinningHands(conditions, exposedMelds, winningHands, bonusWinningHands);

    // Assert that ALL_IN_TRIPLETS is replaced by SELF_TRIPLETS
    then(result).containsExactly(WinningHandType.SELF_TRIPLETS)
        .doesNotContain(WinningHandType.ALL_IN_TRIPLETS);
  }

  @Test
  void testSelfPickWithoutAllInTriplets() {
    // Test case where ExposedMelds is empty, SelfPick is true, but does not contain ALL_IN_TRIPLETS
    WinningConditions conditions = new WinningConditions(false, false, false, false, false);
    ExposedMelds exposedMelds = new ExposedMelds(List.of(), List.of(), List.of()); // Empty melds
    List<WinningHandType> winningHands = List.of(WinningHandType.ALL_IN_TRIPLETS);
    List<WinningHandType> bonusWinningHands = List.of();

    List<WinningHandType> result = scoreCalculator.constructFinalWinningHands(conditions, exposedMelds, winningHands, bonusWinningHands);

    // Assert that COMMON_HAND is included, and no modification happens
    then(result).containsExactlyInAnyOrder(WinningHandType.ALL_IN_TRIPLETS);
  }

  @Test
  void testEmptyResultsAddsChickenHand() {
    // Test case where the result is empty and Chicken Hand is added
    WinningConditions conditions = new WinningConditions(false, false, false, false, false);
    ExposedMelds exposedMelds = new ExposedMelds(List.of(), List.of(), List.of()); // Empty melds
    List<WinningHandType> winningHands = List.of();
    List<WinningHandType> bonusWinningHands = List.of();

    List<WinningHandType> result = scoreCalculator.constructFinalWinningHands(conditions, exposedMelds, winningHands, bonusWinningHands);

    // Assert that CHICKEN_HAND is added
    then(result).containsExactlyInAnyOrder(WinningHandType.CHICKEN_HAND);
  }

  @Test
  void testRemoveWinFromWallIfGreatFlowersPresent() {
    // Test case where WIN_FROM_WALL is removed if GREAT_FLOWERS or FLOWER_HANDS is in the result
    WinningConditions conditions = new WinningConditions(false, false, false, false, false);
    ExposedMelds exposedMelds = new ExposedMelds(List.of(), List.of(), List.of()); // Empty melds
    List<WinningHandType> winningHands = List.of(WinningHandType.WIN_FROM_WALL);
    List<WinningHandType> bonusWinningHands = List.of(WinningHandType.GREAT_FLOWERS);

    List<WinningHandType> result = scoreCalculator.constructFinalWinningHands(conditions, exposedMelds, winningHands, bonusWinningHands);

    // Assert that WIN_FROM_WALL is removed when GREAT_FLOWERS is present
    then(result).containsExactly(WinningHandType.GREAT_FLOWERS)
        .doesNotContain(WinningHandType.WIN_FROM_WALL);
  }

  @Test
  void testBonusWinningHandsAdded() {
    // Test case where bonusWinningHands are added to the results
    WinningConditions conditions = new WinningConditions(false, false, false, false, false);
    ExposedMelds exposedMelds = new ExposedMelds(List.of(), List.of(), List.of()); // Empty melds
    List<WinningHandType> winningHands = List.of(WinningHandType.COMMON_HAND);
    List<WinningHandType> bonusWinningHands = List.of(WinningHandType.ALL_FLOWERS);

    List<WinningHandType> result = scoreCalculator.constructFinalWinningHands(conditions, exposedMelds, winningHands, bonusWinningHands);

    // Assert that ALL_FLOWERS is added to the result
    then(result).containsExactlyInAnyOrder(WinningHandType.COMMON_HAND, WinningHandType.ALL_FLOWERS);
  }

  @Test
  void testCalculateExtraWinningHandsCalled() {
    // Test case to ensure calculateExtraWinningHands is called
    WinningConditions conditions = new WinningConditions(false, false, false, false, true);
    ExposedMelds exposedMelds = new ExposedMelds(List.of(), List.of(), List.of(MahjongTileType.D1, MahjongTileType.D2)); // Empty melds
    List<WinningHandType> winningHands = List.of(WinningHandType.ALL_IN_TRIPLETS);
    List<WinningHandType> bonusWinningHands = List.of();

    given(winningConditionCalculator.calculateExtraWinningHands(conditions, winningHands, bonusWinningHands))
        .willReturn(List.of(WinningHandType.WIN_BY_DOUBLE_KONG));

    // You can mock the calculator call if necessary to check if the extra hands are added
    List<WinningHandType> result = scoreCalculator.constructFinalWinningHands(conditions, exposedMelds, winningHands, bonusWinningHands);

    // Check if the result contains extra hands as per calculateExtraWinningHands
    then(result).containsExactlyInAnyOrder(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.WIN_BY_DOUBLE_KONG);
  }
}
