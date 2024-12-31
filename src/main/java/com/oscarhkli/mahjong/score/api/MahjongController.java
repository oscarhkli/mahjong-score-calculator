package com.oscarhkli.mahjong.score.api;

import com.oscarhkli.mahjong.score.MahjongTileType;
import com.oscarhkli.mahjong.score.ScoreCalculator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class MahjongController {

  private final ScoreCalculator scoreCalculator;

  @GetMapping(path = "/api/v1/mahjong/faans", produces = "application/json")
  public ResponseEntity<WinningHandResponse> deduceWinningHand(
      @RequestHeader HttpHeaders headers,
      @RequestParam List<MahjongTileType> handTiles,
      @RequestParam(required = false, defaultValue = "") List<MahjongTileType> exposedChows,
      @RequestParam(required = false, defaultValue = "") List<MahjongTileType> exposedPongs,
      @RequestParam(required = false, defaultValue = "") List<MahjongTileType> exposedKongs) {
    log.info(
        "deduceWinningHand handTiles: {}, exposedChows: {}, exposedPongs: {}, exposedKongs: {} [referer: {}, user-agent: {}]",
        handTiles,
        exposedChows,
        exposedPongs,
        exposedKongs,
        headers.getOrEmpty(HttpHeaders.REFERER),
        headers.getOrEmpty(HttpHeaders.USER_AGENT));
    var winningHandResponse =
        WinningHandResponse.of(
            this.scoreCalculator.calculate(handTiles, exposedChows, exposedPongs, exposedKongs));
    log.info(
        "Return WinningHandResponse with totalFaans: {}",
        winningHandResponse.getData().getTotalFaans());
    return ResponseEntity.ok(winningHandResponse);
  }
}
