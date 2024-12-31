package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MeldsFactoryTest {
  MeldsFactory meldsFactory = new MeldsFactory();

  @ParameterizedTest
  @MethodSource
  void constructMelds(
      MahjongSetType mahjongSetType,
      List<MahjongTileType> mahjongTileTypes,
      List<MahjongTileType> exposedChows,
      List<MahjongTileType> exposedPongs,
      List<MahjongTileType> exposedKongs,
      List<Melds> expected) {
    var mahjongTiles = new int[42];
    mahjongTileTypes.stream()
        .map(MahjongTileType::getIndex)
        .forEach(index -> mahjongTiles[index]++);

    var melds =
        meldsFactory.construct(
            mahjongSetType, mahjongTiles, exposedChows, exposedPongs, exposedKongs);
    then(melds)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> constructMelds() {
    return Stream.of(
        Arguments.of(
            MahjongSetType.DOT,
            List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1),
                    List.of(),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1),
                    List.of(),
                    List.of(),
                    null,
                    new int[] {0, 1, 0, 1, 0, 0, 0, 0, 0, 0},
                    2,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D4,
                MahjongTileType.D5,
                MahjongTileType.D5,
                MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        MahjongTileType.D1,
                        MahjongTileType.D1,
                        MahjongTileType.D2,
                        MahjongTileType.D3),
                    List.of(),
                    List.of(),
                    MahjongTileType.D5,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D4,
                MahjongTileType.D5,
                MahjongTileType.D5,
                MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1, MahjongTileType.D1, MahjongTileType.D2),
                    List.of(MahjongTileType.D5),
                    List.of(),
                    null,
                    new int[] {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                    1,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1, MahjongTileType.D3),
                    List.of(),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D5,
                MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1, MahjongTileType.D3),
                    List.of(),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                    1,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D5,
                MahjongTileType.D5,
                MahjongTileType.D5,
                MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1, MahjongTileType.D3),
                    List.of(MahjongTileType.D5),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D5,
                MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        MahjongTileType.D1,
                        MahjongTileType.D1,
                        MahjongTileType.D2,
                        MahjongTileType.D2),
                    List.of(),
                    List.of(),
                    MahjongTileType.D5,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0),
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        MahjongTileType.D1,
                        MahjongTileType.D1,
                        MahjongTileType.D3,
                        MahjongTileType.D3),
                    List.of(),
                    List.of(),
                    MahjongTileType.D2,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(MahjongTileType.D1, MahjongTileType.D3, MahjongTileType.D5),
            List.of(),
            List.of(),
            List.of(),
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
            MahjongSetType.DOT,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
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
            MahjongSetType.BAMBOO,
            List.of(MahjongTileType.B1, MahjongTileType.B1, MahjongTileType.B1),
            List.of(),
            List.of(),
            List.of(),
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
            MahjongSetType.WIND,
            List.of(
                MahjongTileType.SOUTH,
                MahjongTileType.SOUTH,
                MahjongTileType.SOUTH,
                MahjongTileType.EAST,
                MahjongTileType.EAST,
                MahjongTileType.EAST,
                MahjongTileType.NORTH,
                MahjongTileType.NORTH),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.WIND,
                    List.of(),
                    List.of(MahjongTileType.EAST, MahjongTileType.SOUTH),
                    List.of(),
                    MahjongTileType.NORTH,
                    new int[5],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(MahjongTileType.D2, MahjongTileType.D3, MahjongTileType.D4),
            List.of(MahjongTileType.D1),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1, MahjongTileType.D2),
                    List.of(),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(MahjongTileType.D5, MahjongTileType.D5, MahjongTileType.D5),
            List.of(),
            List.of(MahjongTileType.D1),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(),
                    List.of(MahjongTileType.D1, MahjongTileType.D5),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.BAMBOO,
            List.of(MahjongTileType.B2, MahjongTileType.B3, MahjongTileType.B4),
            List.of(),
            List.of(),
            List.of(MahjongTileType.B1),
            List.of(
                new Melds(
                    MahjongSetType.BAMBOO,
                    List.of(MahjongTileType.B2),
                    List.of(),
                    List.of(MahjongTileType.B1),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(MahjongTileType.D7, MahjongTileType.D8, MahjongTileType.D9),
            List.of(MahjongTileType.D4),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D4, MahjongTileType.D7),
                    List.of(),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.CHARACTER,
            List.of(),
            List.of(MahjongTileType.C1),
            List.of(MahjongTileType.C4),
            List.of(MahjongTileType.C5),
            List.of(
                new Melds(
                    MahjongSetType.CHARACTER,
                    List.of(MahjongTileType.C1),
                    List.of(MahjongTileType.C4),
                    List.of(MahjongTileType.C5),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.BAMBOO,
            List.of(MahjongTileType.B7),
            List.of(MahjongTileType.B1),
            List.of(MahjongTileType.B4),
            List.of(MahjongTileType.B5),
            List.of(
                new Melds(
                    MahjongSetType.BAMBOO,
                    List.of(MahjongTileType.B1),
                    List.of(MahjongTileType.B4),
                    List.of(MahjongTileType.B5),
                    null,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                    1,
                    0))),
        Arguments.of(
            MahjongSetType.WIND,
            List.of(MahjongTileType.EAST),
            List.of(),
            List.of(MahjongTileType.SOUTH, MahjongTileType.WEST),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.WIND,
                    List.of(),
                    List.of(MahjongTileType.SOUTH, MahjongTileType.WEST),
                    List.of(),
                    null,
                    new int[] {0, 1, 0, 0, 0},
                    1,
                    0))),
        Arguments.of(
            MahjongSetType.WIND,
            List.of(MahjongTileType.EAST, MahjongTileType.EAST),
            List.of(),
            List.of(MahjongTileType.SOUTH, MahjongTileType.WEST),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.WIND,
                    List.of(),
                    List.of(MahjongTileType.SOUTH, MahjongTileType.WEST),
                    List.of(),
                    MahjongTileType.EAST,
                    new int[5],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(MahjongTileType.D3),
            List.of(MahjongTileType.D1),
            List.of(),
            List.of(MahjongTileType.D4),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1),
                    List.of(),
                    List.of(MahjongTileType.D4),
                    null,
                    new int[] {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                    1,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(),
            List.of(MahjongTileType.D1, MahjongTileType.B4),
            List.of(MahjongTileType.B1),
            List.of(MahjongTileType.WEST),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D1),
                    List.of(),
                    List.of(),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(),
            List.of(MahjongTileType.D5),
            List.of(MahjongTileType.D8),
            List.of(MahjongTileType.D9),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(MahjongTileType.D5),
                    List.of(MahjongTileType.D8),
                    List.of(MahjongTileType.D9),
                    null,
                    new int[10],
                    0,
                    0))),
        Arguments.of(
            MahjongSetType.DOT,
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D5,
                MahjongTileType.D5),
            List.of(MahjongTileType.D2),
            List.of(),
            List.of(),
            List.of(
                new Melds(
                    MahjongSetType.DOT,
                    List.of(
                        MahjongTileType.D1,
                        MahjongTileType.D1,
                        MahjongTileType.D2,
                        MahjongTileType.D2),
                    List.of(),
                    List.of(),
                    MahjongTileType.D5,
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    0,
                    0))));
  }
}
