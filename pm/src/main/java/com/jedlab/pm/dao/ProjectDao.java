package com.jedlab.pm.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.jedlab.pm.model.Project;

public interface ProjectDao extends PagingAndSortingRepository<Project, Long>
{

}
