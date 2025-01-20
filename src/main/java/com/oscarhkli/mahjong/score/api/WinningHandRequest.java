package com.oscarhkli.mahjong.score.api;

import com.oscarhkli.mahjong.score.ExposedMelds;
import com.oscarhkli.mahjong.score.MahjongTileType;
import com.oscarhkli.mahjong.score.WindType;
import com.oscarhkli.mahjong.score.WinningConditions;
import java.util.List;
import lombok.Builder;

@Builder
public record WinningHandRequest(
    List<MahjongTileType> handTiles,
    ExposedMelds exposedMelds,
    List<MahjongTileType> bonusTiles,
    WindType wind,
    WinningConditions winningConditions) {}
