package com.ecommerce.user_service.service;

import com.ecommerce.user_service.dto.AuthResponse;
import com.ecommerce.user_service.dto.LoginRequest;
import com.ecommerce.user_service.dto.RegisterRequest;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.repository.UserRepository;
import com.ecommerce.user_service.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new UserAlreadyExistsException("Username is already exist");
        }

        if (userRepository.existsByEmail(request.getEmail())){
            throw  new UserAlreadyExistsException("Email is already exist");
        }

        User user=User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        User user= userRepository.findByUsername(request.getUsername()).
                orElseThrow(()->new InvalidCredentialsException("invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new InvalidCredentialsException("invalid username or password");
        }
        String token = jwtUtil.generateToken(request.getUsername(),user.getRole());
            return new  AuthResponse(token,user.getUsername(),user.getRole());
        }
    }



