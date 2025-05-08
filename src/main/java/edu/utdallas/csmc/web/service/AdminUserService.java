package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.dto.AdminUserFormResultDTO;
import edu.utdallas.csmc.web.dto.UserDataTableResultDTO;
import edu.utdallas.csmc.web.dto.UserResultDTO;
import edu.utdallas.csmc.web.helper.AdminUserHelper;
import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.RoleRepository;
import edu.utdallas.csmc.web.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    public UserDataTableResultDTO queryUsersByPage(DataTablesInput input) {

        UserDataTableResultDTO userDataTableResultDTO = new UserDataTableResultDTO();
        List<UserResultDTO> userResultDTOList = new ArrayList<>();
        AdminUserHelper helper = new AdminUserHelper();

        //custom sort by column
        Pageable pageable = helper.sortByColumn(input);

        //query by tab or search box
        String word = helper.filterWord(input);

        Page<User> userPage = userRepository.findUsersByPage2(word, pageable);
        List<User> userList = userPage.getContent();
        for (User user : userList) {
            UserResultDTO userResultDTO = new UserResultDTO();

            userResultDTO.setUserId(user.getId());
            userResultDTO.setUserName(user.getUsername());
            userResultDTO.setFirstName(user.getFirstName());
            userResultDTO.setLastName(user.getLastName());

            List<Role> roles = user.getRoles();
            StringBuilder rolesBuilder = new StringBuilder();
            for (int i = 0; i < roles.size(); i++) {
                rolesBuilder.append(roles.get(i).getName());
                if (i < roles.size() - 1) {
                    rolesBuilder.append(" || ");
                }
            }
            userResultDTO.setRoles(rolesBuilder.toString());

            userResultDTOList.add(userResultDTO);
        }

        userDataTableResultDTO.setData(userResultDTOList);
        userDataTableResultDTO.setRecordsTotal(userPage.getTotalElements());
        userDataTableResultDTO.setRecordsFiltered(userPage.getTotalElements());
        userDataTableResultDTO.setDraw(input.getDraw());
        return userDataTableResultDTO;
    }

    public AdminUserFormResultDTO getNewUserForm() {
        AdminUserFormResultDTO tmp = new AdminUserFormResultDTO();
        List<Role> roleList = roleRepository.findAll();
        List<String> roleIDList = new ArrayList<>();
        List<String> roleNameList = new ArrayList<>();

        for (Role role : roleList) {
            roleIDList.add(role.getId().toString());
            roleNameList.add(role.getName());
        }

        tmp.setRolesIdList(roleIDList);
        tmp.setRolesNameList(roleNameList);

        return tmp;
    }

    public AdminUserFormResultDTO createNewUser(String userName, String firstName, String lastName, String cardId, String scanCode, String[] roleSelected) {

        User newUser = new User();
        List<Role> roleList = new ArrayList<>();
        AdminUserFormResultDTO adminUserFormResultDTO = new AdminUserFormResultDTO();

        for (String roleId : roleSelected) {
            Optional<Role> roleOp = roleRepository.findById(UUID.fromString(roleId));
            if (roleOp.isPresent()) {
                roleList.add(roleOp.get());
            }
        }

        cardId = cardId.trim().isEmpty() ? null : cardId.trim();
        scanCode = scanCode.trim().isEmpty() ? null : scanCode.trim();

        Optional<User> netIdOp = null;
        if (userName != null) {
            netIdOp = userRepository.findByUsername(userName);
        }
        Optional<User> cardIdOp = null;
        if (cardId != null) {
            cardIdOp = userRepository.findByCardId(cardId);
        }
        Optional<User> scanCodeOp = null;
        if (scanCode != null) {
            scanCodeOp = userRepository.findByScancode(scanCode);
        }

        if (netIdOp != null && netIdOp.isPresent()) {
            adminUserFormResultDTO.setMessage("Net ID already exists");
        } else if (cardIdOp != null && cardIdOp.isPresent()) {
            adminUserFormResultDTO.setMessage("Card ID already exists");
        } else if (scanCodeOp != null && scanCodeOp.isPresent()) {
            adminUserFormResultDTO.setMessage("Scan Code already exists");
        } else {
            newUser.setUsername(userName);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setCardId(cardId);
            newUser.setScancode(scanCode);
            newUser.setRoles(roleList);
            userRepository.save(newUser);
            System.out.println("Saved new user!!");
        }

        //fetch roles collection
        List<Role> roles = roleRepository.findAll();
        List<String> roleIDList = new ArrayList<>();
        List<String> roleNameList = new ArrayList<>();
        for (Role role : roles) {
            roleIDList.add(role.getId().toString());
            roleNameList.add(role.getName());
        }
        adminUserFormResultDTO.setRolesIdList(roleIDList);
        adminUserFormResultDTO.setRolesNameList(roleNameList);

        return adminUserFormResultDTO;
    }


    public AdminUserFormResultDTO getUpdateUserForm(UUID userId) {
        AdminUserFormResultDTO tmp = new AdminUserFormResultDTO();
        List<String> roleIDList = new ArrayList<>();
        List<String> roleNameList = new ArrayList<>();
        List<Boolean> roleSelectList = new ArrayList<>();

        List<Role> roleList = roleRepository.findAll();
        for (int i = 0; i < roleList.size(); i++) {
            roleSelectList.add(false);
        }

        Optional<User> userOp = userRepository.findById(userId);

        if (userOp.isPresent()) {
            User user = userOp.get();
            tmp.setUserId(user.getId());
            tmp.setUserName(user.getUsername());
            tmp.setFirstName(user.getFirstName());
            tmp.setLastName(user.getLastName());
            tmp.setCardId(user.getCardId());
            tmp.setScanCode(user.getScancode());

            List<Role> selectedRoles = user.getRoles();
            for (Role roleSelected : selectedRoles) {
                for (int i = 0; i < roleList.size(); i++) {
                    if (roleList.get(i).getId() == roleSelected.getId()) {
                        roleSelectList.set(i, true);
                        break;
                    }
                }
            }

            for (Role role : roleList) {
                roleIDList.add(role.getId().toString());
                String Name = role.getName();
                String roleName = Name.substring(0, 1).toUpperCase() + Name.substring(1).toLowerCase();
                roleNameList.add(roleName);
                //roleNameList.add(role.getName());
            }

            tmp.setRolesIdList(roleIDList);
            tmp.setRolesNameList(roleNameList);
            tmp.setRoleSelectedList(roleSelectList);
        }

        return tmp;
    }

    public AdminUserFormResultDTO updateCreatedUser(UUID userId, String userName, String firstName, String lastName, String cardId, String scanCode, String[] roleSelected) {
        AdminUserFormResultDTO adminUserFormResultDTO = new AdminUserFormResultDTO();
        List<Role> roleList = new ArrayList<>();
        AdminUserHelper helper = new AdminUserHelper();

        Optional<User> userOp = userRepository.findById(userId);
        if (userOp.isPresent()) {
            User user = userOp.get();
            cardId = cardId.trim().isEmpty() ? null : cardId.trim();
            scanCode = scanCode.trim().isEmpty() ? null : scanCode.trim();

            Optional<User> netIdOp = null;
            if (userName != null) {
                netIdOp = userRepository.findByUsername(userName);
            }
            Optional<User> cardIdOp = null;
            if (cardId != null) {
                cardIdOp = userRepository.findByCardId(cardId);
            }
            Optional<User> scanCodeOp = null;
            if (scanCode != null) {
                scanCodeOp = userRepository.findByScancode(scanCode);
            }

            if (netIdOp != null && netIdOp.isPresent() && !userName.equals(user.getUsername())) {
                adminUserFormResultDTO.setMessage("Net ID already exists");
            } else if (cardIdOp != null && cardIdOp.isPresent() && !cardId.equals(user.getCardId())) {
                adminUserFormResultDTO.setMessage("Card ID already exists");
            } else if (scanCodeOp != null && scanCodeOp.isPresent() && !scanCode.equals(user.getScancode())) {
                adminUserFormResultDTO.setMessage("Scan Code already exists");
            } else {

                user.setUsername(userName);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setCardId(cardId);
                user.setScancode(scanCode);

                for (String roleId : roleSelected) {
                    Optional<Role> roleOp = roleRepository.findById(UUID.fromString(roleId));
                    if (roleOp.isPresent()) {
                        roleList.add(roleOp.get());
                    }
                }

                user.setRoles(roleList);
                userRepository.save(user);
                System.out.println("Updated new user!!");
            }

            //Pass parameters value back to form
            List<Role> roles = roleRepository.findAll();
            helper.passParametersBackToForm(adminUserFormResultDTO, user, roles);

        }

        return adminUserFormResultDTO;
    }

    public void deleteUser(UUID userId) {
        Optional<User> userOp = userRepository.findById(userId);
        if (userOp.isPresent()) {
            userRepository.deleteUser(userId);
        }
    }
}
