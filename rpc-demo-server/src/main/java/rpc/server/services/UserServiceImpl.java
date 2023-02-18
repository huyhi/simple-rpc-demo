package rpc.server.services;

import lombok.extern.slf4j.Slf4j;
import rpc.api.UserService;
import rpc.dto.User;

import java.util.Random;

@Slf4j
public class UserServiceImpl implements UserService {

    @Override
    public User queryById(Long userId) {
        // do some query... ...
        Random r = new Random();

        return User.builder()
                .userId(userId)
                .userName(String.format("%s", r.nextInt(1000)))
                .phoneNumber(String.format("%s%s%s", r.nextInt(1000), r.nextInt(1000), r.nextInt(1000)))
                .build();
    }
}
