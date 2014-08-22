/*
 * LogOut.java
 *
 * Created on August 1, 2007, 5:34 PM
 *
 */
package galileoclub.jsf.beans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Samuel Franklyn
 */
public class LogOutBean {

    public String logOut() {
        final HttpSession session = (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:index";
    }
}
