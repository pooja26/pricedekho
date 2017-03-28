package controllers;

import play.libs.mailer.Email;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;

/**
 * Created by Pooja Mahapatra on 26/03/17 5:49 PM.
 */
public class MailingClient extends Controller{

    @Inject
    MailerClient mailerClient;

    public Result sendEmail(){
        Email email = new Email()
                .setSubject("Testing Play")
                .setFrom("poojamhptr@gmail.com")
                .addTo("poojamhptr@gmail.com")
                .setBodyText("Hi...");
        mailerClient.send(email);
        return ok("Mail sent");
    }
}
