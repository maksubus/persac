package org.persac.persistence.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * Created by maks on 12.10.15.
 */

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "role_string")
    private String roleString;

    public Role() {
    }

    public Role(String roleString) {
        this.roleString = roleString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoleString() {
        return roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }

    @Override
    public String getAuthority() {
        return roleString;
    }
}
