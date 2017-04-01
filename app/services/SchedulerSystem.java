package services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import play.Logger;
import play.libs.ws.WSClient;
import scala.concurrent.duration.Duration;
import services.actors.CrawlActor;
import services.actors.RuleFilterActor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pooja Mahapatra on 29/03/17 11:25 PM.
 */
@Singleton
public class SchedulerSystem {

    WSClient wsClient;

    ProductService productService;

    UserService userService;

    MailingService mailingService;

    @Inject
    public SchedulerSystem(WSClient wsClient, ProductService productService, UserService userService, MailingService mailingService) {
        this.wsClient = wsClient;
        this.productService = productService;
        this.userService = userService;
        this.mailingService = mailingService;
        ActorSystem actorSystem = ActorSystem.create("Crawler");
        Props crawlActorProp = Props.create(CrawlActor.class, wsClient, productService);
        Props ruleFilterActorProp = Props.create(RuleFilterActor.class, userService, mailingService);
        ActorRef crawlActorRef = actorSystem.actorOf(crawlActorProp,
                "crawlActor");
        ActorRef ruleFilterActorRef = actorSystem.actorOf(ruleFilterActorProp,
                "ruleFilterActor");
        Logger.info("Crawler Initiated");
        actorSystem.scheduler().schedule(Duration.create(30,TimeUnit.SECONDS),
                Duration.create(30, TimeUnit.SECONDS),
                crawlActorRef, "run ",
                actorSystem.dispatcher(), null);
        actorSystem.scheduler().schedule(Duration.create(10,TimeUnit.SECONDS),
                Duration.create(30, TimeUnit.SECONDS),
                ruleFilterActorRef, "run ",
                actorSystem.dispatcher(), null);

    }

}
