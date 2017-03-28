package models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Created by pooja_mahapatra on 24/03/17.
 */

@Entity
public class User {
    @Id
    private ObjectId id;

    private Long prodId;

    private String name;

    private String emailId;

    @Embedded
    private List<IdsAndRules> idsAndRules;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public List<IdsAndRules> getIdsAndRules() {
        return idsAndRules;
    }

    public void setIdsAndRules(List<IdsAndRules> idsAndRules) {
        this.idsAndRules = idsAndRules;
    }
}