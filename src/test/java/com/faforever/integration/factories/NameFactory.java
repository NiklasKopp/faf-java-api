package com.faforever.integration.factories;

import com.faforever.api.data.domain.Name;
import com.faforever.api.data.domain.Player;
import com.faforever.integration.TestDatabase;
import lombok.Builder;

import java.time.OffsetDateTime;

import static org.junit.Assert.assertEquals;

public class NameFactory {

  @Builder
  private static Name create(OffsetDateTime changeTime, String name, Player player, TestDatabase database) {
    Name entity = new Name()
      .setChangeTime(changeTime != null ? changeTime : OffsetDateTime.now())
      .setName(name)
      .setPlayer(player);
    long count = database.getNameRepository().count();
    database.getNameRepository().save(entity);
    assertEquals(count + 1, database.getNameRepository().count());
    return entity;
  }
}
