package org.persac.persistence.dao;

import org.persac.persistence.model.Role;

/**
 * Created by maks on 13.10.15.
 */
public interface RoleDao {

    Role getById(int id);
    Role loadById(int id);
}
