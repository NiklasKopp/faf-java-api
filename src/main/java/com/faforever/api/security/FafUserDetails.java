package com.faforever.api.security;

import com.faforever.api.data.domain.BanInfo;
import com.faforever.api.data.domain.User;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class FafUserDetails extends org.springframework.security.core.userdetails.User {

  private final int id;

  public FafUserDetails(User user) {
    this(user.getId(), user.getLogin(), user.getPassword(), isNonLocked(user.getBanInfo()), getRoles(user));
  }

  public FafUserDetails(int id, String username, String password, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, true, true, true, accountNonLocked, authorities);
    this.id = id;
  }

  private static boolean isNonLocked(BanInfo banInfo) {
    return banInfo == null
        || banInfo.getExpiresAt().isBefore(OffsetDateTime.now());
  }

  @NotNull
  private static List<GrantedAuthority> getRoles(User user) {
    ArrayList<GrantedAuthority> roles = new ArrayList<>();
    roles.add(new SimpleGrantedAuthority("ROLE_USER"));

    if (user.getUserGroup() != null && user.getUserGroup().getGroup() == 1) {
      roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    return roles;
  }
}
