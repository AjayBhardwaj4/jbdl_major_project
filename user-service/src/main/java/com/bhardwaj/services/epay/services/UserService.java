package com.bhardwaj.services.epay.services;

import com.bhardwaj.services.epay.models.User;
import com.bhardwaj.services.epay.models.requests.UserCreateRequest;
import com.bhardwaj.services.epay.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.bhardwaj.services.epay.constants.CommonConstants.*;
import static com.bhardwaj.services.epay.constants.UserConstants.USER_AUTHORITY;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public User loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public void create(UserCreateRequest userCreateRequest) throws JsonProcessingException {
        User user = userCreateRequest.toUser();
        user.setPassword(encryptPassword(user.getPassword()));
        user.setAuthorities(USER_AUTHORITY);
        userRepository.save(user);


        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USER_CREATION_TOPIC_USERID, user.getId());
        jsonObject.put(USER_CREATION_TOPIC_PHONE_NUMBER, user.getPhoneNumber());
        jsonObject.put(USER_CREATION_TOPIC_IDENTIFIER_VALUE, user.getIdentifierValue());
        jsonObject.put(USER_CREATION_TOPIC_IDENTIFIER_KEY, user.getUserIdentifier());

        kafkaTemplate.send(USER_CREATION_TOPIC, objectMapper.writeValueAsString(jsonObject));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
