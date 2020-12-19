package commands;

import static main.MainClass.coin;
import static main.MainClass.getGuild;
import static main.MainClass.getMemberById;
import static main.MainClass.getUserById;
import static main.MainClass.inGeneral;
import static main.MainClass.tagMember;

import java.util.ArrayList;

import database.Database;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * plays a game of rock paper scissors on a set bet command syntax: ?rps
 * amount @member in private chat with bot: react with emote to message from bot
 * 
 */
public class RockPaperScissors extends ListenerAdapter {

    private final ArrayList<Game> games = new ArrayList<Game>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
	if (inGeneral(event))
	    return;

	String msg = event.getMessage().getContentRaw();
	if (!msg.toLowerCase().startsWith("?rps"))
	    return;

	String[] parts = msg.split(" ");
	if (parts.length < 3)
	    return;

	String outMessage = "";

	long player1 = event.getMember().getIdLong();
	long player2 = Long.valueOf(parts[2].substring(3, parts[2].length() - 1));
	int gameIndex = indexOf(player1, player2);

	if ("accept".equalsIgnoreCase(parts[1])) {
	    if (gameIndex == -1)
		outMessage = "There is no game to start!";
	    else {
		games.get(gameIndex).startGame();
		outMessage = "Started a game between: " + tagMember(player1) + " and: " + tagMember(player2);
	    }
	} else {

	    int amount = Integer.valueOf(parts[1]);

	    if (amount < 0) {
		event.getChannel().sendMessage("Please enter a positive number").queue();
		return;
	    }

	    Database db = Database.database();
	    boolean canPlay = false;

	    if (db.getBalance(player1) < amount)
		outMessage = tagMember(player1) + " can't afford a bet of: **" + amount + coin + "**";
	    else if (db.getBalance(player2) < amount)
		outMessage = tagMember(player2) + " can't afford a bet of: **" + amount + coin + "**";
	    else
		canPlay = true;
	    if (canPlay) {
		if (gameIndex == -1) {
		    games.add(new Game(player1, player2, amount, event.getChannel()));
		    outMessage = "Started a game of rock paper scissors between: " + tagMember(player1) + " and "
			    + tagMember(player2) + " for:** " + amount + coin + "**, waiting for: ?rps accept "
			    + tagMember(player1);

		} else {
		    canPlay = true;
		    Game oldGame = games.remove(gameIndex);
		    outMessage = "Replaced a game of rock paper scissors between: " + tagMember(player1) + " and "
			    + tagMember(player2) + " for:** " + oldGame.amount + coin + "** with: **" + amount + coin
			    + "**, waiting for: ?rps accept " + tagMember(player1);
		    games.add(new Game(player1, player2, amount, event.getChannel()));
		}

	    }
	}
	event.getChannel().sendMessage(outMessage).queue();
    }

    @Override
    public void onPrivateMessageReactionAdd(PrivateMessageReactionAddEvent event) {
	// take care of the reactions, which are the player's picks
	long player1 = event.getUser().getIdLong();
	if (player1 == 788841586262802452l) // the bot
	    return;

	String msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getContentDisplay();
	String name = msg.substring(msg.indexOf(":") + 2);

	long player2 = getGuild().getMembersByEffectiveName(name, false).get(0).getIdLong();

	int choice = -1;
	String emote = event.getReactionEmote().getAsCodepoints();
	if ("U+1faa8".equals(emote)) // rock
	    choice = 0;
	else if ("U+1f4c4".equals(emote)) // paper
	    choice = 1;
	else if ("U+2702U+fe0f".equals(emote)) // scissors
	    choice = 2;

	int gameIndex = indexOf(player1, player2);
	if (gameIndex == -1)
	    return;

	Game game = games.get(gameIndex);
	if (game.player1 == player1)
	    game.setPlayer1Choice(choice);
	else
	    game.setPlayer2Choice(choice);
    }

    /**
     * Index of a game in the list
     * 
     * @param player1 - player1 id
     * @param player2 - player2 id
     * @return index, -1 if not in list
     */
    private int indexOf(long player1, long player2) {
	for (int i = 0; i < games.size(); i++) {
	    if ((games.get(i).player1 == player1 && games.get(i).player2 == player2)
		    || (games.get(i).player1 == player2 && games.get(i).player2 == player1))
		return i;
	}
	return -1;
    }

    /**
     * Holder object for a rock paper scissors game
     */
    private class Game {
	public long player1;
	public long player2;
	public int amount;

	// 0 - rock, 1 - paper, 2 - scissors
	private int player1Choice;
	private int player2Choice;

	private MessageChannel printChannel;

	public Game(long player1, long player2, int amount, MessageChannel printChannel) {
	    this.player1 = player1;
	    this.player2 = player2;
	    this.printChannel = printChannel;
	    this.amount = amount;

	    player1Choice = -1;
	    player2Choice = -1;
	}

	/**
	 * Set the choice for player1
	 * 
	 * @param player1Choice 0/1/2 - rock/paper/scissors
	 */
	public void setPlayer1Choice(int player1Choice) {
	    if (this.player1Choice != -1)
		return;
	    this.player1Choice = player1Choice;

	    if (player2Choice != -1)
		processGame();
	}

	/**
	 * Set the choice for player2
	 * 
	 * @param player1Choice 0/1/2 - rock/paper/scissors
	 */
	public void setPlayer2Choice(int player2Choice) {
	    if (this.player2Choice != -1)
		return;
	    this.player2Choice = player2Choice;

	    if (player1Choice != -1)
		processGame();
	}

	/**
	 * Starts the game, sends the private messages to the players
	 */
	public void startGame() {
	    PrivateChannel c1 = getUserById(player1).openPrivateChannel().complete();
	    c1.sendMessage(
		    "Reply to this message with your pick, playing vs: " + getMemberById(player2).getEffectiveName())
		    .queue(message -> {
			message.addReaction("🪨").queue();
			message.addReaction("📄").queue();
			message.addReaction("✂️").queue();
		    });

	    PrivateChannel c2 = getUserById(player2).openPrivateChannel().complete();
	    c2.sendMessage(
		    "Reply to this message with your pick, playing vs: " + getMemberById(player1).getEffectiveName())
		    .queue(message -> {
			message.addReaction("🪨").queue();
			message.addReaction("📄").queue();
			message.addReaction("✂️").queue();
		    });
	}

	/**
	 * Decides the winner and acts accordingly
	 */
	private void processGame() {
	    Database db = Database.database();
	    String outMessage = tagMember(player1) + " picked: " + choiceToString(player1Choice) + " "
		    + tagMember(player2) + " picked: " + choiceToString(player2Choice) + ", ";

	    int delta = player1Choice - player2Choice;
	    if (delta == 0)
		outMessage += "tie!";
	    else {
		if (delta == -1 || delta == 2) { // 2 win, swap the players
		    long temp = player1;
		    player1 = player2;
		    player2 = temp;
		}

		if (db.transfer(player2, player1, amount))
		    outMessage += tagMember(player1) + " won: **" + amount + coin + "**";
		else
		    outMessage += tagMember(player2) + " can't pay :(";
	    }

	    printChannel.sendMessage(outMessage).queue();
	    games.remove(indexOf(player1, player2));
	}

	/**
	 * Converts string of choice to number
	 * 
	 * @param choice - rock/paper/scissors
	 * @return 0/1/2
	 */
	private String choiceToString(int choice) {
	    switch (choice) {
	    case 0:
		return ":rock:";
	    case 1:
		return ":page_facing_up:";
	    case 2:
		return ":scissors:";
	    }
	    return null;
	}
    }
}
