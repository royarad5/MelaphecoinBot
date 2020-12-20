package commands;

import static main.Main.coin;
import static main.Main.inGeneral;
import static main.Main.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * display the member's debt syntax: ?debt
 * 
 */
public class Debt extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;

	if (event.getMessage().getContentDisplay().startsWith("?debt"))
	    event.getChannel().sendMessage(tagMember(event.getAuthor().getIdLong()) + " has a debt of: **"
		    + Database.database().getDebtSize(event.getAuthor().getIdLong()) + coin + "**").queue();
    }
}
