package services.actors;

import akka.actor.UntypedActor;
import models.IdsAndRules;
import models.Product;
import models.RuleIntegration;
import models.User;
import org.apache.commons.lang3.time.DateUtils;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import play.Logger;
import services.MailingService;
import services.UserService;
import util.LogicalOperator;
import util.MongoConfig;
import util.RelationalOperator;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Pooja Mahapatra on 01/04/17 11:47 AM.
 */
public class RuleFilterActor extends UntypedActor {

    private UserService userService;

    private MailingService mailingService;

    private DateUtils dateUtils;

    @Inject
    public RuleFilterActor(UserService userService, MailingService mailingService,
                           DateUtils dateUtils) {
        this.userService = userService;
        this.mailingService = mailingService;
        this.dateUtils = dateUtils;

    }

    @Override
    public void onReceive(Object message) throws Throwable {
        List<User> userList = userService.getAllUsers();
        userList.forEach(user -> {
            List<IdsAndRules> idsAndRules;
            idsAndRules = user.getIdsAndRules();
            String emailId = user.getEmailId();
            List<Product> userProducts = new ArrayList<>();
            for (IdsAndRules join : idsAndRules) {
                Long pId = join.getProdId();
                String op = join.getLogicalOperator();
                List<RuleIntegration> ruleIntegration = join.getRuleIntegrations();
                if (ruleIntegration == null) {
                    Logger.warn("No rules found for this product: {} by user {}",pId, emailId);
                    continue;
                }
                Query<Product> queryProd = MongoConfig.getDB().createQuery(Product.class).disableValidation();
                List<Criteria> criteriaList = new ArrayList<>();
                Date date = new Date();
                Long startTime = dateUtils.truncate(date, Calendar.DAY_OF_MONTH).getTime();
                Long endTime = dateUtils.ceiling(date, Calendar.DAY_OF_MONTH).getTime();
                queryProd.field("id").equal(pId);
                queryProd.field("timestamp").greaterThanOrEq(startTime);
                queryProd.field("timestamp").lessThanOrEq(endTime);
                for (RuleIntegration rules : ruleIntegration) {
                    String key = rules.getKey();
                    String operator = rules.getOperator();
                    Double value = rules.getValue();
                    Criteria criteria = null;
                    if (RelationalOperator.fromValue(operator) == RelationalOperator.EQUALS) {
                        criteria = queryProd.criteria(key).equal(value);
                    } else if (RelationalOperator.fromValue(operator) == RelationalOperator.GREATER_THAN) {
                        criteria = queryProd.criteria(key).greaterThan(value);
                    } else if (RelationalOperator.fromValue(operator) == RelationalOperator.GREATER_THAN_EQUAL) {
                        criteria = queryProd.criteria(key).greaterThanOrEq(value);
                    } else if (RelationalOperator.fromValue(operator) == RelationalOperator.LESS_THAN) {
                        criteria = queryProd.criteria(key).lessThan(value);
                    } else if (RelationalOperator.fromValue(operator) == RelationalOperator.LESS_THAN_EQUAL) {
                        criteria = queryProd.criteria(key).lessThanOrEq(value);
                    } else {
                        Logger.warn(operator + " not supported");
                    }
                    if (criteria != null) {
                        criteriaList.add(criteria);
                    }
                }
                Criteria criteriaArray[] = criteriaList.toArray(new Criteria[]{});
                if (LogicalOperator.fromValue(op) == LogicalOperator.AND)
                    queryProd.and(criteriaArray);
                else
                    queryProd.or(criteriaArray);
                Product product = queryProd.get();
                if (product != null) {
                    userProducts.add(product);
                }
            }
            if (userProducts.size() != 0) {
                mailingService.sendEmail(user.getEmailId(), userProducts);
            }
        });

    }
}
