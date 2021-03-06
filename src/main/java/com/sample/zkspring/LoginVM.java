package com.sample.zkspring;

import lombok.Getter;
import lombok.Setter;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.sample.zkspring.services.authentication.UserService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginVM {
    @WireVariable UserService userService;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    @Init
    public void Init() throws ServletException {

    }

    @Command
    public void login() throws ServletException, IOException {
        RequestCache requestCache = new HttpSessionRequestCache();
        HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        HttpServletResponse response = (HttpServletResponse) Executions.getCurrent().getNativeResponse();
        try {
            request.login(this.username, this.password);
 //           SavedRequest savedRequest = requestCache.getRequest(request, response);
//
//            if (savedRequest != null) {
                Executions.sendRedirect(response.encodeRedirectURL("/"));
//            } else {
//                System.out.println("fail");
//            }
            Executions.getCurrent().setVoided(true);
        } catch (ServletException e) {
            e.printStackTrace();
        }


    }
}
