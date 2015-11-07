package org.persac.persistence.model;

import java.io.Serializable;

/**
 * @author mzhokha
 * @since 10.07.2014
 */
public class UserDetailsImpl implements UserDetails, Serializable {

    private Integer id;
    private String userName;

    public UserDetailsImpl() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
