package maqetta.core.server.command;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.davinci.server.user.IUser;
import org.davinci.server.user.UserException;
import org.maqetta.server.Command;
import org.maqetta.server.IDavinciServerConstants;
import org.maqetta.server.ServerManager;

public class Register extends Command {

    @Override
    public void handleCommand(HttpServletRequest req, HttpServletResponse resp, IUser user) throws IOException {
        String name = req.getParameter("userName");
        
        /* ensure the user name is word characters only to prevent user names like "..\..\xsers.xml" */
        
        Pattern validUserPattern = Pattern.compile("^[\\w\\-]([\\.\\w@])+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = validUserPattern.matcher(name);
		if(!matcher.matches()){
			this.responseString = "INVALID USER NAME";
			return;
		}
        
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        try {
            user = ServerManager.getServerManger().getUserManager().addUser(name, password, email);
            this.responseString = "OK";
            HttpSession session = req.getSession(true);
            session.setAttribute(IDavinciServerConstants.SESSION_USER, user);
            Cookie k = new Cookie(IDavinciServerConstants.SESSION_USER, user != null ? user.getUserName() : null);
    		k.setPath("/maqetta");
    		resp.addCookie(k);
        } catch (UserException e) {
            this.responseString = e.getReason();
        }

    }

}
