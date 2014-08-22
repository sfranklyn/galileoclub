/*
 * App.java
 *
 * Created on Jul 19, 2007, 2:19:19 PM
 *
 */
package galileoclub.jsf.beans;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class AppBean {

    public String getBaseURL() {
        final HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        final String baseURL = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/faces/index.xhtml";
        return baseURL;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
