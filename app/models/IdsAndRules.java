package models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Created by pooja_mahapatra on 28/03/17.
 */
public class IdsAndRules {
    @Id
    private ObjectId id;

    private Long prodId;

    private String logicalOperator;

    private List<RuleIntegration> ruleIntegrations;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public List<RuleIntegration> getRuleIntegrations() {
        return ruleIntegrations;
    }

    public void setRuleIntegrations(List<RuleIntegration> ruleIntegrations) {
        this.ruleIntegrations = ruleIntegrations;
    }

    public String getLogicalOperator() {
        return logicalOperator;
    }

    public void setLogicalOperator(String logicalOperator) {
        this.logicalOperator = logicalOperator;
    }
}
