package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Database extends Thread {

    // TODO save backups

    private static final String DATA_FILE = "C:\\Users\\almog\\Desktop\\db.txt";
    private static final long BACKUP_CYCLE_TIME = 10 * 60000; // 10 minutes
    private static final int STARTING_BALANCE = 100;
    public static final Database DATABASE = new Database();

    private ConcurrentHashMap<Long, Integer> data;
    private boolean update;

    private Database() {
	data = new ConcurrentHashMap<Long, Integer>();
	update = false;

	List<String> lines = null;
	try {
	    lines = Files.readAllLines(new File(DATA_FILE).toPath(), Charset.defaultCharset());
	} catch (IOException ignored) {
	}

	for (String line : lines) {
	    String[] parts = line.split("-");
	    data.put(Long.valueOf(parts[0]), Integer.valueOf(parts[1]));
	}

	start();
    }

    @Override
    public void run() {
	while (true) {
	    long startingTime = System.currentTimeMillis();
	    while (startingTime + BACKUP_CYCLE_TIME > System.currentTimeMillis()) {
	    }

	    if (update) {

		String dataToWrite = "";

		Iterator<Entry<Long, Integer>> iterator = data.entrySet().iterator();
		while (iterator.hasNext()) {
		    Entry<Long, Integer> pair = iterator.next();
		    dataToWrite += String.valueOf(pair.getKey()) + "-" + String.valueOf(pair.getValue()) + "\n";
		}

		try (Writer writer = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(DATA_FILE), "utf-8"))) {
		    writer.write(dataToWrite);
		} catch (IOException ignored) {
		}

		update = false;
	    }
	}
    }

    /**
     * Sets the balance of a member to the given balance
     * 
     * @param memberId - targte member
     * @param balance  - balance to set to
     */
    public void write(long memberId, int balance) {
	validateMember(memberId);
	data.put(memberId, balance);
	update = true;
    }

    /**
     * Returns the balance of a given member
     * 
     * @param memberId - target member
     * @return - balance
     */
    public int read(long memberId) {
	validateMember(memberId);
	return data.get(memberId);
    }

    /**
     * Adds given amount of money to member's balance
     * 
     * @param memberId - target member
     * @param amount   - amount to add
     * @return - new balance
     */
    public int add(long memberId, int amount) {
	validateMember(memberId);
	int newBalance = data.get(memberId) + amount;
	data.put(memberId, newBalance);
	update = true;
	return newBalance;
    }

    /**
     * Subtracts given amount of money from a member
     * 
     * @param memberId - target member
     * @param amount   - amount to subtract
     * @return - new balance, -1 if the action will cause the user to enter a
     *         negative balance
     */
    public int subtract(long memberId, int amount) {
	validateMember(memberId);
	int balance = data.get(memberId);

	if (balance - amount < 0)
	    return -1;

	int newBalance = balance - amount;
	data.put(memberId, newBalance);
	update = true;
	return newBalance;
    }

    /**
     * Transfer a given amount of money from member1 to member2
     * 
     * @param member1 - member to take the money from
     * @param member2 - member to give the money to
     * @param amount  - amount to transfer
     * @return - true/false if the operation succeeded
     */
    public boolean transfer(long member1, long member2, int amount) {
	if (subtract(member1, amount) == -1)
	    return false;

	add(member2, amount);
	return true;
    }

    /**
     * Adds the member to the map if he isn't already there
     * 
     * @param memberId - member to check
     */
    private void validateMember(long memberId) {
	if (!data.containsKey(memberId))
	    data.put(memberId, STARTING_BALANCE);
    }

    /**
     * Database object is singleton
     * 
     * @return - the database object
     */
    public static Database database() {
	return DATABASE;
    }
}
