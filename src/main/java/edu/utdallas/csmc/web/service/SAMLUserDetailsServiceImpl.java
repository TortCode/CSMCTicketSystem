package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.user.Role;
import edu.utdallas.csmc.web.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;
import org.springframework.stereotype.Service;
import org.opensaml.saml2.core.Attribute;
import edu.utdallas.csmc.web.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

@Service
public class SAMLUserDetailsServiceImpl implements SAMLUserDetailsService, UserDetailsService {

    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(SAMLUserDetailsServiceImpl.class);

    @Autowired
    SSORoleService ssoRoleService;

    @Autowired
    DefaultRoleService roleService;

    @Autowired
    UserRepository userRepository;

    private static final String USER_EMAIL_ATTR = "urn:oid:0.9.2342.19200300.100.1.3";
    private static final String USER_NAME_ATTR = "urn:oid:2.16.840.1.113730.3.1.241";
    private static final String USER_ROLE_ATTR = "urn:oid:1.3.6.1.4.1.5923.1.1.1.1";

    // Need to fetch from the database later...
    private static final List<String> allowedRoles = Arrays.asList("student", "admin", "instructor", "mentor", "developer", "sponsor", "teaching assistant");
    
    @Override
    public UserDetails loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        edu.utdallas.csmc.web.model.user.User userToReturn = null;

        LOG.debug("loadUserBySAML {}", credential);

        try {
            List<Attribute> attributes = credential.getAttributes();
            for(int i = 0; i < attributes.size(); i++){
                LOG.debug("Attribute " + (i+1) + ": " + attributes.get(i).getName());
                LOG.debug("Attribute " + (i+1) + " Name: " + attributes.get(i).getFriendlyName());
                LOG.debug("Attribute " + (i+1) + " Value: " + attributes.get(i).getAttributeValues());
            }

            String userID = credential.getAttributeAsString(USER_EMAIL_ATTR);
            LOG.info(userID + " is logged in");
            userID = userID.substring(0, userID.indexOf('@'));

            // check if the user is in our database
            edu.utdallas.csmc.web.model.user.User currentUser = ssoRoleService.getUserByUsername(userID).orElse(null);
            if (currentUser != null) {
                LOG.debug("loadUserBySAML(), attempting to fetch user authorities, userID: " + userID);
                userToReturn = currentUser;
            }
            else {
                LOG.info("New user, userID: " + userID);

                // add user to user table and set role from the info getting from SAML
                userToReturn = new User();
                userToReturn.setUsername(userID);

                String userName = credential.getAttributeAsString(USER_NAME_ATTR);
                LOG.info(userName);
                userToReturn.setFirstName(userName.substring(0, userName.indexOf(' ')));
                userToReturn.setLastName(userName.substring(userName.indexOf(' ') + 1));

                List<Role> userRoles = new ArrayList<>();
                String[] userRoleString = credential.getAttributeAsStringArray(USER_ROLE_ATTR);

                for (String role : userRoleString) {
                    if (allowedRoles.contains(role)) {
                        userRoles.add(roleService.getRoleByName(role));
                    }
                }

		// Save user information
		userToReturn.setRoles(new ArrayList<Role>());	
                userRepository.save(userToReturn);	
		
		// Update user roles and save user
		userToReturn.getRoles().addAll(userRoles);
		userRepository.save(userToReturn);	
		
            }

            Collection<GrantedAuthority> grantedRoles = new ArrayList<>();
            List<Role> userRoleString = userToReturn.getRoles();
            for (Role role : userRoleString) {
                if(allowedRoles.contains(role.getName())){
                    grantedRoles.add(new SimpleGrantedAuthority(role.getName().replaceAll(" ", "_").toUpperCase()));
                }
            }
            return new org.springframework.security.core.userdetails.User(userID, "DUMMY_PWD", grantedRoles);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
		    return null;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            edu.utdallas.csmc.web.model.user.User currentUser = ssoRoleService.getUserByUsername(username).orElse(null);
            Collection<GrantedAuthority> grantedRoles = new ArrayList<>();
            if(currentUser != null) {
                List<Role> userRoleString = currentUser.getRoles();
                for (Role role : userRoleString) {
                    if(allowedRoles.contains(role.getName())){
                        grantedRoles.add(new SimpleGrantedAuthority(role.getName().replaceAll(" ", "_").toUpperCase()));
                    }
                }
            } else {
                throw new UsernameNotFoundException("Unable to find user...");
            }

            return new org.springframework.security.core.userdetails.User(username, "DUMMY_PWD", true, true, true, true, grantedRoles);

        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
		    return null;
        }
    }
}
