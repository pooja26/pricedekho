package services;

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

    public User getUser(String emailId){

        query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        User user = query.field("emailId").equal(emailId).get();
        return user;
    }

    public void registerProduct(Long prodId,String emailId){
        //registeredProdIds = getUser();
        User user = getUser(emailId);
        if(user != null) {
            if(user.getProdIds() != null && !user.getProdIds().contains(prodId)) {
                user.getProdIds().add(prodId);
            } else {
                List<Long> prodIds = new ArrayList<>();
                prodIds.add(prodId);
                user.setProdIds(prodIds);
            }

        } else {
            user = new User();
            List<Long> prodIds = new ArrayList<>();
            prodIds.add(prodId);
            user.setProdIds(prodIds);
            user.setEmailId(emailId);
        }

        MongoConfig.getDB().save(user);
    }

    public void getAllProductsOfAUser(String emailId){
        if (query == null){
            query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        }
        User user = query.field("emailId ").equal(emailId).retrievedFields(true,"registeredIds").get();
    }

    public List<Product> getFilteredProduct(Long prodId,String operator,String key,Double value){
        Query<Product> queryProd = null;
        List<Product> filteredProducts=null;
        if (query == null){
            queryProd = MongoConfig.getDB().createQuery(Product.class).disableValidation();
        }
        if(key.equals("discount")){
            Product calcDiscount = queryProd.field("id").equal(prodId).get();
            Double price = calcDiscount.getPrice();
            Double discountedPrice = calcDiscount.getDiscountedPrice();
            Double discount = (price - discountedPrice) / price * 100;

        } else {
            queryProd.field("id").equal(prodId);
            if(operator.equals(">")){
                queryProd.field(key).greaterThan(value);
            }
            if(operator.equals(">=")){
                queryProd.field(key).greaterThanOrEq(value);
            }
            if(operator.equals("<=")){
                queryProd.field(key).lessThanOrEq(value);
            }
            if(operator.equals("<")){
                queryProd.field(key).lessThan(value);
            }
            filteredProducts = queryProd.asList();
        }
        return  filteredProducts;
    }

    public void unregisterProduct(Long prodId,String emailId){
        query = MongoConfig.getDB().createQuery(User.class).disableValidation();
        User user = query.field("emailId").equal(emailId).get();
        if(user.getProdIds().contains(prodId)){
            user.getProdIds().remove(prodId);
            MongoConfig.getDB().save(user);
        }


    }

}
