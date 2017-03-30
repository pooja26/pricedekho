package services;

import models.CrawlProduct;
import models.Product;
import org.mongodb.morphia.query.Query;
import util.MongoConfig;

import java.util.List;

/**
 * Created by Harkrishn Patro on 25/03/17 9:58 AM.
 */
public class ProductService {

    private Query<Product> productQuery = null;
    private Query<CrawlProduct> crawlProductQuery = null;

    public void process(Product product) {
        MongoConfig.getDB().save(product);
    }

    public void insertProductsToBeCrawled(Long productId) {
        CrawlProduct crawlProduct = new CrawlProduct();
        crawlProduct.setProductId(productId);
        MongoConfig.getDB().save(crawlProduct);
    }

    public List<CrawlProduct> getAllCrawlProducts() {
        if (crawlProductQuery == null) {
            crawlProductQuery = MongoConfig.getDB().createQuery(CrawlProduct.class);
        }
        return crawlProductQuery.asList();
    }

    public List<Product> getListFromRange(Long prodId, Long startDate, Long endDate) {
        if (productQuery == null)
            productQuery = MongoConfig.getDB().createQuery(Product.class).disableValidation();
        productQuery.field("id").equal(prodId)
                .field("timestamp").greaterThanOrEq(startDate)
                .field("timestamp").lessThanOrEq(endDate);
        List<Product> list = productQuery.asList();
        return list;
    }
}
