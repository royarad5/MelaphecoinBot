package commands;

import static main.MainClass.coin;
import static main.MainClass.inGeneral;
import static main.MainClass.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Debt extends ListenerAdapter{

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;
	
        if (event.getMessage().getContentDisplay().startsWith("?debt"))
            event.getChannel().sendMessage(tagMember(event.getAuthor().getIdLong()) + " has a debt of: **" + Database.database().getDebtSize(event.getAuthor().getIdLong()) + coin + "**").queue();
    }
}
