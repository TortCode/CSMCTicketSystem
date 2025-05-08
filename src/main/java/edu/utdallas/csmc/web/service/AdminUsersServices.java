package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminRoleResultDTO;
import edu.utdallas.csmc.web.dto.AdminSingleRoleDTO;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.RoleRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.*;

@Service
public class AdminUsersServices {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * This function fetches all roles from the system.
     */
    public AdminRoleResultDTO getRoleResult() {
        AdminRoleResultDTO adminRoleResultDTO = new AdminRoleResultDTO();
        adminRoleResultDTO.setRoles(new ArrayList<>());

        List<Role> allRoles = roleRepository.findAll();
        for (Role r : allRoles) {
            AdminSingleRoleDTO tmp = new AdminSingleRoleDTO();
            tmp.setName(r.getName());
            tmp.setDescription("");
            tmp.setId(r.getId().toString());
            adminRoleResultDTO.getRoles().add(tmp);
        }
        Collections.sort(adminRoleResultDTO.getRoles(), new Comparator<AdminSingleRoleDTO>() {
            @Override
            public int compare(AdminSingleRoleDTO o1, AdminSingleRoleDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return adminRoleResultDTO;
    }

    /**
     * This function creates a new role.
     */
    public String createRole(ModelMap model, String rolename, String description) {
        Optional<Role> roleWithSameName = roleRepository.findByName(rolename);
        if (roleWithSameName.isPresent()) {
            // already existing
            return "The role is existing!!!";
        }
        Optional<User> user = userRepository.findByUsername((String) model.getAttribute("user_services"));
        Role newR = new Role();

        newR.setName(rolename);

        newR.setCreatedOn(new Date());
        newR.setLastModifiedOn(new Date());

        newR.setLastModifiedBy(user.get());
        newR.setCreatedBy(user.get());

        roleRepository.save(newR);
        return "Successfully created a new role!!!";
    }

    /**
     * This function updates the information of a role.
     */
    public String editRole(ModelMap model, UUID roleID, String rolename, String description) {
        Optional<User> user = userRepository.findByUsername((String) model.getAttribute("user_services"));
        Optional<Role> role = roleRepository.findById(roleID);
        Optional<Role> roleWithSameName = roleRepository.findByName(rolename);
        if (role.isPresent()) {
            if (roleWithSameName.isPresent() && !roleWithSameName.get().getId().toString().equals(roleID.toString())) {
                // Already existing -> DO nothing;
                return "The role is existing!!!";
            } else {
                role.get().setName(rolename);
                role.get().setLastModifiedOn(new Date());
                role.get().setLastModifiedBy(user.get());
                roleRepository.save(role.get());
            }
        }
        return "Updated sucessfully!!!";
    }

    /**
     * This function fetches the role by its ID
     */
    public AdminSingleRoleDTO getRoleByID(UUID roleID) {
        AdminSingleRoleDTO adminSingleRoleDTO = new AdminSingleRoleDTO();
        Optional<Role> role = roleRepository.findById(roleID);
        if (role.isPresent()) {
            adminSingleRoleDTO.setName(role.get().getName());
            adminSingleRoleDTO.setId(role.get().getId().toString());
        }
        return adminSingleRoleDTO;
    }

    /**
     * This function removes a role by its ID.
     */
    public String removeRole(UUID roleID) {
        String message = "Can not remove!!!";

        Optional<Role> role = roleRepository.findById(roleID);

        if (role.isPresent()) {
            String rolename = role.get().getName();
            List<Role> roleAndUser = roleRepository.getUsersByRole(role.get().getName());
            if (roleAndUser.size() > 0) {
                message = "The User has associated data to the role <" + rolename + "> and cannot be deleted.";
            } else {
                roleRepository.delete(role.get());
                message = "Removed successfully the role <" + rolename + "> !!!";
            }
        } else {
            message = "Role does not exist";
        }
        return message;
    }

}
