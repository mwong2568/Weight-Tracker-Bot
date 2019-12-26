import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import com.mysql.fabric.xmlrpc.base.Member;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Commands extends ListenerAdapter {

	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		try {
			
			//connect to SQL database
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/dbot?autoreconnect=true&useSSL=false", "root", "Misael941320!");

			//ignore events from the bot itself
			if (event.getAuthor().isBot())
				return;
			
			
			Message message = event.getMessage();
			String[] args = message.getContentRaw().split(" ");
			String msg = message.getContentDisplay();
			Date date = new Date();
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

			
			if (msg.equals("w!join")) {
				User newUser = message.getAuthor();
				String newUserID = newUser.getId();
				
				Statement stmt = con.createStatement();
				stmt.executeUpdate("INSERT INTO Profile(Discord_ID) VALUES (\"" + newUserID + "\")");
				con.close();
				
				event.getChannel().sendMessage("You have been added to the database!").queue();
				
			}
			
			if (args[0].equals("w!delete")) {
				String deleteDate = args[1];
				User user = event.getAuthor();
				String userID = user.getId();
				
				Statement stmt = con.createStatement();
				stmt.executeUpdate("DELETE FROM Weigh_In WHERE (Discord_ID = \"" + userID + "\" "
						+ " AND Date = \"" + deleteDate + "\")");
				con.close();
				
				event.getChannel().sendMessage("Successfully deleted.").queue();
			}
			
			if (msg.equals("w!users")) {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Profile");
				
				while (rs.next())
				{
					if (rs.getString(1).isEmpty())
						break;
					String userID = rs.getString(1);
					
					User tempUser = event.getJDA().getUserById(userID);
					event.getChannel().sendMessage(tempUser.getName()).queue();
				}
					
				con.close();
			}
			if (msg.equals("w!entries")) {
				User user = message.getAuthor();
				String userID = user.getId();
				
				event.getChannel().sendMessage("Here's a list of the total weigh ins for <@" + userID + ">!").queue();
				
				
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Weigh_In WHERE Discord_ID=\"" + userID + "\";");
				
				StringBuilder totalWeighIns = new StringBuilder();
				
				int numWeighIns = 0;
				while (rs.next())
				{
					totalWeighIns.append(rs.getString(2) + "    " + rs.getDouble(3) + "\n");
					numWeighIns++;
				}
					event.getChannel().sendMessage(totalWeighIns.toString()).queue();
					event.getChannel().sendMessage("Wow you weighed in " + numWeighIns + " times! Keep it up! :)").queue();
				con.close();
			}
			if (msg.equals("w!info")) {
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage("Here is the list of all available commands!").queue();
				event.getChannel().sendMessage("w!join --  Adds user to the database").queue();
				event.getChannel().sendMessage("w!weighin <weight> --  Stores your current weight").queue();
				event.getChannel().sendMessage("w!delete <MM/DD/YYYY> --  Deletes your weigh in(s) on that date").queue();
				event.getChannel().sendMessage("w!users --  Lists all current users in the database").queue();
				event.getChannel().sendMessage("w!entries --  Lists all your entries").queue();
			}

			if (args[0].equals("w!weighin")) {

				if (args.length == 1) {
					event.getChannel().sendTyping().queue();
					event.getChannel().sendMessage("Please input your weight following the command :)").queue();
				} else if (args.length == 2) {
					boolean isNumeric = true;
					for (int i = 0; i < args[1].length(); i++) {
						if (!Character.isDigit(args[1].charAt(i)) && args[1].charAt(i) != '.')
							isNumeric = false;
					}
					if (isNumeric) {
						//get weight if the input is numeric
						double inputWeight = Double.parseDouble(args[1]);
						//get current date
						int year  = localDate.getYear();
						int month = localDate.getMonthValue();
						int day   = localDate.getDayOfMonth();
						String weighInDate = Integer.toString(month) + "/" + Integer.toString(day) 
						+ "/" + Integer.toString(year);
						
						//store info in database
						User user = message.getAuthor();
						String userID = user.getId();
						
						Statement stmt = con.createStatement();
						stmt.executeUpdate("INSERT INTO Weigh_In (Discord_ID, Date, Weight) "
								+ "VALUES (\"" + userID + "\", \"" + weighInDate + "\", \"" + inputWeight + "\")");
						con.close();
						
						
						event.getChannel().sendTyping().queue();
						event.getChannel().sendMessage("Your weigh in " + inputWeight + ", on " 
						+ weighInDate + " has been stored!").queue();
					} else {
						event.getChannel().sendTyping().queue();
						event.getChannel().sendMessage("Please input a number :)").queue();
					}
				}

			}

			if (args[0].equalsIgnoreCase(Mainclass.prefix + "weight")) {
				event.getChannel().sendTyping().queue();
				event.getChannel().sendMessage("omg weight haosdjsamwefodlnflkw").queue();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
