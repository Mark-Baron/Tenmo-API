package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class UserController {

    private UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping(path="/users")
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @GetMapping(path="/users/{username}")
    public User getUser(@PathVariable String username) {
        return userDao.findByUsername(username);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path="/users")
    public User addUser(@RequestBody User user) {
        userDao.create(user.getUsername(), user.getPassword());
        return user;
    }

}
