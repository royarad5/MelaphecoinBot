package passiveTasks;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static main.Main.*;

/**
 * tracks the fines for typing text in the counting to 20,000 text channel
 */
public class CountingToFine extends ListenerAdapter {

    private static final int FINE = 50;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (event.getChannel().getIdLong() != 711549257945186344l)
	    return;

	if (!isNumber(event.getMessage().getContentDisplay())) {
	    long memberId = event.getMember().getIdLong();

	    Database db = Database.database();
	    int fineSize = FINE;
	    if (db.subtract(memberId, FINE) == -1) {
		fineSize = db.getBalance(memberId);
		db.setBalance(memberId, 0);
	    }

	    getGuild().getDefaultChannel()
		    .sendMessage(tagMember(memberId)
			    + " typed something that is not a number in Counting to 20,000 and got fined for: **"
			    + fineSize + coin + "**")
		    .queue();
	}

    }

    /**
     * Check is the string is a number
     * 
     * @param num - string to test
     * @return true/false if the strign is a number
     */
    private static boolean isNumber(String num) {
	try {
	    Integer.valueOf(num);
	    return true;
	} catch (NumberFormatException ignored) {
	    return false;
	}
    }
}
