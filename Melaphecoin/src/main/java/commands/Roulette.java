package commands;

import database.Database;
import main.MainClass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Random;

public class Roulette extends ListenerAdapter {

    private Database database = Database.database();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	// ?r amount colour
	String msg = event.getMessage().getContentRaw();

	if (!msg.toLowerCase().startsWith("?roulette") && !msg.toLowerCase().startsWith("?rt"))
	    return;

	long memberId = event.getMember().getIdLong();
	String[] parts = msg.split(" ");

	if (parts.length > 2) {
	    int bet = Integer.parseInt(parts[1]);
	    if (database.subtract(event.getMember().getIdLong(), bet) == -1) {
		event.getChannel().sendMessage(tagMember(event, memberId) + " Insufficent funds").queue();
	    } else {
		int prize = roulette(event, parts[2], bet);
		if (prize == -1)
		    event.getChannel().sendMessage(tagMember(event, memberId) + " lost").queue();
		else if (prize == -2)
		    event.getChannel().sendMessage(tagMember(event, memberId) + " Please enter a valid color").queue();
		else {
		    event.getChannel()
			    .sendMessage(
				    tagMember(event, memberId) + " won: **" + (prize - bet) + MainClass.coin + "**")
			    .queue();
		    database.add(memberId, prize);
		}
	    }
	}

    }

    public int roulette(MessageReceivedEvent event, String color, int bet) {
	Random rand = new Random();

	int rand_int = rand.nextInt(29);
	int winningColor = -1; // 0-green, 1-black, 2-red
	if (rand_int == 0) {
	    winningColor = 0;
	    event.getChannel().sendMessage("Green").queue();
	} else if (rand_int <= 14) {
	    winningColor = 1;
	    event.getChannel().sendMessage("Black").queue();
	} else {
	    winningColor = 2;
	    event.getChannel().sendMessage("Red").queue();
	}

	if ("green".equalsIgnoreCase(color) || "g".equalsIgnoreCase(color)) {
	    if (winningColor == 0)
		return bet * 14; // Green - x14
	    else
		return -1;
	} else if ("black".equalsIgnoreCase(color) || "b".equalsIgnoreCase(color)) {
	    if (winningColor == 1)
		return bet * 2; // Black - x2
	    else
		return -1;
	} else if ("red".equalsIgnoreCase(color) || "r".equalsIgnoreCase(color)) {
	    if (winningColor == 2)
		return bet * 2;	// Red - x2
	    else
		return -1;
	}

	return -2;
    }
    
    private String tagMember(MessageReceivedEvent event, long memberId) {
	return event.getGuild().getMemberById(memberId).getAsMention();
    }
}
