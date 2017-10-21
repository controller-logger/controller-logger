package com.harshil.logger.controller.utils;

import com.harshil.logger.controller.bean.RequestContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

//    public static RequestContext getCurrentRequestContext(){
//
//    }


    @Nullable
    private static HttpServletRequest getCurrentHttpRequest(){
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes)requestAttributes).getRequest();
        }
        return request;
    }

    private static UserDetails getCurrentUser() {
        UserDetails user = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserDetails) {
            user = (UserDetails)principal;
        }
        return user;
    }

}
