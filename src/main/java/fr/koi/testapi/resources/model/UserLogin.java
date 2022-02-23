package fr.koi.testapi.resources.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserLogin {
    private String login;
    private String password;
}
