package commands;

import database.Database;
import main.MainClass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Balance extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (!"?balance".equalsIgnoreCase(event.getMessage().getContentDisplay()))
	    return;

	event.getChannel()
		.sendMessage("**" + Database.database().read(event.getMember().getIdLong()) + "**" + MainClass.coin)
		.queue();
    }

}
