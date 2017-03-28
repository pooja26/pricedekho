package controllers;

import com.google.gson.Gson;
import models.Product;
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
        productService.insertProductsToBeCrawled(prodId);
        String emailId = request().body().asJson().findPath("emailId").asText();
        userService.registerProduct(prodId,emailId);
        return ok("Request sent to service");
    }

    public Result unregister(Long prodId) {
        String emailId = request().body().asJson().findPath("emailId").asText();
        userService.unregisterProduct(prodId,emailId);
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