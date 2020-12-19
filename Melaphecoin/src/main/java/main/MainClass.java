package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import javax.security.auth.login.LoginException;

import commands.Balance;
import commands.Coinflip;
import commands.DailySpin;
import commands.Debt;
import commands.ForceSave;
import commands.Help;
import commands.Leaderboard;
import commands.Loan;
import commands.Overview;
import commands.PayToDebt;
import commands.RockPaperScissors;
import commands.Roulette;
import commands.Sex;
import commands.Transfer;
import database.Database;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import passiveTasks.CountingToFine;

public class MainClass {

    private static final String TOKEN_FILE = "C:\\Users\\almog\\Desktop\\token.txt";

    public static final long MALOSH_ID = 699728425941991566l;
    public static Emote melaphecoin = null;
    public static String coin = null;

    public static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
	String token = Files.readAllLines(new File(TOKEN_FILE).toPath(), Charset.defaultCharset()).get(0);

	jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();

	jda.awaitReady();

	jda.addEventListener(new Coinflip());
	jda.addEventListener(new Balance());
	jda.addEventListener(new RockPaperScissors());
	jda.addEventListener(new DailySpin());
	jda.addEventListener(new Roulette());
	jda.addEventListener(new Transfer());
	jda.addEventListener(new ForceSave());
	jda.addEventListener(new Help());
	jda.addEventListener(new Leaderboard());
	jda.addEventListener(new Debt());
	jda.addEventListener(new Loan());
	jda.addEventListener(new PayToDebt());
	jda.addEventListener(new Overview());
	jda.addEventListener(new Sex());

	jda.addEventListener(new CountingToFine());
	Database.database(); // boot the database

	melaphecoin = getGuild().getEmotesByName("Melaphecoin", false).get(0);
	coin = " " + melaphecoin.getAsMention();
    }

    public static String tagMember(long memberId) {
	return getMemberById(memberId).getAsMention();
    }

    public static User getUserById(long userId) {
	return jda.getUserById(userId);
    }

    public static Member getMemberById(long memberId) {
	return getGuild().getMemberById(memberId);
    }

    public static Guild getGuild() {
	return jda.getGuildById(MALOSH_ID);
    }
}
