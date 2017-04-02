package services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.apache.commons.lang3.time.DateUtils;
import play.Logger;
import play.libs.ws.WSClient;
import scala.concurrent.duration.Duration;
import services.actors.CrawlActor;
import services.actors.RuleFilterActor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pooja Mahapatra on 29/03/17 11:25 PM.
 */
@Singleton
public class SchedulerSystem {


    @Inject
    public SchedulerSystem(WSClient wsClient, ProductService productService, UserService userService,
                           MailingService mailingService, DateUtils dateUtils, Date date) {
        ActorSystem actorSystem = ActorSystem.create("Crawler");
        Props crawlActorProp = Props.create(CrawlActor.class, wsClient, productService, date);
        Props ruleFilterActorProp = Props.create(RuleFilterActor.class, userService, mailingService, dateUtils);
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
