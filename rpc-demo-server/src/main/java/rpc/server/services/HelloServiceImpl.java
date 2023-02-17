package rpc.server.services;

import lombok.extern.slf4j.Slf4j;
import rpc.api.HelloService;

@Slf4j
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String greeting) {
        log.info("sayHello api called. args: {}", greeting);
        return "Hello, you too";
    }
}
