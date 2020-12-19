package passiveTasks;

import static database.MessageDeletor.deleteMessage;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SelfDeletor extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (event.getAuthor().getIdLong() == 788841586262802452l)
	    deleteMessage(event.getChannel().getIdLong(), event.getMessage().getIdLong());
    }
}
