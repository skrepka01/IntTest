package com.itm.space.backendresources.service;

import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.api.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

  public interface UserService {

    void createUser(UserRequest userRequest);

    UserResponse getUserById(UUID id);

}
