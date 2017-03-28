package services.actors;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.CrawlProduct;
import models.Product;
import models.SkuInventory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import play.libs.ws.WSClient;
import services.ProductService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pooja_mahapatra on 25/3/17.
 */
public class CrawlActor extends UntypedActor {

    private WSClient wsClient;

    private ProductService productService = new ProductService();

    private static final String urlPrefix = "http://developer.myntra.com/style/";

    /*
    @Inject
    public CrawlActor(WSClient wsClient) {
        this.wsClient = wsClient;
    }


    @Override
    public void onReceive(Object message) throws Throwable {
        System.out.println("Started crawling");
        ArrayList<CrawlProduct> productList =  (ArrayList<CrawlProduct>) productService.getAllCrawlProducts();
        productList.forEach(crawlProduct -> {
            Long productId = crawlProduct.getProductId();
            WSRequest wsRequest = wsClient.url( urlPrefix + productId);
            CompletionStage<WSResponse> responsePromise = wsRequest.get();
            responsePromise.thenApply(WSResponse::asJson);
            responsePromise.whenComplete((response, throwable) -> {
                Product product = new Product();
                Date date = new Date();
                Long time=date.getTime();
                JsonNode jsonNode = response.asJson();
                JsonNode data = jsonNode.findPath("data");
                product.setPrice(data.findPath("price").asDouble());
                product.setDiscountedPrice(data.findPath("discountedPrice").asDouble());
                product.setId(data.findPath("id").asLong());
                product.setTimestamp(time);
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
    */

    @Override
    public void onReceive(Object message) throws Throwable {
        ArrayList<CrawlProduct> productList = (ArrayList<CrawlProduct>) productService.getAllCrawlProducts();
        HttpClient client = new DefaultHttpClient();

        productList.forEach(crawlProduct -> {
            Long productId = crawlProduct.getProductId();
            HttpGet get = new HttpGet(urlPrefix + productId);
            try {
                HttpResponse response = client.execute(get);
                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(result.toString());;
                Product product = new Product();
                Date date = new Date();
                Long time = date.getTime();
                JsonNode data = jsonNode.findPath("data");
                product.setPrice(data.findPath("price").asDouble());
                product.setDiscountedPrice(data.findPath("discountedPrice").asDouble());
                product.setId(data.findPath("id").asLong());
                product.setTimestamp(time);
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
            } catch (Exception e) {

            }
        });
    }
}