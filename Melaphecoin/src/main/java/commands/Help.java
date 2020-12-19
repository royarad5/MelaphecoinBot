package commands;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Help extends ListenerAdapter {

    private static final String HELP_MESSAGE = "```?bal - will show you the balance in your account.\r\n" + "\r\n"
	    + "?bal @person - will show you the balance of the person you tag.\r\n" + "\r\n"
	    + "?cf *amount* @person - coinflip between you and the person you tagged for a specific amount of money.\r\n"
	    + "\r\n" + "?ds - daily spin which will be available once every day for free.\r\n" + "\r\n"
	    + "?ds buy - after using your daily spin you will be able to buy another daily spin for 75 coins by using this command.\r\n"
	    + "\r\n" + "?give *amount* @person - will give the person you tag a specific amount of money.\r\n" + "\r\n"
	    + "?help - display this message.\r\n" + "\r\n"
	    + "?leaderboard - print the top 5 richest members of the server\r\n" + "\r\n"
	    + "?rps *amount* @person - rock paper scissors. You will be sent a message from the bot and you will need to react with one of the three options.\r\n"
	    + "\r\n"
	    + "?rt *amount* *color* (red, black or green) - roulette. can choose from red(x2), black(x2) and green(x14). If you win your money will be multiplied by the numbers next to the color.\r\n"
	    + "\r\n" + "alternative ways of writing the commands:\r\n" + "?bal = ?balance, ?cash, ?money\r\n"
	    + "?cf = ?coinflip\r\n" + "?ds = ?dailyspin\r\n" + "?rt = ?roulette\r\n" + "?give = ?transfer```";

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (event.getMessage().getContentDisplay().toLowerCase().startsWith("?help")) {
	    PrivateChannel c1 = event.getMember().getUser().openPrivateChannel().complete();
	    c1.sendMessage(HELP_MESSAGE);
	}
    }

}
