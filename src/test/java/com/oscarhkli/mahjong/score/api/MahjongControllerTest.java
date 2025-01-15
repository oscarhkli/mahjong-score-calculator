package com.oscarhkli.mahjong.score.api;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscarhkli.mahjong.score.ExposedMelds;
import com.oscarhkli.mahjong.score.MahjongTileType;
import com.oscarhkli.mahjong.score.ScoreCalculator;
import com.oscarhkli.mahjong.score.WindType;
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
  @Captor ArgumentCaptor<ExposedMelds> exposedMeldsCaptor;
  @Captor ArgumentCaptor<List<MahjongTileType>> winningHandCaptor;
  @Captor ArgumentCaptor<WindType> windSettingsCaptor;

  @Test
  @SneakyThrows
  void testDeduceWinningHand() {
    var fakeWinningHand =
        new WinningHand(List.of(WinningHandType.COMMON_HAND, WinningHandType.ALL_ONE_SUIT));
    given(
            scoreCalculator.calculate(
                tilesCaptor.capture(),
                exposedMeldsCaptor.capture(),
                winningHandCaptor.capture(),
                windSettingsCaptor.capture()))
        .willReturn(fakeWinningHand);

    var request =
        WinningHandRequest.builder()
            .handTiles(
                List.of(
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
                    MahjongTileType.D9))
            .build();
    final var response =
        mockMvc
            .perform(
                post("/api/v1/mahjong/faans")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
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
    then(exposedMeldsCaptor.getValue())
        .extracting(ExposedMelds::getChows, ExposedMelds::getPongs, ExposedMelds::getKongs)
        .containsExactly(List.of(), List.of(), List.of());
  }

  @Test
  @SneakyThrows
  void testDeduceWinningHandWithExposedTiles() {
    var fakeWinningHand = new WinningHand(List.of(WinningHandType.ALL_ONE_SUIT));
    given(
            scoreCalculator.calculate(
                tilesCaptor.capture(),
                exposedMeldsCaptor.capture(),
                winningHandCaptor.capture(),
                windSettingsCaptor.capture()))
        .willReturn(fakeWinningHand);

    var request =
        WinningHandRequest.builder()
            .handTiles(List.of(MahjongTileType.D9, MahjongTileType.D9))
            .exposedMelds(
                new ExposedMelds(
                    List.of(MahjongTileType.D1, MahjongTileType.D3),
                    List.of(MahjongTileType.D5, MahjongTileType.D6),
                    List.of()))
            .build();
    final var response =
        mockMvc
            .perform(
                post("/api/v1/mahjong/faans")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
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
    then(exposedMeldsCaptor.getValue())
        .extracting(ExposedMelds::getChows, ExposedMelds::getPongs, ExposedMelds::getKongs)
        .containsExactly(
            List.of(MahjongTileType.D1, MahjongTileType.D3),
            List.of(MahjongTileType.D5, MahjongTileType.D6),
            List.of());
  }
}
