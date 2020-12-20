package main;

import java.io.IOException;
import java.util.Scanner;

import javax.security.auth.login.LoginException;

import commands.Balance;
import commands.Coinflip;
import commands.DailySpin;
import commands.Debt;
import commands.ForceSave;
import commands.Help;
import commands.Leaderboard;
import commands.Loan;
import commands.MaxLoan;
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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import passiveTasks.CountingToManager;

/**
 * class containing the main method, also contains helper function and variables
 */
public class MainClass {

    public static final long OWNER = 402166510795227139l;
    
    public static long GENERAL_CHAT = 0;
    public static final long MALOSH_ID = 699728425941991566l;
    public static Emote melaphecoin = null;
    public static String coin = null;

    public static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
	Scanner scanner = new Scanner(System.in);
	System.out.println("Enter Token:");
	String token = scanner.nextLine();
	scanner.close();
	
	KeepAliveServer kas = new KeepAliveServer();
	kas.start();
	
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
	jda.addEventListener(new MaxLoan());

	jda.addEventListener(new CountingToManager());
	Database.database(); // boot the database

	melaphecoin = getGuild().getEmotesByName("Melaphecoin", false).get(0);
	coin = " " + melaphecoin.getAsMention();
	GENERAL_CHAT = getGuild().getDefaultChannel().getIdLong();
    }

    public static long getMemberId(String rawId) {
	return Long.valueOf(rawId.replace("<", "").replace(">", "").replace("!", "").replace("@", "").replace("&", ""));
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

    public static boolean inGeneral(MessageReceivedEvent event) {
	return event.getChannel().getIdLong() == GENERAL_CHAT;
    }
}
