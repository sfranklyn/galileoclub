/*
 * Visit.java
 *
 * Created on Jul 20, 2007, 2:25:04 PM
 *
 */
package galileoclub.jsf.beans;

import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class VisitBean {
    private static final Locale localeId = new Locale("in", "ID");
    private static final Locale localeUs = new Locale("en", "US");
    private Locale locale = null;
    private Integer userId = null;
    private String userName = null;
    private String fullName = null;
    private String securePath = null;
    private List menuList = null;
    private Boolean isAdmin = null;
    private Long pointCount = null;
    private String userPcc = null;
    private String userSon = null;
    private String userCode = null;

    public Locale getLocale() {
        if (locale == null) {
            locale = localeId;
        }
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        return locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    public String chooseEnglish() {
        locale = localeUs;
        return null;
    }

    public String chooseIndonesia() {
        locale = localeId;
        return null;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(final Integer userId) {
        this.userId = userId;
    }

    public String getSecurePath() {
        return securePath;
    }

    public void setSecurePath(final String securePath) {
        this.securePath = securePath;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public List getMenuList() {
        return menuList;
    }

    public void setMenuList(final List menuList) {
        this.menuList = menuList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Long getPointCount() {
        return pointCount;
    }

    public void setPointCount(Long pointCount) {
        this.pointCount = pointCount;
    }

    public String getUserPcc() {
        return userPcc;
    }

    public void setUserPcc(String userPcc) {
        this.userPcc = userPcc;
    }

    public String getUserSon() {
        return userSon;
    }

    public void setUserSon(String userSon) {
        this.userSon = userSon;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}
