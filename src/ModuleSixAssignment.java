
/** ... */

/**
 * Word Occurrences
 */

/**
 * Reviews the poem "The Raven" to see how many times each unique word occurs in the poem.
 */

import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.*;

import org.jsoup.Jsoup;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * <p>
 * This program access the poem "The Raven" by Edgar Allen Poe through the
 * Internet using Jsoup. Once the poem is recovered it is turned into a string
 * array and then the frequency of each word is calculated. From there, each
 * unique word and its count is stored.
 * </p>
 * <p>
 * Users are provided a UI where they can review the word occurrences. Results
 * are displayed in descending order with the most frequently used words
 * appearing first. Within the UI the user can indicate how many results they
 * would like to see.
 * </p>
 * 
 * @author Nick Astor
 * @version 1.0.0 March 30, 2022
 *
 */

public class ModuleSixAssignment {
	/**
	 * The list model that will show the word occurrence results
	 */
	public static DefaultListModel<String> listModel = new DefaultListModel<String>();

	/**
	 * Placed in class scope so multiple methods can easily access the connection
	 */
	public static Connection conn;

	/**
	 * Generate string array for each word in poem Removes punctuation and makes all
	 * words lowercase
	 * 
	 * @param url URL to pull poem from. Assumes URL has poem located in div
	 *            class="chapter"
	 * @return array of all words in poem without punctuation and lowercased
	 */
	public static String[] generate_poem(String url) {
		String poem = null;
		Document doc;
		// Loop through to account for a potential temporary connection issue
		do {
			try {
				// Connect to url and recover text from <div class="chapter"> tag
				doc = Jsoup.connect(url).get();
				poem = doc.select("div.chapter").first().text();
			} catch (IOException e) {
				// Inform user the program is going to attempt the request again
				System.out.println(
						"Error connecting to URL...Make sure the server hosting the URL is accessible\nTrying again.\n");
			}
		} while (poem == null);
		// Remove all punctuation, make lowercase, and split into array with whitespace
		// as a delimiter
		return poem.replaceAll("[-—]", " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
	}

	/**
	 * Populates UI with amount of results user indicated
	 * 
	 * @param query Amount of results from frequency mapping that needs to be
	 *              displayed
	 */
	public static void populateListModel(int query) {
		// Clear JList
		listModel.clear();

		try {
			// Prepare statement
			Statement statement = conn.createStatement();
			// Return top words limited by user supplied input
			ResultSet results = statement.executeQuery("SELECT word, COUNT(word) " + "FROM word " + "GROUP BY word "
					+ "ORDER BY COUNT(word) DESC, word LIMIT " + query + ";");
			// populate list model with sql results
			int x = 1;
			while (results.next()) {
				listModel.addElement(x + ": " + results.getString(1) + " - " + results.getInt(2));
				x++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates initial connection to database and prepares table
	 */
	public static void createTable() {
		try {
			// Create connection to database
			String driver = "com.mysql.cj.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/word_occurences";
			String username = "root";
			// This has been changed for privacy
			String password = "MyNewPass";
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);

			// Create statement and clear table
			Statement statement = conn.createStatement();
			int check = statement.executeUpdate("TRUNCATE word;");

			// Recover words
			String[] words = generate_poem("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm");

			// Update each word into database
			for (String word : words) {
				check = statement.executeUpdate("INSERT INTO word (word) VALUES ('" + word + "');");
				if (check == 0)
					System.out.println("Something is wrong");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void constructGUI() {
		// Create frame
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame();
		frame.setTitle("Word Occurrences");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create layout
		frame.setLayout(new GridLayout(3, 2));

		// Create Swing Components
		JLabel searchLabel = new JLabel("Enter number of results to display: ");
		JLabel responseLabel = new JLabel("Results: ");
		JList<String> stringList = new JList<String>(listModel);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(stringList);
		stringList.setLayoutOrientation(JList.VERTICAL);
		JButton transmitButton = new JButton("Get Results");
		JTextField textSearch = new JTextField();
		JLabel emptyLabel = new JLabel();

		// Create action listener
		transmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Populate the list model
				populateListModel(Integer.parseInt(textSearch.getText()));
			}
		});

		// Adding components to panels and frame
		frame.add(searchLabel);
		frame.add(textSearch);
		frame.add(responseLabel);
		frame.add(scrollPane);
		frame.add(emptyLabel);
		frame.add(transmitButton);

		// Pack frame
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Connect to db and create table
		createTable();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				constructGUI();
			}
		});

	}

}
