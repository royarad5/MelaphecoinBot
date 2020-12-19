package commands;

import static main.MainClass.coin;
import static main.MainClass.tagMember;

import java.util.Random;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DailySpin extends ListenerAdapter {

    private Database database = Database.database();

    public void spinWheel(MessageReceivedEvent event) {
	long memberId = event.getMember().getIdLong();

	Random rand = new Random();
	int randInt = rand.nextInt(10011);

	int prize = -1;

	if (randInt == 0)
	    prize = 5000;
	else if (randInt <= 500)
	    prize = 100;
	else if (randInt <= 510)
	    prize = 1000;
	else if (randInt <= 4510)
	    prize = 5;
	else if (randInt <= 6010)
	    prize = 50;
	else if (randInt <= 7010)
	    prize = 75;
	else
	    prize = 25;

	database.add(memberId, prize);
	event.getChannel().sendMessage(tagMember(memberId) + " won: **" + prize + coin + "**").queue();;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	String msg = event.getMessage().getContentRaw();
	if (!msg.toLowerCase().startsWith("?dailyspin") && !msg.toLowerCase().startsWith("?ds"))
	    return;

	String[] parts = msg.split(" ");

	if (parts.length > 1 && "buy".equals(parts[1])) {
	    if (database.subtract(event.getMember().getIdLong(), 75) == -1)
		event.getChannel().sendMessage("Insufficent funds").queue();
	    else
		spinWheel(event);
	} else {
	    if (database.hasFreeSpin(event.getMember().getIdLong())) {
		database.useFreeSpin(event.getMember().getIdLong());
		spinWheel(event);
	    } else
		event.getChannel().sendMessage("You've already used your free spin today").queue();
	}
    }
}
