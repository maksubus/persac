package org.persac.service;

import org.persac.persistence.model.User;
import org.persac.service.exception.EmailExistsException;
import org.persac.service.exception.UsernameExistsException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author mzhokha
 * @since 09.07.2014
 */
@Transactional
public interface UserService {

    public User getUserByName(String name);
    public void saveUserDetailsInSession(String name);
    User registerNewUserAccount(User user) throws EmailExistsException, UsernameExistsException;
}
