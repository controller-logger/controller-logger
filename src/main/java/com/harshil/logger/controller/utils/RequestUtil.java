package com.harshil.logger.controller.utils;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.harshil.logger.controller.bean.RequestContext;

public class RequestUtil {

    public static RequestContext getRequestContext() {
        HttpServletRequest request = getCurrentHttpRequest();
        UserDetails user = getCurrentUser();

        return new RequestContext().add("url", getRequestUrl(request)).add("username", getRequestUserName(user));
    }

    @Nullable
    private static String getRequestUrl(@Nullable HttpServletRequest request) {
        return request == null ? null : request.getRequestURL().append(request.getQueryString()).toString();
    }

    @Nullable
    private static String getRequestUserName(@Nullable UserDetails userDetails) {
        return userDetails == null ? null : userDetails.getUsername();
    }

    @Nullable
    private static HttpServletRequest getCurrentHttpRequest() {
        HttpServletRequest request = null;
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null && requestAttributes instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes)requestAttributes).getRequest();
        }
        return request;
    }

    @Nullable
    private static UserDetails getCurrentUser() {
        UserDetails user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = null;
        if (authentication != null) {
            principal = authentication.getPrincipal();
        }
        if (principal != null && principal instanceof UserDetails) {
            user = (UserDetails)principal;
        }
        return user;
    }

}
