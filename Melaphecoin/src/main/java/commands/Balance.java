package commands;

import database.Database;
import main.MainClass;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Balance extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	String msg = event.getMessage().getContentRaw();
	if (!msg.startsWith("?balance") && !msg.startsWith("?bal") && !msg.startsWith("?cash"))
	    return;

	String[] parts = msg.split(" ");
	long member = event.getMember().getIdLong();

	if (parts.length > 1)
	    member = Long.valueOf(parts[1].substring(3, parts[1].length() - 1));

	event.getChannel()
		.sendMessage(tagMember(member) + ": **" + Database.database().read(member) + "**" + MainClass.coin)
		.queue();
    }

    private String tagMember(long memberId) {
	return MainClass.jda.getGuildById(MainClass.MALOSH_ID).getMemberById(memberId).getAsMention();
    }
}
