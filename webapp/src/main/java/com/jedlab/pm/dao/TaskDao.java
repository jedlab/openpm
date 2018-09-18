package com.jedlab.pm.dao;

import org.springframework.data.repository.CrudRepository;

import com.jedlab.pm.model.Task;

public interface TaskDao extends CrudRepository<Task, Long>
{

}
