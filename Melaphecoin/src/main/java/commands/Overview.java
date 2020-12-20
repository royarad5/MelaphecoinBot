package commands;

import static main.MainClass.coin;
import static main.MainClass.inGeneral;
import static main.MainClass.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * shows the member's balance and debt syntax: ?ov / ?overview
 */
public class Overview extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;
	if (event.getMessage().getContentDisplay().toLowerCase().startsWith("?overview")
		|| event.getMessage().getContentDisplay().toLowerCase().startsWith("?ov")) {
	    long memberId = event.getAuthor().getIdLong();

	    event.getChannel().sendMessage(tagMember(memberId) + "\ndebt: **" + Database.DATABASE.getDebtSize(memberId)
		    + coin + "**\nBalance:** " + Database.DATABASE.getBalance(memberId) + coin + "**").queue();
	}
    }
}
