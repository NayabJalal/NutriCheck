package com.nutricheck.service;

import com.nutricheck.dto.UserRequest;
import com.nutricheck.dto.UserResponse;
import com.nutricheck.entity.User;
import com.nutricheck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserResponse createNewUser(UserRequest request) {
        User user = modelMapper.map(request, User.class);
        user = userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }
}
