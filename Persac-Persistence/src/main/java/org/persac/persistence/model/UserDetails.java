package org.persac.persistence.model;

import java.io.Serializable;

/**
 * @author mzhokha
 * @since 10.07.2014
 */
public interface UserDetails extends Serializable {

    public Integer getId();
    public void setId(Integer id);
    public String getUserName();
    public void setUserName(String userName);
}
