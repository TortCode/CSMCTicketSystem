package edu.utdallas.csmc.web;

import edu.utdallas.csmc.web.service.DefaultUsernameService;
import edu.utdallas.csmc.web.service.DefaultRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalValuesControllerSetup {

    @Autowired
    private DefaultUsernameService defaultUsernameService;

    @Autowired
    private DefaultRoleService defaultRoleService;

    @Value("${app.sandbox:false}")
    private boolean isSandbox;

    @ModelAttribute
    public void initModel(ModelMap model) {
        model.addAttribute("user_services", defaultUsernameService.getUsername());
        model.addAttribute("user_role", defaultRoleService.getUserRoles());
        model.addAttribute("is_impersonating", defaultUsernameService.isImpersonationActive());
        model.addAttribute("is_logged_in", defaultUsernameService.getLoginStatus());
        model.addAttribute("preferred_name", defaultUsernameService.getPreferredName());
        model.addAttribute("is_sandbox", isSandbox);
    }

}
