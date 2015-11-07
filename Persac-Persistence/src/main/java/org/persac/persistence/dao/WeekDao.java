package org.persac.persistence.dao;

import org.persac.persistence.model.Week;

import java.util.Date;
import java.util.List;

/**
 * @author mzhokha
 * @since 26.03.14
 */
public interface WeekDao {

    public boolean save(Week week);
    public Week getById(int id);
    public List<Week> getLast5Weeks();
    public List<Week> getAll();
    public Week getByMondayDate(Date mondayDate);
    public void update(Week week);
    public void deleteById(Integer id);
    public void deleteAll();
}
