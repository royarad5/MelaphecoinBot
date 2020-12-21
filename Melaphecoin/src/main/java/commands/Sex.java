package commands;
import static main.MainClass.inGeneral;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * yes
 */
public class Sex extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;
	if (event.getMessage().getContentDisplay().toLowerCase().startsWith("?sex")) {
	    event.getChannel().sendMessage("Yes").queue();
	    event.getMember().mute(false).queue();
	}
    }
}
