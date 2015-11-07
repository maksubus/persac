package org.persac.persistence.dao;

import org.persac.persistence.model.User;

/**
 * @author mzhokha
 * @since 09.07.2014
 */
public interface UserDao {

    User getUserByName(String name);
    void save(User user);
    void update(User user);
    User findByEmail(String email);

}
