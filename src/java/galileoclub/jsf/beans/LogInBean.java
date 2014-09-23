/*
 * LogIn.java
 *
 * Created on July 31, 2007, 1:29 PM
 *
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.dao.PointsDaoRemote;
import galileoclub.ejb.dao.RolesDaoRemote;
import galileoclub.ejb.dao.UsersDaoRemote;
import galileoclub.ejb.service.UsersServiceRemote;
import galileoclub.jpa.Users;
import java.io.IOException;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class LogInBean {

    private static final Logger log = Logger.getLogger(LogInBean.class.getName());
    private String userName = null;
    private String userPassword = null;
    private AppBean app;
    private VisitBean visit = null;
    private PropertyResourceBundle messageSource;
    @EJB
    private UsersDaoRemote usersDaoRemote;
    @EJB
    private RolesDaoRemote rolesDaoRemote;
    @EJB
    private UsersServiceRemote usersServiceRemote;
    @EJB
    private PointsDaoRemote pointsDaoRemote;

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(final String userPassword) {
        this.userPassword = userPassword;
    }

    public String logIn() {
        if (usersServiceRemote == null) {
            try {
                final HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                req.getSession().invalidate();
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            } finally {
                return "index";
            }
        }

        List<String> errorList = usersServiceRemote.logIn(userName, userPassword, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
            return "index";
        }

        Users users = usersDaoRemote.selectByUserName(userName);
        visit.setUserId(users.getUserId());
        visit.setUserName(users.getUserName());
        visit.setFullName(users.getFullName());
        visit.setUserCode(users.getUserCode());
        visit.setIsAdmin(rolesDaoRemote.isAdminByUserName(visit.getUserName()));
        final List<String> menuList = rolesDaoRemote.getMenuList(visit.getUserName());
        visit.setMenuList(menuList);
        if (users.getUserCode() != null && !"".equals(users.getUserCode())) {
            visit.setPointCount(pointsDaoRemote.sumByPointUserCode(users.getUserCode()));
        } else {
            visit.setPointCount(null);
        }
        visit.setUserPcc(users.getUserPcc());
        visit.setUserSon(users.getUserSon());

        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(visit.getSecurePath());
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ex.getMessage(), ""));
        }
        return "redirect:secure/index";
    }

    public VisitBean getVisit() {
        return visit;
    }

    public void setVisit(final VisitBean visit) {
        this.visit = visit;
    }

    public RolesDaoRemote getRolesDaoRemote() {
        return rolesDaoRemote;
    }

    public void setRolesDaoRemote(RolesDaoRemote rolesDaoRemote) {
        this.rolesDaoRemote = rolesDaoRemote;
    }

    public PropertyResourceBundle getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(PropertyResourceBundle messageSource) {
        this.messageSource = messageSource;
    }

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}