package com.oscarhkli.mahjong.score;

public record WinningConditions(
    boolean isSelfPick,
    boolean isWinByLastCatch,
    boolean isRobbingKong,
    boolean isWinByKong,
    boolean isWinByDoubleKong) {

  public WinningConditions() {
    this(false, false, false, false, false);
  }
}
