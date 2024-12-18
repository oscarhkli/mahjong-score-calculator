package com.oscarhkli.mahjong.score.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.oscarhkli.mahjong.score.security.JwtHelper;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@Slf4j
class MahjongApiComponentTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtHelper jwtHelper;

  @Test
  @SneakyThrows
  void testDeduceWinningHand() {
    var token = jwtHelper.generateToken("MSC_USER", List.of("ROLE_USER"));

    var response =
        mockMvc
            .perform(
                get("/api/v1/mahjong/faans")
                    .secure(true)
                    .header("Authorization", "Bearer " + token)
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

    var expectedResponseJson =
        """
        {
           "data": {
             "totalFaans": 8,
             "winningHands": [
               {
                 "type": "COMMON_HAND",
                 "name": "Common Hand",
                 "faans": 1
               },
               {
                 "type": "ALL_ONE_SUIT",
                 "name": "All One Suit",
                 "faans": 7
               }
             ]
           }
         }""";

    JSONAssert.assertEquals(expectedResponseJson, response, true);
  }
}
