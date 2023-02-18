package rpc.api;

import rpc.dto.User;

public interface UserService {

    User queryById(Long userId);

}
