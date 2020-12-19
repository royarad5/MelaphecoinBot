package commands;

import static main.MainClass.coin;
import static main.MainClass.inGeneral;
import static main.MainClass.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * transfers money from one member to another syntax: ?give amount also:
 * ?transfer
 */
public class Transfer extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;
	String msg = event.getMessage().getContentRaw();
	if (!msg.toLowerCase().startsWith("?give") && !msg.toLowerCase().startsWith("?transfer"))
	    return;

	if (Database.DATABASE.getDebtSize(event.getAuthor().getIdLong()) != 0) {
	    event.getChannel().sendMessage("You can't transfer money while in debt").queue();
	    return;
	}

	String[] parts = msg.split(" ");

	System.out.println(msg);
	System.out.println(parts[0]);
	System.out.println(parts[1]);
	System.out.println(parts[2]);

	try {
	    int amount = Integer.valueOf(parts[1]);
	    long target = Long.valueOf(parts[2].substring(2, parts[2].length() - 1));
	    long giver = event.getMember().getIdLong();

	    if (amount < 0) {
		event.getChannel().sendMessage("Please enter a positive number").queue();
		return;
	    }

	    if (Database.database().transfer(giver, target, amount))
		event.getChannel().sendMessage("Transferred: **" + amount + coin + "** from: " + tagMember(giver)
			+ " to: " + tagMember(target)).queue();
	    else
		event.getChannel().sendMessage(tagMember(giver) + " can't give: **" + amount + coin + "**").queue();
	} catch (Exception e) {
	    event.getChannel().sendMessage("Error while processing the command").queue();
	    e.printStackTrace();
	}
    }
}
