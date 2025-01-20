package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class BonusWinningConditionCalculator {

  public List<WinningHandType> calculateBonusWinningHands(
      Melds windMelds,
      Melds dragonMelds,
      List<MahjongTileType> bonusTiles,
      WindType windSettings,
      ExposedMelds exposedMelds) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    if (exposedMelds.isEmpty()) {
      bonusWinningHands.add(WinningHandType.WIN_FROM_WALL);
    }
    bonusWinningHands.addAll(calculateWindTilesWinningConditions(windMelds, windSettings));
    bonusWinningHands.addAll(calculateDragonTilesWinningConditions(dragonMelds));
    bonusWinningHands.addAll(calculateBonusTilesWinningConditions(bonusTiles, windSettings));
    return bonusWinningHands;
  }

  private List<WinningHandType> calculateWindTilesWinningConditions(
      Melds windMelds, WindType windSettings) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    if (windMelds.getPongs().contains(windSettings.prevailing())
        || windMelds.getKongs().contains(windSettings.prevailing())) {
      bonusWinningHands.add(WinningHandType.PREVAILING_WIND);
    }
    if (windMelds.getPongs().contains(windSettings.seat())
        || windMelds.getKongs().contains(windSettings.seat())) {
      bonusWinningHands.add(WinningHandType.SEAT_WIND);
    }
    return bonusWinningHands;
  }

  private List<WinningHandType> calculateDragonTilesWinningConditions(Melds dragonMelds) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    for (var i = 0; i < dragonMelds.getPongKongSize(); i++) {
      bonusWinningHands.add(WinningHandType.ONE_DRAGON);
    }
    return bonusWinningHands;
  }

  private List<WinningHandType> calculateBonusTilesWinningConditions(
      List<MahjongTileType> bonusTiles, WindType windSettings) {
    var bonusWinningHands = new ArrayList<WinningHandType>();
    var bonusTileSet = new HashSet<>(bonusTiles);
    var hasAllFlowers =
        Stream.of(MahjongTileType.F1, MahjongTileType.F2, MahjongTileType.F3, MahjongTileType.F4)
            .allMatch(bonusTileSet::contains);
    var hasAllSeasons =
        Stream.of(MahjongTileType.S1, MahjongTileType.S2, MahjongTileType.S3, MahjongTileType.S4)
            .allMatch(bonusTileSet::contains);
    if (bonusTiles.size() == 8) {
      bonusWinningHands.add(WinningHandType.GREAT_FLOWERS);
    } else if (bonusTiles.size() == 7 && (hasAllFlowers || hasAllSeasons)) {
      bonusWinningHands.add(WinningHandType.FLOWER_HANDS);
    } else if (bonusTiles.isEmpty()) {
      bonusWinningHands.add(WinningHandType.NO_FLOWERS);
    } else {
      var windIndex =
          windSettings.seat().getIndex() - MahjongSetType.WIND.getStartingTile().getIndex();
      if (hasAllFlowers) {
        bonusWinningHands.add(WinningHandType.ALL_FLOWERS);
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.SEASON)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
      } else if (hasAllSeasons) {
        bonusWinningHands.add(WinningHandType.ALL_FLOWERS);
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.FLOWER)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
      } else {
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.FLOWER)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
        if (isFlowerOfOwnWind(bonusTiles, windIndex, MahjongSetType.SEASON)) {
          bonusWinningHands.add(WinningHandType.FLOWER_OF_OWN_WIND);
        }
      }
    }
    return bonusWinningHands;
  }

  private boolean isFlowerOfOwnWind(
      List<MahjongTileType> bonusTiles, int windIndex, MahjongSetType mahjongSetType) {
    return bonusTiles.stream()
        .map(MahjongTileType::getIndex)
        .map(bonusTileIndex -> bonusTileIndex - mahjongSetType.getStartingTile().getIndex())
        .anyMatch(bonusWindIndex -> bonusWindIndex == windIndex);
  }

}
