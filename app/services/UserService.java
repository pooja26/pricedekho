package services;

import models.IdsAndRules;
import models.Product;
import models.User;
import org.mongodb.morphia.query.Query;
import util.MongoConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pooja Mahapatra on 26/03/17 11:36 AM.
 */
public class UserService {

    private Query<User> query = null;

    private User user = new User();

    public User getUser(String emailId) {

        query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        User user = query.field("emailId").equal(emailId).get();
        return user;
    }

    public void registerProduct(String emailId, IdsAndRules idsAndRules) {

        User user = getUser(emailId);
        if (user != null) {
            if (user.getIdsAndRules() != null && !user.getIdsAndRules().contains(idsAndRules)) {
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
        User user = query.field("emailId").equal(emailId).filter("idsAndRules.prodId", idsAndRules.getProdId()).get();
        System.out.println(user.getIdsAndRules());
    }

    public void getAllProductsOfAUser(String emailId) {
        if (query == null) {
            query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        }
        User user = query.field("emailId").equal(emailId).retrievedFields(true, "registeredIds").get();
    }

    public List<Product> getFilteredProduct(Long prodId, String operator, String key, Double value) {
        Query<Product> queryProd = null;
        List<Product> filteredProducts = null;
        if (query == null) {
            queryProd = MongoConfig.getDB().createQuery(Product.class).disableValidation();
        }
        if (key.equals("discount")) {
            Product calcDiscount = queryProd.field("id").equal(prodId).get();
            Double price = calcDiscount.getPrice();
            Double discountedPrice = calcDiscount.getDiscountedPrice();
            Double discount = (price - discountedPrice) / price * 100;

        } else {
            queryProd.field("id").equal(prodId);
            if (operator.equals(">")) {
                queryProd.field(key).greaterThan(value);
            }
            if (operator.equals(">=")) {
                queryProd.field(key).greaterThanOrEq(value);
            }
            if (operator.equals("<=")) {
                queryProd.field(key).lessThanOrEq(value);
            }
            if (operator.equals("<")) {
                queryProd.field(key).lessThan(value);
            }
            filteredProducts = queryProd.asList();
        }
        return filteredProducts;
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
