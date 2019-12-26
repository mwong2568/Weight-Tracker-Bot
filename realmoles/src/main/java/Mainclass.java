import java.sql.Connection;
import java.sql.DriverManager;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Mainclass {

	public static String prefix = "w!";
	
	public static void main(String[] args) throws LoginException {
		
			
			
			JDA jda = new JDABuilder("NjU1ODczOTE3ODIyNTY2NDUw.XffxnA.Px2Sa18ZJW52Lhq94Y6zeSPw1ck").build();
			//jda.getPresence().setStatus(OnlineStatus.IDLE);
			jda.getPresence().setActivity(Activity.watching("Miles"));;
			jda.addEventListener(new Commands());
			
			
		
		
	}

}
