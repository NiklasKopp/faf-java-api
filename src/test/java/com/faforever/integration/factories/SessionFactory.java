package com.faforever.integration.factories;

import com.faforever.api.client.ClientType;
import com.faforever.api.client.OAuthClient;
import com.faforever.api.data.domain.Player;
import com.faforever.api.data.domain.User;
import com.faforever.integration.TestDatabase;
import com.faforever.integration.utils.MockMvcHelper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;

import static junitx.framework.ComparableAssert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SessionFactory {

  private static final String OAUTH_CLIENT_ID = "1234";
  private static final String OAUTH_SECRET = "secret";

  private static final ShaPasswordEncoder shaPasswordEncoder = new ShaPasswordEncoder(256);

  @SneakyThrows
  public static Session createUserAndGetAccessToken(TestDatabase database,
                                                    MockMvc mvc) {
    return createUserAndGetAccessToken("JUnitTestUser_SessionFactory", "myCoolPassword",
        database, mvc);
  }

  @SneakyThrows
  public static Session createUserAndGetAccessToken(String login,
                                                    String password,
                                                    TestDatabase database,
                                                    MockMvc mvc) {
    OAuthClient client = new OAuthClient()
      .setId(OAUTH_CLIENT_ID)
      .setName("test")
      .setClientSecret(OAUTH_SECRET)
      .setRedirectUris("test")
      .setDefaultRedirectUri("test")
      .setDefaultScope("test")
      .setClientType(ClientType.PUBLIC);
    database.getOAuthClientRepository().save(client);

    long userCounter = database.getUserRepository().count();
    User user = (User) new User()
      .setPassword(shaPasswordEncoder.encodePassword(password, null))
      .setLogin(login)
      .setEmail(login + "@example.com");
    database.getUserRepository().save(user);
    assertEquals((userCounter + 1), database.getUserRepository().count());

    Player player = database.getPlayerRepository().findOne(user.getId());
    Assert.assertNotNull(player);

    String authorization = "Basic "
      + new String(Base64Utils.encode((OAUTH_CLIENT_ID + ":" + OAUTH_SECRET).getBytes()));
    ResultActions auth = MockMvcHelper.of(mvc).perform(
      post("/oauth/token")
        .header("Authorization", authorization)
        .param("username", login)
        .param("password", password)
        .param("grant_type", "password"));
    auth.andExpect(status().isOk());
    JsonNode node = database.getObjectMapper().readTree(auth.andReturn().getResponse().getContentAsString());
    String token = node.get("access_token").asText();
    Assert.assertNotEquals("", token);
    return new Session().setPlayer(player).setToken("Bearer " + token);
  }


  @Data
  public static class Session {
    private Player player;
    private String Token;
  }
}
