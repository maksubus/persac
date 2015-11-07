package org.persac.persistence.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.persac.persistence.json.UserRoleJsonSerializer;

import javax.persistence.*;

/**
 * Created by maks on 12.10.15.
 */


@Entity
@Table(name = "user_role")
@JsonSerialize(using = UserRoleJsonSerializer.class)
public class UserRole {

    private int id;

    private User user;

    private Role role;

    public UserRole() {
    }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "role_id")
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
