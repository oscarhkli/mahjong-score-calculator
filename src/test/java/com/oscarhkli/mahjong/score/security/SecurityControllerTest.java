package com.oscarhkli.mahjong.score.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oscarhkli.mahjong.score.ScoreCalculator;
import com.oscarhkli.mahjong.score.api.MahjongController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MahjongController.class)
@AutoConfigureMockMvc
class SecurityControllerTest {

  @Autowired MockMvc mockMvc;
  @MockitoBean ScoreCalculator scoreCalculator;

  @Test
  @SneakyThrows
  void testGetFaansWithoutAuthentication() {
    mockMvc.perform(get("/api/v1/mahjong/faans")).andExpect(status().isUnauthorized());
  }
}
