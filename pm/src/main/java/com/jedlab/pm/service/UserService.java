package com.jedlab.pm.service;

import java.util.ArrayList;
import java.util.Iterator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.jedlab.framework.util.CollectionUtil;
import com.jedlab.pm.dao.UserDao;
import com.jedlab.pm.model.User;

@Service
@Transactional
public class UserService extends AbstractCrudService<User>
{

    @Autowired
    UserDao userDao;

    
    public void save(User user)
    {
        userDao.save(user);
    }

    public void activateUserById(Long userId)
    {
        userDao.activateUserById(userId);
    }

    public User findById(Long userId)
    {
        return userDao.findOne(userId);
    }

    public User findByUsername(String username)
    {
        ArrayList<User> userList = Lists.newArrayList(userDao.findAll(new UsernameSpec(username)).iterator());
        if(CollectionUtil.isEmpty(userList))
            return null;
        return userList.iterator().next();
    }

    public static class UsernameSpec implements Specification<User>
    {
        private String username;

        public UsernameSpec(String username)
        {
            this.username = username;
        }

        public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb)
        {
            return cb.equal(root.get("username"), this.username);
        }

    }

    @Override
    public AbstractDAO<User> getDao()
    {
        return userDao;
    }

   

}
