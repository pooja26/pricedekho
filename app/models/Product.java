package models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Created by Pooja Mahapatra on 25/03/17 11:00 AM 12:56 PM.
 */
@Entity
public class Product {

    @Id
    ObjectId objectId;

    Long timestamp;

    long id;

    double price;

    double discountedPrice;

    double discount;

    @Embedded
    List<SkuInventory> sku;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<SkuInventory> getSku() {
        return sku;
    }

    public void setSku(List<SkuInventory> sku) {
        this.sku = sku;
    }

}
