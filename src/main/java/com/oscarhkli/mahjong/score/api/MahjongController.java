package com.oscarhkli.mahjong.score.api;

import com.oscarhkli.mahjong.score.ExposedMelds;
import com.oscarhkli.mahjong.score.ScoreCalculator;
import com.oscarhkli.mahjong.score.WinningConditions;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
public class MahjongController {

  private final ScoreCalculator scoreCalculator;

  @PostMapping(
      path = "/api/v1/mahjong/faans",
      consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<WinningHandResponse> deduceWinningHand(
      @RequestHeader HttpHeaders headers, @RequestBody WinningHandRequest request) {
    log.info(
        "deduceWinningHand request: {}, [referer: {}, user-agent: {}]",
        request,
        headers.getOrEmpty(HttpHeaders.REFERER),
        headers.getOrEmpty(HttpHeaders.USER_AGENT));
    var winningHandResponse =
        WinningHandResponse.of(
            this.scoreCalculator.calculate(
                request.handTiles(),
                Optional.ofNullable(request.exposedMelds()).orElseGet(ExposedMelds::new),
                request.bonusTiles(),
                request.wind(),
                Optional.ofNullable(request.winningConditions())
                    .orElseGet(WinningConditions::new)));
    log.info(
        "Return WinningHandResponse with totalFaans: {}",
        winningHandResponse.getData().getTotalFaans());
    return ResponseEntity.ok(winningHandResponse);
  }
}
