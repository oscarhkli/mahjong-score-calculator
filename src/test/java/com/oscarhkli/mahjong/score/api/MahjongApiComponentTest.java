package com.oscarhkli.mahjong.score.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

  private String generateToken() {
    return jwtHelper.generateToken("MSC_USER", List.of("ROLE_USER"));
  }

  @Test
  @SneakyThrows
  void testDeduceWinningHandWithoutExposedTiles() {
    var request =
        """
        {
          "handTiles":["D1", "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D5", "D6", "D9", "D9"]
        }""";

    var response =
        mockMvc
            .perform(
                post("/api/v1/mahjong/faans")
                    .with(csrf())
                    .header("Authorization", "Bearer %s".formatted(generateToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(request))
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

  @Test
  @SneakyThrows
  void testDeduceWinningHandWithExposedTiles() {
    var request =
        """
        {
          "handTiles":["D9","D9"],
          "exposedMelds":{
            "chows": ["D1","D3"],
            "pongs": ["D5","D6"]
          }
        }""";

    var response =
        mockMvc
            .perform(
                post("/api/v1/mahjong/faans")
                    .with(csrf())
                    .header("Authorization", "Bearer %s".formatted(generateToken()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(request))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn()
            .getResponse()
            .getContentAsString();

    var expectedResponseJson =
        """
        {
           "data": {
             "totalFaans": 7,
             "winningHands": [
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
