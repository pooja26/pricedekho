package services;

import models.Product;
import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.twirl.api.Html;
import scala.collection.mutable.StringBuilder;
import views.html.application.filteredproduct;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Pooja Mahapatra on 26/03/17 5:49 PM.
 */
public class MailingService {

    MailerClient mailerClient;

    @Inject
    public MailingService(MailerClient mailerClient) {
        this.mailerClient = mailerClient;
    }

    public void sendEmail(String emailId, List<Product> productList){
        Html content  = filteredproduct.render(emailId,productList);
        StringBuilder emailContent = new StringBuilder();
        content.buildString(emailContent);
        Email email = new Email()
                .setSubject("Priceous Product List")
                .setFrom("bunty.hari@gmail.com")
                .addTo(emailId)
                .setBodyHtml(emailContent.toString());
        mailerClient.send(email);
    }
}
