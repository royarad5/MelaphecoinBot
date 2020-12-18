package commands;

import database.Database;
import main.MainClass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Transfer extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	String msg = event.getMessage().getContentRaw();
	if (!msg.startsWith("?give") && !msg.startsWith("?transfer"))
	    return;

	String[] parts = msg.split(" ");

	try {
	    int amount = Integer.valueOf(parts[1]);
	    long target = Long.valueOf(parts[2].substring(3, parts[2].length() - 1));
	    long giver = event.getMember().getIdLong();

	    if (Database.database().transfer(giver, target, amount))
		event.getChannel().sendMessage("Transferred: **" + amount + MainClass.coin + "** from: "
			+ tagMember(giver) + " to: " + tagMember(target)).queue();
	    else
		event.getChannel().sendMessage(tagMember(giver) + " can't give: **" + amount + MainClass.coin + "**")
			.queue();
	} catch (Exception e) {
	    event.getChannel().sendMessage("Error while processing the command").queue();
	}
    }

    private String tagMember(long memberId) {
	return MainClass.jda.getGuildById(MainClass.MALOSH_ID).getMemberById(memberId).getAsMention();
    }
}
