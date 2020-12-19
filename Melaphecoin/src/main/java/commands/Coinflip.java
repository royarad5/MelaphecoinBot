package commands;

import static main.MainClass.coin;
import static main.MainClass.tagMember;

import java.util.ArrayList;

import database.Database;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Coinflip extends ListenerAdapter {

    private Database database = Database.database();
    /** Holds all of the current bets */
    private final ArrayList<CoinflipData> flips = new ArrayList<CoinflipData>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	// command structure: (?coinflip can be replaced with ?cf)
	// ?coinflip amount @target
	// ?coinflip accept @target

	String msg = event.getMessage().getContentRaw();
	if (!msg.toLowerCase().startsWith("?coinflip") && !msg.toLowerCase().startsWith("?cf"))
	    return;

	String[] parts = msg.split(" ");
	if (parts.length < 3)
	    return;

	String outMessage = "";
	try {
	    if ("accept".equals(parts[1])) {
		long player1 = Long.valueOf(parts[2].substring(3, parts[2].length() - 1));
		long player2 = event.getMember().getIdLong();

		int flipIndex = indexOf(player1, player2);
		if (flipIndex == -1)
		    outMessage = tagMember(player1) + " and " + tagMember(player2)
			    + " don't have a bet together";
		else {
		    CoinflipData placedFlip = flips.remove(flipIndex);

		    // we have a 50% chance to flip the players instead of writing the code twice
		    if ((int) (Math.random() * 2) % 2 == 0) {
			long temp = player1;
			player1 = player2;
			player2 = temp;
		    }

		    boolean success = database.transfer(player2, player1, placedFlip.amount);
		    if (success)
			outMessage = tagMember(player1) + " won: **" + placedFlip.amount + coin + "**";
		    else
			outMessage = tagMember(player1) + " won, but " + tagMember(player2)
				+ " can't pay up, bet cancelled";
		}
	    } else {
		long player1 = event.getMember().getIdLong();
		long player2 = Long.valueOf(parts[2].substring(3, parts[2].length() - 1));
		int amount = Integer.valueOf(parts[1]);

		if (database.getBalance(player1) < amount)
		    outMessage = tagMember(player1) + " can't afford a bet of: **" + amount + coin
			    + "**";
		else if (database.getBalance(player2) < amount)
		    outMessage = tagMember(player2) + " can't afford a bet of: **" + amount +coin
			    + "**";
		else {

		    int flipIndex = indexOf(player1, player2);
		    if (flipIndex == -1) {
			flips.add(new CoinflipData(player1, player2, amount));

			outMessage = "Placed a bet for: **" + amount + coin + "** between "
				+ tagMember(player1) + " and " + tagMember(player2)
				+ " waiting for: ?cf accept " + tagMember(player2);
		    } else {
			CoinflipData placedFlip = flips.remove(flipIndex);
			flips.add(new CoinflipData(player1, player2, amount));

			outMessage = "Replaced a bet between " + tagMember(player1) + " and "
				+ tagMember(player2) + " for: **" + placedFlip.amount + coin
				+ "** with: **" + amount + coin + "** waiting for: ?cf accept "
				+ tagMember(player2);
			;
		    }
		}
	    }
	} catch (Exception ignored) {
	    outMessage = "Error trying to process the command";
	}

	event.getChannel().sendMessage(outMessage).queue();
    }

    /**
     * Returns the index of a bet inside the {@link #flips} list
     * 
     * @param player1 - player1 id
     * @param player2 - player2 id
     * @return - index in the list, -1 if bet is not there
     */
    private int indexOf(long player1, long player2) {
	for (int i = 0; i < flips.size(); i++) {
	    CoinflipData flip = flips.get(i);
	    if (flip.player1 == player1 && flip.player2 == player2)
		return i;
	}
	return -1;
    }

    /**
     * Holds all of the data required for a flip
     */
    private class CoinflipData {
	public long player1;
	public long player2;
	public int amount;

	public CoinflipData(long player1, long player2, int amount) {
	    this.player1 = player1;
	    this.player2 = player2;
	    this.amount = amount;
	}
    }
}