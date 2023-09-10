package com.laudog;

import com.laudog.domain.user.UserDto;
import com.laudog.domain.user.UserService;
import com.laudog.security.AuthUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

@SpringBootTest
public class TestUserService {

    @Autowired
    UserService userService;

    @Test
    public void testUserCreationAndUpdate() {
        UserDto.Registration reg = UserDto.Registration.builder()
            .username("lauren4t")
            .password("fouf")
            .email("lolo4@lo.com")
            .roles(List.of("ROLE_ADMIN","ROLE_USER"))
            .build();
        UserDto u = userService.registration(reg).block();

        AuthUserDetails details = AuthUserDetails.builder().userDto(u).build();
        UserDto.Update update = UserDto.Update.builder()
            .username("lauren4t")
            .build();
        userService.update(update, details).block();
    }

}
