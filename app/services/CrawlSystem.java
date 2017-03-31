package services;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import play.Logger;
import play.libs.ws.WSClient;
import scala.concurrent.duration.Duration;
import services.actors.CrawlActor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pooja Mahapatra on 29/03/17 11:25 PM.
 */
@Singleton
public class CrawlSystem {

    WSClient wsClient;

    ProductService productService;

    @Inject
    public CrawlSystem(WSClient wsClient, ProductService productService) {
        this.wsClient = wsClient;
        this.productService = productService;
        ActorSystem crawlSystem = ActorSystem.create("Crawler");
        Props crawlActorProp = Props.create(CrawlActor.class, wsClient, productService);
        ActorRef crawlActorRef = crawlSystem.actorOf(crawlActorProp,
                "crawlActor");
        Logger.info("Crawler Initiated");
        crawlSystem.scheduler().schedule(Duration.create(30,TimeUnit.SECONDS),
                Duration.create(30, TimeUnit.SECONDS),
                crawlActorRef, "run ",
                crawlSystem.dispatcher(), null);
    }

}
