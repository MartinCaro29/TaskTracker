package com.example.demo.services;

import com.example.demo.entities.AuditLog;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.dtos.UserDto;
import com.example.demo.entities.User;
import com.example.demo.repositories.AuditLogRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogRepository auditLogRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditLogRepository auditLogRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogRepository = auditLogRepository;
    }

    public Page<UserDto> getAllUsers(int page, int size){

        Pageable pageable = PageRequest.of(page,size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(u->new UserDto(u.getId(),u.getUsername(),u.getEmail()));
    }

    public User getUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id:" + id));
    }


    public UserDto createUser(User user) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        AuditLog auditLog = new AuditLog();
        auditLog.setEntityId(savedUser.getId());
        auditLog.setEntityType(AuditLog.EntityType.USER);
        auditLog.setAction(AuditLog.Action.CREATE);
        auditLog.setStatus(AuditLog.Status.SUCCESS);
        auditLogRepository.save(auditLog);

        return new UserDto(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());



    }

}
