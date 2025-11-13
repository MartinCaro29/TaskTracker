package com.example.demo.repositories;

import com.example.demo.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase(){
        userRepository.deleteAll();
    }

    @Test
    void testSaveUser(){
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@test.com");
        user.setPassword("12345678");

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("Test", saved.getUsername());
    }



    @Test
    void testFindAll(){
        User user1 = new User();
        user1.setUsername("Test1");
        user1.setEmail("test1@test.com");
        user1.setPassword("12345678");

        User user2 = new User();
        user2.setUsername("Test2");
        user2.setEmail("test2@test.com");
        user2.setPassword("12345678");

        userRepository.save(user1);
        userRepository.save(user2);

        List<User> allUsers = userRepository.findAll();
        assertEquals(2, allUsers.size());

    }

    @Test
    void testFindByUsername(){
        User user = new User();
        user.setUsername("Test");
        user.setEmail("test@test.com");
        user.setPassword("12345678");

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("Test");

        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getUsername());
    }
}
