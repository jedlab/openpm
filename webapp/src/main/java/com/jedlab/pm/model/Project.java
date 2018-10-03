package com.jedlab.pm.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.omidbiz.core.axon.internal.IgnoreElement;

import com.jedlab.framework.report.ReportField;
import com.jedlab.framework.spring.dao.PO;

/**
 * @author omidp
 *
 */
@NamedQueries({@NamedQuery(name=Project.FIND_BY_NAME, query="select p from Project p where p.name = :name")})
@Entity
@Table(name = "project")
public class Project extends PO
{

    public static final String FIND_BY_NAME = "project.findByName";
    
    @Column(name = "proj_name")
    @ReportField(msg="Project_Name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<Task>(0);

    public User getOwner()
    {
        return owner;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }

    @IgnoreElement
    public List<Task> getTasks()
    {
        return tasks;
    }

    public void setTasks(List<Task> tasks)
    {
        this.tasks = tasks;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
