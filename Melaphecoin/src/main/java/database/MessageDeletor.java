package database;

import static main.MainClass.*;

public class MessageDeletor extends Thread {
    
    private static final long MESSAGE_LIFETIME = 10 * 60000; // 10 minutes
    
    private long channelId;
    private long messageId;
    
    public MessageDeletor(long channelId, long messageId) {
	this.channelId = channelId;
	this.messageId = messageId;
	
	start();
    }
    
    @Override
    public void run() {
        long startingTime = System.currentTimeMillis();
        while (startingTime + MESSAGE_LIFETIME > System.currentTimeMillis()) {}
        
        getGuild().getTextChannelById(channelId).deleteMessageById(messageId).queue();
    }

    public static void deleteMessage(long channelId, long messageId) {
	new MessageDeletor(channelId, messageId);
    }
}
