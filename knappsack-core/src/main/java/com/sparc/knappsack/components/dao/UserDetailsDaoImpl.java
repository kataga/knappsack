package com.sparc.knappsack.components.dao;

import com.mysema.query.types.path.StringPath;
import com.sparc.knappsack.components.entities.Organization;
import com.sparc.knappsack.components.entities.QUser;
import com.sparc.knappsack.components.entities.User;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("userDetailsDao")
public class UserDetailsDaoImpl extends BaseDao implements UserDetailsDao {
    QUser userDetails = QUser.user;

    public void add(User user) {
        getEntityManager().persist(user);
    }

    public List<User> getAll() {
        return query().from(userDetails).distinct().list(userDetails);
    }

    public User get(Long id) {
        return getEntityManager().find(User.class, id);
    }

    public List<User> get(List<Long> ids) {
        if(ids == null || ids.isEmpty()) {
            return new ArrayList<User>();
        }
        return query().from(userDetails).where(userDetails.id.in(ids)).list(userDetails);
    }

    public void delete(User user) {
        getEntityManager().remove(getEntityManager().merge(user));
    }

    public User findByOpenIdIdentifier(String openIdIdentifier) {
        StringPath stringPath = new StringPath("openId");
        return query().from(userDetails).where(userDetails.openIdIdentifiers.contains(openIdIdentifier)).uniqueResult(userDetails);
    }

    public User findByEmail(String email) {
        return query().from(userDetails).where(userDetails.email.equalsIgnoreCase(email)).uniqueResult(userDetails);
    }
    
    public User findByUserName(String userName) {
        return query().from(userDetails).where(userDetails.username.equalsIgnoreCase(userName)).uniqueResult(userDetails);
    }

    @Override
    public List<User> getBatch(List<Long> ids) {
        return query().from(userDetails).where(userDetails.id.in(ids)).distinct().list(userDetails);
    }

    @Override
    public List<User> getBatchForEmails(List<String> emails) {
        Set<String> formattedEmails = new HashSet<String>();
        for (String email : emails) {
            formattedEmails.add(StringUtils.trimAllWhitespace(email.toLowerCase()));
        }
        return cacheableQuery().from(userDetails).where(userDetails.email.in(formattedEmails)).distinct().list(userDetails);
    }

    public void update(User user) {
        getEntityManager().merge(user);
    }

    public long countAll() {
        return query().from(userDetails).count();
    }

    public List<User> getUsersByActiveOrganization(Organization org) {
        return cacheableQuery().from(userDetails).where(userDetails.activeOrganization.eq(org)).list(userDetails);
    }
    
}
