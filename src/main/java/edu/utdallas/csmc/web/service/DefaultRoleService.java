package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DefaultRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    public Set<String> getUserRoles() {
        Set<String> userRoles = new HashSet<>();
        String username = defaultUsernameService.getUsername();
        if(!username.equals("")){
            List<Role> rolesList = roleRepository.findByUserUsername(username);
            for (Role role : rolesList) {
                if (!userRoles.contains(role.getName())) {
                    userRoles.add(role.getName());
                }
            }
        }
        return userRoles;
    }

    public Role getRoleByName(String name){
        return roleRepository.findByName(name).get();
    }
}
