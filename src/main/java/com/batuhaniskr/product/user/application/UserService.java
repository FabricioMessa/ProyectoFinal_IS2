package com.batuhaniskr.product.user.application;

import com.batuhaniskr.product.user.domain.UserRegistrationDto;
import com.batuhaniskr.product.user.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findByEmail(String email);
    User save(UserRegistrationDto registrationDto);
}