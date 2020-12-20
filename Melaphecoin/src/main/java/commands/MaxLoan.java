package commands;
import static main.MainClass.coin;
import static main.MainClass.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MaxLoan extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (event.getMessage().getContentDisplay().equalsIgnoreCase("?maxloan")
		|| event.getMessage().getContentDisplay().equalsIgnoreCase("?ml"))
	    event.getChannel().sendMessage(tagMember(event.getMember().getIdLong()) + ": **"
		    + Database.DATABASE.maxLoanSize(event.getMember().getIdLong()) + coin + "**").queue();
    }
}
