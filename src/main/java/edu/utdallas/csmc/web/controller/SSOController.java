package edu.utdallas.csmc.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.GrantedAuthority;

@Controller
public class SSOController {
    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(SSOController.class);

    @Value("${app.sandbox:false}")
    private boolean isSandbox;

    /*
    actual log in and validation
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String logInSSO(HttpServletRequest request, Model model) {

        if(isSandbox){
            return "redirect:/";
        }

        System.out.println("auth start");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("auth "+auth);

        if (auth == null) {
            LOG.debug("Current authentication instance from security context is null");
        } else {
            LOG.debug("Current authentication instance from security context: " + this.getClass().getSimpleName());
        }

        if (auth == null || (auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        } else {
            System.out.println("Details " + auth.getDetails().toString());
            LOG.debug("Principal " + auth.getPrincipal().toString());
            LOG.debug("Authenticated " + (auth.isAuthenticated() ? "true" : "false"));
            LOG.debug("Details " + auth.getDetails().toString());
            LOG.debug("Credentials " + auth.getCredentials().toString());
            LOG.debug("Name " + auth.getName());
            User loggedUser = (User)auth.getPrincipal();
            LOG.info("User Email: " + loggedUser.getUsername());
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOutSSO(HttpServletRequest request, Model model) {
        System.out.println("logout");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, null, auth);
        return "redirect:/";
    }


}
