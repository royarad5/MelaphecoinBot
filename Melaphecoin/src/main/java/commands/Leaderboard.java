package commands;

import static main.MainClass.tagMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Leaderboard extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (!event.getMessage().getContentDisplay().toLowerCase().startsWith("?leaderboard")
		&& !event.getMessage().getContentDisplay().toLowerCase().startsWith("?lb"))
	    return;
	
	ConcurrentHashMap<Long, Integer> balances = Database.database().getBalances();

	List<Entry<Long, Integer>> list = new ArrayList<>(balances.entrySet());
	list.sort(Entry.comparingByValue());
	Collections.reverse(list);

	String outMessage = "";
	for (int i = 0; i < 5; i++) {
	    outMessage += tagMember(list.get(i).getKey()) + ": " + list.get(i).getValue() + "\n";
	}

	event.getChannel().sendMessage(outMessage).queue();
    }

}
