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
          "handTiles":["D1", "D1", "D1", "D2", "D2", "D2", "D3", "D3", "D3", "D4", "D5", "D6", "D9", "D9"],
          "bonusTiles": [],
          "wind": {
            "prevailing": "EAST",
            "seat": "WEST"
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
             "totalFaans": 10,
             "winningHands": [
               {
                 "type": "COMMON_HAND",
                 "name": "Common Hand",
                 "faans": 1,
                 "limitHand": false
               },
               {
                 "type": "ALL_ONE_SUIT",
                 "name": "All One Suit",
                 "faans": 7,
                 "limitHand": false
               },
               {
                 "type": "WIN_FROM_WALL",
                 "name": "Win From Wall",
                 "faans": 1,
                 "limitHand": false
               },
               {
                 "type": "NO_FLOWERS",
                 "name": "No Flowers/Seasons",
                 "faans": 1,
                 "limitHand": false
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
          },
          "bonusTiles":[],
          "wind": {
            "prevailing": "EAST",
            "seat": "WEST"
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
             "totalFaans": 8,
             "winningHands": [
               {
                 "type": "ALL_ONE_SUIT",
                 "name": "All One Suit",
                 "faans": 7,
                 "limitHand": false
               },
               {
                "type": "NO_FLOWERS",
                "name": "No Flowers/Seasons",
                "faans": 1,
                 "limitHand": false
               }
             ]
           }
         }""";

    JSONAssert.assertEquals(expectedResponseJson, response, true);
  }

  @Test
  @SneakyThrows
  void testDeduceWinningHandWithExposedTilesAndBonusTiles() {
    var request =
        """
        {
          "handTiles":["D9","D9"],
          "exposedMelds":{
            "chows": ["D1","D3"],
            "pongs": ["D5","D6"]
          },
          "bonusTiles":["F1","F2","F3","F4","S1","S2","S3"],
          "wind": {
            "prevailing": "EAST",
            "seat": "WEST"
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
             "totalFaans": 10,
             "winningHands": [
               {
                 "type": "ALL_ONE_SUIT",
                 "name": "All One Suit",
                 "faans": 7,
                 "limitHand": false
               },
               {
                "type": "FLOWER_HANDS",
                "name": "Flower Hands",
                "faans": 3,
                 "limitHand": false
               }
             ]
           }
         }""";

    JSONAssert.assertEquals(expectedResponseJson, response, true);
  }

  @Test
  @SneakyThrows
  void testDeduceSpecialWinningHand() {
    var request =
        """
        {
          "handTiles":["D1","D1","D1","D2","D3","D4","D5","D6","D7","D8","D9","D9","D9","D9"],
          "exposedMelds":{
            "chows": [],
            "pongs": []
          },
          "bonusTiles":["F1","F2","F3","F4","S1","S2","S3"],
          "wind": {
            "prevailing": "EAST",
            "seat": "WEST"
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
             "totalFaans": 13,
             "winningHands": [
               {
                 "type": "NINE_GATES",
                 "name": "Nine Gates",
                 "faans": 10,
                 "limitHand": true
               },
               {
                 "type": "FLOWER_HANDS",
                 "name": "Flower Hands",
                 "faans": 3,
                 "limitHand": false
               }
             ]
           }
         }""";

    JSONAssert.assertEquals(expectedResponseJson, response, true);
  }

  @Test
  @SneakyThrows
  void testDeduceWinningHandByBonusTiles() {
    var request =
        """
        {
          "handTiles":["D7","D8","B5","B6","C1","C2","C3","C4","C5","WEST","EAST","RED","GREEN","GREEN"],
          "exposedMelds":{
            "chows": [],
            "pongs": []
          },
          "bonusTiles":["F1","F2","F3","F4","S1","S2","S3"],
          "wind": {
            "prevailing": "EAST",
            "seat": "WEST"
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
             "totalFaans": 3,
             "winningHands": [
               {
                 "type": "FLOWER_HANDS",
                 "name": "Flower Hands",
                 "faans": 3,
                 "limitHand": false
               },
               {
                 "type": "SELF_PICK_WITHOUT_EXTRA_FAAN",
                 "name": "Self Pick Without Extra Faan Hand",
                 "faans": 0,
                 "limitHand": false
               }
             ]
           }
         }""";

    JSONAssert.assertEquals(expectedResponseJson, response, true);
  }



}
