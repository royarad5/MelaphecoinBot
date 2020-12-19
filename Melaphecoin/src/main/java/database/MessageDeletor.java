package database;

import static main.MainClass.getGuild;

import java.util.concurrent.TimeUnit;

public class MessageDeletor extends Thread {

    private long channelId;
    private long messageId;

    public MessageDeletor(long channelId, long messageId) {
	this.channelId = channelId;
	this.messageId = messageId;

	// start();
    }

    @Override
    public void run() {
	try {
	    TimeUnit.MINUTES.sleep(10);
	} catch (InterruptedException ignored) {
	}

	getGuild().getTextChannelById(channelId).deleteMessageById(messageId).queue();
    }

    public static void deleteMessage(long channelId, long messageId) {
	new MessageDeletor(channelId, messageId);
    }
}
