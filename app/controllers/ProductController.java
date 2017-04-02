package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import models.IdsAndRules;
import models.Product;
import models.RuleIntegration;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;
import services.UserService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pooja_mahapatra on 24/03/17.
 */
public class ProductController extends Controller {


    @Inject
    private ProductService productService;

    @Inject
    private UserService userService;

    // Just update the set of products in DB to be crawled through register api.
    public Result register(Long prodId) throws JsonProcessingException {

        String emailId = request().body().asJson().findPath("emailId").asText();
        String operator = request().body().asJson().findPath("condition").asText();
        JsonNode idsAndRules1 = request().body().asJson().findPath("ruleIntegration");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,false);
        List<RuleIntegration> ruleIntegrationList = mapper.treeToValue(idsAndRules1,new ArrayList<RuleIntegration>().getClass());
        productService.insertProductsToBeCrawled(prodId);
        IdsAndRules idsAndRules = new IdsAndRules();
        idsAndRules.setProdId(prodId);
        idsAndRules.setRuleIntegrations(ruleIntegrationList);
        idsAndRules.setLogicalOperator(operator);
        userService.registerProduct(emailId,idsAndRules);
        return ok("Request sent to service");
    }

    public Result update(Long prodId) throws JsonProcessingException {

        String emailId = request().body().asJson().findPath("emailId").asText();
        String operator = request().body().asJson().findPath("condition").asText();
        JsonNode ruleListJsonNode = request().body().asJson().findPath("ruleIntegration");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,false);
        List<RuleIntegration> ruleIntegrationList = mapper.treeToValue(ruleListJsonNode,new ArrayList<RuleIntegration>().getClass());
        IdsAndRules idsAndRules = new IdsAndRules();
        idsAndRules.setProdId(prodId);
        idsAndRules.setRuleIntegrations(ruleIntegrationList);
        idsAndRules.setLogicalOperator(operator);
        userService.updateProduct(emailId,idsAndRules);
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

    // Get all products to be crawled.

    // Crawler should be instantiated through a scheduler. Pick all the products to be crawled from DB.

    // Maintain a user to product(s) mapping for relevance and notification(s).

    // Notification to user through email.

    // v2. -> Have a rule engine for a product to trigger a notification on fulfillment. if size == x, price < 500 or discount > 60%, trigger email.


}