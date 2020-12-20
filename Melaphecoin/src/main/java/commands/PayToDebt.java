package commands;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static main.Main.*;

/**
 * pays to fill the member's debt syntax: ?ptd *amount* also: ?paytodebt
 */
public class PayToDebt extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;
	String msg = event.getMessage().getContentDisplay();

	if (!msg.toLowerCase().startsWith("?payToDebt") && !msg.toLowerCase().startsWith("?ptd"))
	    return;

	String outMessage = "";
	try {
	    String[] parts = msg.split(" ");
	    int amount = Integer.valueOf(parts[1]);
	    if (amount < 0)
		outMessage = "Please insert a positive number";
	    else {
		long memberId = event.getAuthor().getIdLong();

		int payment = Database.DATABASE.payToDebt(memberId, amount);
		outMessage = tagMember(memberId) + "\npaid: **" + payment + coin + "**\ndebt: **"
			+ Database.DATABASE.getDebtSize(memberId) + coin + "**\nBalance:** "
			+ Database.DATABASE.getBalance(memberId) + coin + "**";
	    }

	} catch (Exception ignored) {
	    outMessage = "Error trying to process the command";
	}

	event.getChannel().sendMessage(outMessage).queue();
    }
}
