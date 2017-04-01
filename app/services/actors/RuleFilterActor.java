package services.actors;

import akka.actor.UntypedActor;
import services.MailingService;
import models.IdsAndRules;
import models.Product;
import models.RuleIntegration;
import models.User;
import org.mongodb.morphia.query.Query;
import services.UserService;
import util.MongoConfig;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pooja Mahapatra on 01/04/17 11:47 AM.
 */
public class RuleFilterActor extends UntypedActor {

    UserService userService;

    MailingService mailingService;

    @Inject
    public RuleFilterActor(UserService userService, MailingService mailingService) {
        this.userService = userService;
        this.mailingService = mailingService;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        List<User> userList = userService.getAllUsers();
        userList.forEach(obj -> {
            List<IdsAndRules> idsAndRules;
            Map<Long,List<RuleIntegration>> map = new HashMap<>();
            idsAndRules = obj.getIdsAndRules();
            for(IdsAndRules join: idsAndRules){
                map.put(join.getProdId(),join.getRuleIntegrations());
            }
            List<Product> userProducts = new ArrayList<>();
            for(Map.Entry<Long,List<RuleIntegration>> entry: map.entrySet()){
                Long pId = entry.getKey();
                List<RuleIntegration> ruleIntegration = entry.getValue();
                Query<Product> queryProd = MongoConfig.getDB().createQuery(Product.class).disableValidation();
                queryProd.field("id").equal(pId);
                for(RuleIntegration rules: ruleIntegration) {
                    String key = rules.getKey();
                    String operator = rules.getOperator();
                    String condition = key + " " + operator;
                    Double value = rules.getValue();
                    queryProd.filter(condition, value);
                }

                Product product = queryProd.get();
                if(product != null){
                    userProducts.add(product);
                }
            }
            if(userProducts.size() != 0) {
                mailingService.sendEmail(obj.getEmailId(),userProducts);
            }
        });

    }
}
