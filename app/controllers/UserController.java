package controllers;

import models.Product;
import models.RuleIntegration;
import play.mvc.Controller;
import play.mvc.Result;
import services.UserService;

import java.util.List;

/**
 * Created by pooja_mahapatra on 27/03/17.
 */
public class UserController extends Controller {

    RuleIntegration ruleIntegration = new RuleIntegration();
    UserService userService = new UserService();

    public Result setRules(Long prodId, String key, String operator, Double value){

        ruleIntegration.setKey(key);
        ruleIntegration.setOperator(operator);
        ruleIntegration.setValue(value);
        ruleIntegration.setProdId(prodId);

        List<Product> filteredProducts = userService.getFilteredProduct(prodId,operator,key,value);
        return ok("Rules Set");
    }
}
