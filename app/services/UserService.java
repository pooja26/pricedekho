package services;

import models.IdsAndRules;
import models.Product;
import models.RuleIntegration;
import models.User;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import play.Logger;
import util.MongoConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pooja Mahapatra on 26/03/17 11:36 AM.
 */
public class UserService {


    private Query<User> query = null;

    private Query<Product> queryProd = null;

    private Product product;

    private Map<Long,List<RuleIntegration>> map;

    private List<Product> userProducts;

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
        UpdateOperations<User>updateOperations = MongoConfig.getDB().createUpdateOperations
                (User.class).set("idsAndRules.$.ruleIntegrations",idsAndRules.getRuleIntegrations());
        UpdateResults updateResults = MongoConfig.getDB().update(query.filter("emailId",emailId)
                .filter("idsAndRules.prodId",idsAndRules.getProdId()),updateOperations);
        Logger.debug("Update results: ",updateResults);
    }

    public void getAllProductsOfAUser(String emailId) {
        if (query == null) {
            query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        }
        User user = query.field("emailId").equal(emailId).retrievedFields(true, "registeredIds").get();
    }

    public List<Product> getFilteredProduct() {

        final Query<User> queryUser = MongoConfig.getDB().find(User.class);
        queryUser.fetch().forEach(obj -> {
            List<IdsAndRules> idsAndRules;
            map = new HashMap<>();
            idsAndRules = obj.getIdsAndRules();

            for(IdsAndRules join: idsAndRules){
                map.put(join.getProdId(),join.getRuleIntegrations());
            }
            for(Map.Entry<Long,List<RuleIntegration>> entry: map.entrySet()){
                userProducts = new ArrayList<>();
                Long pId = entry.getKey();
                List<RuleIntegration> ruleIntegration = entry.getValue();
                for(RuleIntegration rules: ruleIntegration) {
                    queryProd = MongoConfig.getDB().createQuery(Product.class).disableValidation();
                    String key = rules.getKey();
                    String operator = rules.getOperator();
                    String condition = key+" "+operator;
                    Double value = rules.getValue();
                    product = queryProd.field("id").equal(pId).filter(condition,value).get();

                    if(product != null){
                        userProducts.add(product);

                        //Trigger the mail if filtered products are available
                        System.out.println(obj.getEmailId());
                    }
                }
            }
        });

        return userProducts;
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
