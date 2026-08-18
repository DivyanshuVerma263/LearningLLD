package model;

import enums.UserTier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class User {
    private String userId;
    private UserTier tier;
}
