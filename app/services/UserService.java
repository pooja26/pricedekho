package services;

import models.IdsAndRules;
import models.User;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import play.Logger;
import util.MongoConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pooja Mahapatra on 26/03/17 11:36 AM.
 */
public class UserService {


    private Query<User> query = null;

    public List<User> getAllUsers() {
        Query<User> queryUser = MongoConfig.getDB().find(User.class);
        return queryUser.asList();
    }

    public User getUser(String emailId) {

        query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        User user = query.field("emailId").equal(emailId).get();
        return user;
    }

    public void registerProduct(String emailId, IdsAndRules idsAndRules) {

        User user = getUser(emailId);
        if (user != null) {
            if(user.getIdsAndRules() == null) {
                List<IdsAndRules> idsAndRulesList = new ArrayList<>();
                idsAndRulesList.add(idsAndRules);
                user.setIdsAndRules(idsAndRulesList);
            }
            else if (user.getIdsAndRules() != null && !user.getIdsAndRules().contains(idsAndRules)) {
                user.getIdsAndRules().add(idsAndRules);
            }
        } else {
            user = new User();
            user.setEmailId(emailId);
            List<IdsAndRules> idsAndRulesList = new ArrayList<>();
            idsAndRulesList.add(idsAndRules);
            user.setIdsAndRules(idsAndRulesList);
        }
        MongoConfig.getDB().save(user);
    }

    public void updateProduct(String emailId, IdsAndRules idsAndRules) {
        query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        query.filter("emailId",emailId);
        UpdateOperations<User>updateOperations = MongoConfig.getDB().createUpdateOperations(User.class)
                .set("idsAndRules.$.ruleIntegrations",idsAndRules.getRuleIntegrations())
                .set("idsAndRules.$.logicalOperator",idsAndRules.getLogicalOperator());
        UpdateResults updateResults = MongoConfig.getDB().update(
                query.filter("idsAndRules.prodId",idsAndRules.getProdId())
                ,updateOperations);
        Logger.debug("Update results: ",updateResults);
    }

    public void getAllProductsOfAUser(String emailId) {
        if (query == null) {
            query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        }
        User user = query.field("emailId").equal(emailId).retrievedFields(true, "registeredIds").get();
    }

    public void getFilteredProduct() {
     }


    public void unregisterProduct(String emailId, IdsAndRules idsAndRules) {
        query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        User user = query.field("emailId").equal(emailId).get();
        if (user.getIdsAndRules().contains(idsAndRules)) {
            user.getIdsAndRules().add(idsAndRules);
            MongoConfig.getDB().save(user);
        }


    }

}
