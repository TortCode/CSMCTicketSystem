package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.RoleRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Service
public class SSORoleService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public Optional<User> getUserByUsername (String username) {
        return userRepository.findByUsername(username);
    }

    public void addUserWithUsername(User user) {
        userRepository.save(user);
    }
}
