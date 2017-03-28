package controllers;

import com.google.gson.Gson;
import models.IdsAndRules;
import models.Product;
import models.RuleIntegration;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;
import services.UserService;
import services.actors.ScheduledCrawler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pooja_mahapatra on 24/03/17.
 */
public class ProductController extends Controller {


    private ProductService productService = new ProductService();
    private UserService userService = new UserService();

    // Just update the set of products in DB to be crawled through register api.
    public Result register(Long prodId) {

        String emailId = request().body().asJson().findPath("emailId").asText();
        String key = request().body().asJson().findPath("key").asText();
        String operator = request().body().asJson().findPath("operator").asText();
        Double value = request().body().asJson().findPath("value").asDouble();
        RuleIntegration ruleIntegration = new RuleIntegration();
        ruleIntegration.setKey(key);
        ruleIntegration.setOperator(operator);
        ruleIntegration.setValue(value);
        List<RuleIntegration> rules = new ArrayList<>();
        rules.add(ruleIntegration);
        IdsAndRules idsAndRules = new IdsAndRules();
        idsAndRules.setProdId(prodId);
        if(idsAndRules.getRuleIntegrations() == null){
            idsAndRules.setRuleIntegrations(rules);
            List<IdsAndRules> join = new ArrayList<>();
            join.add(idsAndRules);
        }
        else {
            idsAndRules.getRuleIntegrations().add(ruleIntegration);
        }
        productService.insertProductsToBeCrawled(prodId);
        userService.registerProduct(emailId,idsAndRules);
        return ok("Request sent to service");
    }

    public Result unregister(Long prodId) {
        String emailId = request().body().asJson().findPath("emailId").asText();
        String key = request().body().asJson().findPath("key").asText();
        String operator = request().body().asJson().findPath("operator").asText();
        Double value = request().body().asJson().findPath("value").asDouble();
        RuleIntegration ruleIntegration = new RuleIntegration();
        ruleIntegration.setKey(key);
        ruleIntegration.setOperator(operator);
        ruleIntegration.setValue(value);
        IdsAndRules idsAndRules = new IdsAndRules();
        idsAndRules.setProdId(prodId);
        idsAndRules.getRuleIntegrations().add(ruleIntegration);
        userService.unregisterProduct(emailId,idsAndRules);
        return ok(prodId + " unregistered from your list.");
    }


    public Result getProductInfoFromDateRange(Long prodId, Long startDate, Long endDate) {
        List<Product> list = productService.getListFromRange(prodId,startDate,endDate);
        return ok(new Gson().toJson(list, new ArrayList<Product>().getClass()));
    }

    //TODO: Remove this in future
    public Result scheduleCrawl() {
        new ScheduledCrawler();
        return ok("Crawler is up");
    }


    // Get all products to be crawled.

    // Crawler should be instantiated through a scheduler. Pick all the products to be crawled from DB.

    // Maintain a user to product(s) mapping for relevance and notification(s).

    // Notification to user through email.

    // v2. -> Have a rule engine for a product to trigger a notification on fulfillment. if size == x, price < 500 or discount > 60%, trigger email.


}