package main;

import javax.security.auth.login.LoginException;

import commands.Balance;
import commands.Coinflip;
import database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class MainClass {

    private static final String TOKEN = "Nzg4ODQxNTg2MjYyODAyNDUy.X9pX-g.CqtbEgPOgqACzWvato7rsGTxOrg";

    public static Emote melaphecoin = null;
    public static String coin = null;

    public static void main(String[] args) throws LoginException, InterruptedException {
	JDA jda = JDABuilder.create(TOKEN, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();
	jda.addEventListener(new Coinflip());
	jda.addEventListener(new Balance());
	Database.database(); // boot the database

	jda.awaitReady();
	melaphecoin = jda.getGuildById("699728425941991566").getEmotesByName("Melaphecoin", false).get(0);
	coin = " " + melaphecoin.getAsMention();
    }
}
