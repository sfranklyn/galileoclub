/*
 * Authorization.java
 *
 * Created on September 5, 2007, 4:05 PM
 */
package galileoclub.filter;

import galileoclub.ejb.dao.UsersDaoRemote;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import galileoclub.jsf.beans.VisitBean;

/**
 *
 * @author  Samuel
 * @version
 */
public class Authorization implements Filter {

    private static final Logger log = Logger.getLogger(Authorization.class.getName());
    @EJB
    private UsersDaoRemote usersDaoRemote = null;

    public Authorization() {
        super();
        if (log.isLoggable(Level.FINE)) {
            log.fine("Authorization filter created");
        }
    }

    @Override
    public void doFilter(final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse res = (HttpServletResponse) response;
        VisitBean visit = (VisitBean) req.getSession().getAttribute("visit");
        if ((visit == null) || (visit.getUserId() == null)) {
            if (visit == null) {
                visit = new VisitBean();
                req.getSession().setAttribute("visit", visit);
            }
            visit.setSecurePath(req.getContextPath() + req.getServletPath() + req.getPathInfo());
            res.sendRedirect(req.getContextPath() + "/faces/index.xhtml");
            return;
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", visit.getUserId());
        param.put("urlRole", req.getRequestURI());
        List userList = null;
        try {
            userList = usersDaoRemote.selectByUserIdUrl(param);
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            return;
        }
        if (userList.size() <= 0) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        log.info("Init Authorization filter");
    }

    @Override
    public void destroy() {
        log.info("Destroy Authorization filter");
    }
}