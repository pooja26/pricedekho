package services.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import models.CrawlProduct;
import scala.concurrent.duration.Duration;
import services.ProductService;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by pooja_mahapatra on 25/3/17.
 */
@Singleton
public class ScheduledCrawler {

    private Cancellable cancelCrawl = null;

    public ScheduledCrawler() {
        scheduleCrawl();
    }

    private static void scheduleCrawl() {
        System.out.println("schedule crawl initiated");
        ActorSystem crawlSystem = ActorSystem.create("Crawler");
        ActorRef crawlActorRef = crawlSystem.actorOf(Props.create(CrawlActor.class),
                "crawlActor");
        crawlSystem.scheduler().schedule(Duration.Zero(),
                Duration.create(30, TimeUnit.SECONDS),
                crawlActorRef, "start",
                crawlSystem.dispatcher(), null);
    }

    private void unscheduleCrawl() {
            if(cancelCrawl != null && !cancelCrawl.isCancelled())
                cancelCrawl.cancel();
    }

}
