package com.faforever.api.data.checks;

import com.faforever.api.data.domain.ClanMembership;
import com.faforever.api.security.FafUserDetails;
import com.yahoo.elide.security.ChangeSpec;
import com.yahoo.elide.security.RequestScope;
import com.yahoo.elide.security.checks.InlineCheck;

import java.util.Optional;

public class IsClanMembershipDeletable {
  public static final String EXPRESSION = "IsClanMembershipDeletable";
  
  public static class Inline extends InlineCheck<ClanMembership> {

    @Override
    public boolean ok(ClanMembership membership, RequestScope requestScope, Optional<ChangeSpec> changeSpec) {
      Object opaqueUser = requestScope.getUser().getOpaqueUser();
      int requesterId = ((FafUserDetails) opaqueUser).getId();
      return opaqueUser instanceof FafUserDetails
          && membership.getPlayer().getId() != membership.getClan().getLeader().getId()
          && (membership.getClan().getLeader().getId() == requesterId ||
          membership.getPlayer().getId() == requesterId);
    }

    @Override
    public boolean ok(com.yahoo.elide.security.User user) {
      return false;
    }
  }
}
