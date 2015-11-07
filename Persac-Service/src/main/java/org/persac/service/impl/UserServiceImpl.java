package org.persac.service.impl;

import org.persac.persistence.dao.RoleDao;
import org.persac.persistence.dao.UserDao;
import org.persac.persistence.model.Role;
import org.persac.persistence.model.User;
import org.persac.persistence.model.UserDetails;
import org.persac.persistence.model.UserRole;
import org.persac.service.UserService;
import org.persac.service.exception.EmailExistsException;
import org.persac.service.exception.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mzhokha
 * @since 09.07.2014
 */
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    UserDetails userDetails;

    @Override
    public User getUserByName(String name) {
        return userDao.getUserByName(name);
    }

    @Override
    public void saveUserDetailsInSession(String name) {
        if (userDetails.getUserName() == null) {
            User user = userDao.getUserByName(name);

            userDetails.setUserName(name);
            userDetails.setId(user.getId());
        }
    }

    @Override
    public User registerNewUserAccount(User user) throws EmailExistsException, UsernameExistsException {
        if (emailExist(user.getEmail())) {
            throw new EmailExistsException("There is an account with the same email address:" + user.getEmail());
        }

        if (usernameExists(user.getName())) {
            throw new UsernameExistsException("There is an account with the same username: " + user.getEmail());
        }

        Role role = roleDao.loadById(1);
        user.addRole(role);

        userDao.save(user);
        return user;
    }

    private boolean emailExist(String email) {
        User user = userDao.findByEmail(email);
        return user != null;
    }

    private boolean usernameExists(String username) {
        return userDao.getUserByName(username) != null;
    }

    @Override
    @Transactional
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User persacUser = userDao.getUserByName(username);

        if (persacUser == null) {
            throw new UsernameNotFoundException("User '" + username + "' doesn't exist in database");
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>(persacUser.getUserRoles().size());
        for (UserRole userRole: persacUser.getUserRoles()) {
            grantedAuthorities.add(userRole.getRole());
        }

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(username, persacUser.getPassword(),grantedAuthorities);

        return springUser;
    }
}
