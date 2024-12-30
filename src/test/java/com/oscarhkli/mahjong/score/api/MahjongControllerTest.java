package com.oscarhkli.mahjong.score.api;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhkli.mahjong.score.MahjongTileType;
import com.oscarhkli.mahjong.score.ScoreCalculator;
import com.oscarhkli.mahjong.score.WinningHand;
import com.oscarhkli.mahjong.score.WinningHandType;
import com.oscarhkli.mahjong.score.api.WinningHandResponse.BreakDown;
import com.oscarhkli.mahjong.score.api.WinningHandResponse.WinningHandDescription;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MahjongController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser
class MahjongControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockitoBean ScoreCalculator scoreCalculator;

  @Captor ArgumentCaptor<List<MahjongTileType>> tilesCaptor;
  @Captor ArgumentCaptor<List<List<MahjongTileType>>> exposedChowsCaptor;
  @Captor ArgumentCaptor<List<MahjongTileType>> exposedPongsCaptor;
  @Captor ArgumentCaptor<List<MahjongTileType>> exposedKongsCaptor;

  @Test
  @SneakyThrows
  void testDeduceWinningHand() {
    var fakeWinningHand =
        new WinningHand(List.of(WinningHandType.COMMON_HAND, WinningHandType.ALL_ONE_SUIT));
    given(
            scoreCalculator.calculate(
                tilesCaptor.capture(),
                exposedChowsCaptor.capture(),
                exposedPongsCaptor.capture(),
                exposedKongsCaptor.capture()))
        .willReturn(fakeWinningHand);

    final var response =
        mockMvc
            .perform(
                get("/api/v1/mahjong/faans")
                    .secure(true)
                    .accept(MediaType.APPLICATION_JSON)
                    .queryParam(
                        "handTiles",
                        "D1",
                        "D1",
                        "D1",
                        "D2",
                        "D2",
                        "D2",
                        "D3",
                        "D3",
                        "D3",
                        "D4",
                        "D5",
                        "D6",
                        "D9",
                        "D9"))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final var winningHandResponse = objectMapper.readValue(response, WinningHandResponse.class);

    var expectedWinningResponse =
        WinningHandResponse.builder()
            .data(
                BreakDown.builder()
                    .totalFaans(8)
                    .winningHands(
                        List.of(
                            WinningHandDescription.builder()
                                .type(WinningHandType.COMMON_HAND)
                                .name("Common Hand")
                                .faans(1)
                                .build(),
                            WinningHandDescription.builder()
                                .type(WinningHandType.ALL_ONE_SUIT)
                                .name("All One Suit")
                                .faans(7)
                                .build()))
                    .build())
            .build();

    then(winningHandResponse).usingRecursiveComparison().isEqualTo(expectedWinningResponse);
    then(tilesCaptor.getValue())
        .containsExactly(
            MahjongTileType.D1,
            MahjongTileType.D1,
            MahjongTileType.D1,
            MahjongTileType.D2,
            MahjongTileType.D2,
            MahjongTileType.D2,
            MahjongTileType.D3,
            MahjongTileType.D3,
            MahjongTileType.D3,
            MahjongTileType.D4,
            MahjongTileType.D5,
            MahjongTileType.D6,
            MahjongTileType.D9,
            MahjongTileType.D9);
    then(exposedChowsCaptor.getValue()).isEmpty();
    then(exposedPongsCaptor.getValue()).isEmpty();
    then(exposedKongsCaptor.getValue()).isEmpty();
  }

  @Test
  @SneakyThrows
  void testDeduceWinningHandWithExposedTiles() {
    var fakeWinningHand = new WinningHand(List.of(WinningHandType.ALL_ONE_SUIT));
    given(
            scoreCalculator.calculate(
                tilesCaptor.capture(),
                exposedChowsCaptor.capture(),
                exposedPongsCaptor.capture(),
                exposedKongsCaptor.capture()))
        .willReturn(fakeWinningHand);

    final var response =
        mockMvc
            .perform(
                get("/api/v1/mahjong/faans")
                    .secure(true)
                    .accept(MediaType.APPLICATION_JSON)
                    .queryParam("handTiles", "D9", "D9")
                    .queryParam("exposedChows", "D1,D2,D3", "D3,D4,D5")
                    .queryParam("exposedPongs", "D5", "D6"))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();
    final var winningHandResponse = objectMapper.readValue(response, WinningHandResponse.class);

    var expectedWinningResponse =
        WinningHandResponse.builder()
            .data(
                BreakDown.builder()
                    .totalFaans(7)
                    .winningHands(
                        List.of(
                            WinningHandDescription.builder()
                                .type(WinningHandType.ALL_ONE_SUIT)
                                .name("All One Suit")
                                .faans(7)
                                .build()))
                    .build())
            .build();

    then(winningHandResponse).usingRecursiveComparison().isEqualTo(expectedWinningResponse);
    then(tilesCaptor.getValue()).containsExactly(MahjongTileType.D9, MahjongTileType.D9);
    then(exposedChowsCaptor.getValue())
        .containsExactly(
            List.of(MahjongTileType.D1, MahjongTileType.D2, MahjongTileType.D3),
            List.of(MahjongTileType.D3, MahjongTileType.D4, MahjongTileType.D5));
    then(exposedPongsCaptor.getValue()).containsExactly(MahjongTileType.D5, MahjongTileType.D6);
    then(exposedKongsCaptor.getValue()).isEmpty();
  }
}
