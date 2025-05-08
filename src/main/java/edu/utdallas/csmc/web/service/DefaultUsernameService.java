package edu.utdallas.csmc.web.service;

import edu.utdallas.csmc.web.model.user.User;
import edu.utdallas.csmc.web.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;

import java.util.Collection;
import java.util.Optional;

@Service
public class DefaultUsernameService {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.sandbox:false}")
    private boolean isSandbox;

    public String getUsername() {
        if(isSandbox){
            //return "sxa174330";
            return "trp210003";
            //return "sxv210070";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || (auth instanceof AnonymousAuthenticationToken)) {
            return "";
        } else {
            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)auth.getPrincipal();
            return user.getUsername();
        }
    }

    public boolean isImpersonationActive() {
        //remove print
        System.out.println("isImpersonationActive");
        boolean active = true;
        SecurityContext securityContext = SecurityContextHolder.getContext();

        if (securityContext == null) {
            active = false;
        } else {
            Authentication authWorkFor = securityContext.getAuthentication();
            System.out.print("authWorkFor "+ authWorkFor);
            if (authWorkFor == null) {
                active = false;
            } else {
                Authentication authActual = getSourceAuthentication(authWorkFor);
                System.out.print("authActual "+ authActual);
                if (authActual == null) {
                    active = false;
                }
            }
        }

        return active;
    }

    private static Authentication getSourceAuthentication(Authentication current) {
        Authentication original = null;

        // iterate over granted authorities and find the 'switch user' authority
        Collection<? extends GrantedAuthority> authorities = current.getAuthorities();

        for (GrantedAuthority auth : authorities) {
            // check for switch user type of authority
            if (auth instanceof SwitchUserGrantedAuthority) {
                original = ((SwitchUserGrantedAuthority) auth).getSource();
            }
        }

        return original;
    }


    public boolean getLoginStatus() {
        if(isSandbox) {
            return true;
        }
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() == null
                || context.getAuthentication() instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return context.getAuthentication().isAuthenticated();
    }

    public String getPreferredName() {
        Optional<User> user = userRepository.findByUsername(this.getUsername());
        return user.isPresent() ? (user.get().getProfile() != null ? (user.get().getProfile().getPreferredName()!=null?user.get().getProfile().getPreferredName():"") : "") : "";
    }

    public User getUser() {
        if (isSandbox) {
            return userRepository.getUser(this.getUsername());
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || (auth instanceof AnonymousAuthenticationToken)) {
            return null;
        } else {
            Optional<User> user = userRepository.findByUsername(this.getUsername());
            return user.isPresent() ? user.get() : null;
        }
    }

}
