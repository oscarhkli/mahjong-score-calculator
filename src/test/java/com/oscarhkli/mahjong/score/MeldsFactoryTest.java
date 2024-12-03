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
      List<MahjongTileType> mahjongTileTypes, MahjongSetType mahjongSetType, List<Melds> expected) {
    var mahjongTiles = new int[42];
    mahjongTileTypes.stream()
        .map(MahjongTileType::getIndex)
        .forEach(index -> mahjongTiles[index]++);

    var melds = meldsFactory.construct(mahjongSetType, mahjongTiles);
    then(melds)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> constructMelds() {
    return Stream.of(
        Arguments.of(
            List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
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
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3),
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
            List.of(
                MahjongTileType.D1,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D2,
                MahjongTileType.D3,
                MahjongTileType.D3,
                MahjongTileType.D4,
                MahjongTileType.D5),
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
            List.of(MahjongTileType.D1, MahjongTileType.D3, MahjongTileType.D5),
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
            List.of(MahjongTileType.B1, MahjongTileType.B1, MahjongTileType.B1),
            MahjongSetType.BAMBOO,
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
            List.of(
                MahjongTileType.SOUTH,
                MahjongTileType.SOUTH,
                MahjongTileType.SOUTH,
                MahjongTileType.EAST,
                MahjongTileType.EAST,
                MahjongTileType.EAST,
                MahjongTileType.NORTH,
                MahjongTileType.NORTH),
            MahjongSetType.WIND,
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
}
