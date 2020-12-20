package commands;

import static main.Main.coin;
import static main.Main.getMemberId;
import static main.Main.inGeneral;
import static main.Main.tagMember;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * display a member's balance, if no member is tagged it will display the
 * calling member's balance command syntax: ?bal/?bal @person alternatives:
 * ?balance ?cash ?money
 *
 */
public class Balance extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;

	String msg = event.getMessage().getContentRaw();
	if (!msg.toLowerCase().startsWith("?balance") && !msg.toLowerCase().startsWith("?bal")
		&& !msg.toLowerCase().startsWith("?cash") && !msg.toLowerCase().startsWith("?money"))
	    return;

	String[] parts = msg.split(" ");
	long member = event.getMember().getIdLong();

	if (parts.length > 1)
	    member = getMemberId(parts[1]);

	event.getChannel()
		.sendMessage(tagMember(member) + ": **" + Database.database().getBalance(member) + "**" + coin).queue();
    }
    

}
