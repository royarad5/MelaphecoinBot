package commands;

import static main.MainClass.coin;
import static main.MainClass.inGeneral;
import static main.MainClass.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * takes a loan syntax: ?loan *amount*
 * 
 */
public class Loan extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;
	String msg = event.getMessage().getContentDisplay();
	if (!msg.toLowerCase().startsWith("?loan"))
	    return;

	String outMessage = "";
	try {
	    String[] parts = msg.split(" ");
	    int amount = Integer.valueOf(parts[1]);
	    if (amount < 0)
		outMessage = "Please insert a positive number";
	    else {
		long memberId = event.getAuthor().getIdLong();
		int loan = Database.DATABASE.takeLoan(memberId, amount);
		if (loan == 0) {
		    outMessage = tagMember(memberId) + " took a loan of: **" + amount + coin
			    + "** and will need to return:** " + (int) (amount * 1.05) + coin + "**";
		} else if (loan == -1) {
		    outMessage = tagMember(memberId) + " can't afford a loan of: **" + amount + coin
			    + "**, maximum loan size:** " + Database.DATABASE.maxLoanSize(memberId) + coin + "**";
		} else {
		    outMessage = tagMember(memberId) + " already has a debt of:** " + loan + coin + "**";
		}
	    }
	} catch (Exception ignored) {
	    outMessage = "Error trying to process the command";
	}

	event.getChannel().sendMessage(outMessage).queue();
    }
}
