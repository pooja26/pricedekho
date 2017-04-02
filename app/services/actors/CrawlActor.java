package services.actors;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import models.CrawlProduct;
import models.Product;
import models.SkuInventory;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import services.ProductService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Created by pooja_mahapatra on 25/3/17.
 */
public class CrawlActor extends UntypedActor {

    private WSClient wsClient;

    private ProductService productService;

    private long crawlRun;

    private Date date;

    private static final String urlPrefix = "http://developer.myntra.com/style/";

    @Inject
    public CrawlActor(WSClient wsClient, ProductService productService, Date date) {
        this.wsClient = wsClient;
        this.productService = productService;
        this.crawlRun = 1;
        this.date = date;
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        Logger.info("Crawl run: " + crawlRun++);
        ArrayList<CrawlProduct> productList = (ArrayList<CrawlProduct>) productService.getAllCrawlProducts();
        productList.forEach(crawlProduct -> {
            Long productId = crawlProduct.getProductId();
            WSRequest wsRequest = wsClient.url(urlPrefix + productId);
            wsRequest.setRequestTimeout(30 * 1000);
            CompletionStage<WSResponse> responsePromise = wsRequest.get();
            responsePromise.thenApply(WSResponse::asJson);
            responsePromise.whenComplete((response, throwable) -> {
                Product product = new Product();
                Long time = date.getTime();
                JsonNode jsonNode = response.asJson();
                JsonNode data = jsonNode.findPath("data");
                double price = data.findPath("price").asDouble();
                double discountedPrice = data.findPath("discountedPrice").asDouble();
                double discount = ((price - discountedPrice) / price) * 100;
                product.setId(data.findPath("id").asLong());
                product.setTimestamp(time);
                product.setPrice(price);
                product.setDiscountedPrice(discountedPrice);
                product.setDiscount(discount);
                List<SkuInventory> skuInventoryList = new ArrayList<>();
                int i = 0;
                for (JsonNode styleOptions : data.findPath("styleOptions")) {
                    SkuInventory si = new SkuInventory();
                    si.setId(styleOptions.findPath("skuId").longValue());
                    si.setAvailability(styleOptions.findPath("available").booleanValue());
                    si.setInventoryCount(styleOptions.findPath("inventoryCount").intValue());
                    si.setSize(styleOptions.findPath("value").asText());
                    skuInventoryList.add(si);
                }
                product.setSku(skuInventoryList);
                productService.process(product);
            });
        });
    }
}