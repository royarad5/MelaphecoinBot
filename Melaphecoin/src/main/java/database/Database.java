package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * holds a representation of the database
 */
public class Database extends Thread {

    // TODO save backups

    private static final String BALANCE_FILE = "C:\\Users\\almog\\Desktop\\balance.txt";
    private static final String DAILY_SPIN_FILE = "C:\\Users\\almog\\Desktop\\daily spin.txt";
    private static final String DEBTS_FILE = "C:\\Users\\almog\\Desktop\\debts.txt";
    private static final long BACKUP_CYCLE_TIME = 10 * 60000; // 10 minutes

    private static final int STARTING_BALANCE = 100;

    public static final Database DATABASE = new Database();

    private final ConcurrentHashMap<Long, Integer> balances;
    private final ConcurrentHashMap<Long, Long> dailySpins;
    private final ConcurrentHashMap<Long, Integer> debts;
    private boolean updateBalance;
    private boolean updateDailySpin;
    private boolean updateDebts;

    private Database() {
	balances = new ConcurrentHashMap<Long, Integer>();
	dailySpins = new ConcurrentHashMap<Long, Long>();
	debts = new ConcurrentHashMap<Long, Integer>();
	updateBalance = false;
	updateDailySpin = false;
	updateDebts = false;

	List<String> lines = null;
	try {
	    lines = Files.readAllLines(new File(BALANCE_FILE).toPath(), Charset.defaultCharset());
	} catch (IOException ignored) {
	}

	for (String line : lines) {
	    String[] parts = line.split("-");
	    balances.put(Long.valueOf(parts[0]), Integer.valueOf(parts[1]));
	}
	// ---------------------------------------------------------------------------
	try {
	    lines = Files.readAllLines(new File(DAILY_SPIN_FILE).toPath(), Charset.defaultCharset());
	} catch (IOException ignored) {
	}

	for (String line : lines) {
	    String[] parts = line.split("-");
	    dailySpins.put(Long.valueOf(parts[0]), Long.valueOf(parts[1]));
	}
	// ---------------------------------------------------------------------------
	try {
	    lines = Files.readAllLines(new File(DEBTS_FILE).toPath(), Charset.defaultCharset());
	} catch (IOException ignored) {
	}

	for (String line : lines) {
	    String[] parts = line.split("-");
	    debts.put(Long.valueOf(parts[0]), Integer.valueOf(parts[1]));
	}

	start();
    }

    @Override
    public void run() {
	while (true) {
	    long startingTime = System.currentTimeMillis();
	    while (startingTime + BACKUP_CYCLE_TIME > System.currentTimeMillis()) {
	    }

	    save();
	}
    }

    /**
     * saves the hashmaps to their respective files
     */
    private void save() {
	if (updateBalance) {
	    String dataToWrite = "";

	    Iterator<Entry<Long, Integer>> iterator = balances.entrySet().iterator();
	    while (iterator.hasNext()) {
		Entry<Long, Integer> pair = iterator.next();
		dataToWrite += String.valueOf(pair.getKey()) + "-" + String.valueOf(pair.getValue()) + "\n";
	    }

	    try (Writer writer = new BufferedWriter(
		    new OutputStreamWriter(new FileOutputStream(BALANCE_FILE), "utf-8"))) {
		writer.write(dataToWrite);
	    } catch (IOException ignored) {
	    }

	    updateBalance = false;
	}
	if (updateDailySpin) {
	    String dataToWrite = "";

	    Iterator<Entry<Long, Long>> iterator = dailySpins.entrySet().iterator();
	    while (iterator.hasNext()) {
		Entry<Long, Long> pair = iterator.next();
		dataToWrite += String.valueOf(pair.getKey()) + "-" + String.valueOf(pair.getValue()) + "\n";
	    }

	    try (Writer writer = new BufferedWriter(
		    new OutputStreamWriter(new FileOutputStream(DAILY_SPIN_FILE), "utf-8"))) {
		writer.write(dataToWrite);
	    } catch (IOException ignored) {
	    }

	    updateBalance = false;
	}
	if (updateDebts) {
	    String dataToWrite = "";

	    Iterator<Entry<Long, Integer>> iterator = debts.entrySet().iterator();
	    while (iterator.hasNext()) {
		Entry<Long, Integer> pair = iterator.next();
		dataToWrite += String.valueOf(pair.getKey()) + "-" + String.valueOf(pair.getValue()) + "\n";
	    }

	    try (Writer writer = new BufferedWriter(
		    new OutputStreamWriter(new FileOutputStream(DEBTS_FILE), "utf-8"))) {
		writer.write(dataToWrite);
	    } catch (IOException ignored) {
	    }

	    updateDebts = false;
	}
    }

    /**
     * forces to save everything to the files
     */
    public void forceSave() {
	updateBalance = true;
	updateDailySpin = true;
	updateDebts = true;
	save();
    }

    /**
     * Takes a loan for the given member, loan can only be a maximum of (balance * 3
     * + 500)
     * 
     * loan is returned with 105% of the amount of money taken
     * 
     * @param memberId - member to take the loan for
     * @param amount   - loan size
     * @return positiveInteger - current loan size, -1 - can't afford loan, 0 -
     *         success
     */
    public int takeLoan(long memberId, int amount) {
	validateDebt(memberId);
	validateMemberBalance(memberId);
	if (debts.get(memberId) > 0)
	    return debts.get(memberId);
	if (amount > maxLoanSize(memberId))
	    return -1;

	add(memberId, amount);
	debts.put(memberId, (int) (amount * 1.05));
	updateDebts = true;

	return 0;
    }

    /**
     * return the max loan size that a given member can take
     * 
     * @param memberId - target member
     * @return - maximum loan size
     */
    public int maxLoanSize(long memberId) {
	return getBalance(memberId) * 3 + 500;
    }

    public int payToDebt(long memberId, int amount) {
	validateDebt(memberId);
	int debt = getDebtSize(memberId);

	int payment = amount > debt ? debt : amount;

	subtract(memberId, payment);
	debts.put(memberId, debt - payment);

	updateDebts = true;

	return payment;
    }

    /**
     * returns the given member's debt
     * 
     * @param memberId - target member
     * @return - debt
     */
    public int getDebtSize(long memberId) {
	validateDebt(memberId);
	return debts.get(memberId);
    }

    /**
     * takes a 10% tax from the income action
     * 
     * @param memberId - member to tax
     * @param amount   - amount of money before taxation
     * @return - taxes paid
     */
    private int taxAction(long memberId, int amount) {
	if (getDebtSize(memberId) > 0) {
	    payToDebt(memberId, (int) (amount * 0.1));
	    return (int) (amount * 0.1);
	}
	return 0;
    }

    /**
     * makes sure that the member is inside the map
     * 
     * @param memberId - member to validate
     */
    private void validateDebt(long memberId) {
	if (!debts.containsKey(memberId)) {
	    updateDebts = true;
	    debts.put(memberId, 0);
	}
    }

    /**
     * Uses the member's free daily spin
     * 
     * @param memberId - target member to use his daily spin
     */
    public void useFreeSpin(long memberId) {
	dailySpins.put(memberId, System.currentTimeMillis() / 1000l);
	updateDailySpin = true;
    }

    /**
     * Checks if the member can use his free daily spin
     * 
     * @param memberId - member to check
     * @return true/false if the member has a free spin or not
     */
    public boolean hasFreeSpin(long memberId) {
	if (!dailySpins.containsKey(memberId)) {
	    dailySpins.put(memberId, 0l);
	    updateDailySpin = true;
	}

	Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jerusalem"));
	c.set(Calendar.HOUR_OF_DAY, 0);
	c.set(Calendar.MINUTE, 0);
	c.set(Calendar.SECOND, 0);
	c.set(Calendar.MILLISECOND, 0);
	long unixTimeStamp = c.getTimeInMillis() / 1000;

	return unixTimeStamp > dailySpins.get(memberId);
    }

    /**
     * @return a copy of the balances hashmap
     */
    public ConcurrentHashMap<Long, Integer> getBalances() {
	return new ConcurrentHashMap<Long, Integer>(balances);
    }

    /**
     * Sets the balance of a member to the given balance
     * 
     * @param memberId - targte member
     * @param balance  - balance to set to
     */
    public void setBalance(long memberId, int balance) {
	validateMemberBalance(memberId);
	balances.put(memberId, balance);
	updateBalance = true;
    }

    /**
     * Returns the balance of a given member
     * 
     * @param memberId - target member
     * @return balance
     */
    public int getBalance(long memberId) {
	validateMemberBalance(memberId);
	return balances.get(memberId);
    }

    /**
     * Adds given amount of money to member's balance
     * 
     * @param memberId - target member
     * @param amount   - amount to add
     * @return new balance
     */
    public int add(long memberId, int amount) {
	validateMemberBalance(memberId);
	taxAction(memberId, amount);
	int newBalance = balances.get(memberId) + amount;
	balances.put(memberId, newBalance);
	updateBalance = true;
	return newBalance;
    }

    /**
     * Subtracts given amount of money from a member
     * 
     * @param memberId - target member
     * @param amount   - amount to subtract
     * @return new balance, -1 if the action will cause the user to enter a negative
     *         balance
     */
    public int subtract(long memberId, int amount) {
	validateMemberBalance(memberId);
	int balance = balances.get(memberId);

	if (balance - amount < 0)
	    return -1;

	int newBalance = balance - amount;
	balances.put(memberId, newBalance);
	updateBalance = true;

	return newBalance;
    }

    /**
     * Transfer a given amount of money from member1 to member2
     * 
     * @param member1 - member to take the money from
     * @param member2 - member to give the money to
     * @param amount  - amount to transfer
     * @return true/false if the operation succeeded
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
    private void validateMemberBalance(long memberId) {
	if (!balances.containsKey(memberId)) {
	    balances.put(memberId, STARTING_BALANCE);
	    updateBalance = true;
	}
    }

    /**
     * Database object is singleton
     * 
     * @return the database object
     */
    public static Database database() {
	return DATABASE;
    }
}
