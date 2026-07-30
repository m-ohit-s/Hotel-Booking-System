package com.my_space.airbnb_clone.util;

import com.my_space.airbnb_clone.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

public class AppUtils {
    public static User getCurrentUser() {
        return (User) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}
