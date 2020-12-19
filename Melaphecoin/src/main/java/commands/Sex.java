package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Sex extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (event.getMessage().getContentDisplay().toLowerCase().startsWith("?sex"))
	    event.getChannel().sendMessage("Yes").queue();
    }
}
