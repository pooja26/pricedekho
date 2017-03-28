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

    private String name;

    private String emailId;

    List<Long> prodIds;

    @Embedded
    List<RuleIntegration> rules;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public List<Long> getProdIds() {
        return prodIds;
    }

    public void setProdIds(List<Long> prodIds) {
        this.prodIds = prodIds;
    }

    public List<RuleIntegration> getRules() {
        return rules;
    }

    public void setRules(List<RuleIntegration> rules) {
        this.rules = rules;
    }
}