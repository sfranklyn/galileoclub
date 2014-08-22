/*
 * UrlsRolesBean.java
 *
 * Created on September 5, 2007, 1:23 PM
 *
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.dao.RolesDaoRemote;
import galileoclub.ejb.datamodel.RolesDataModelBean;
import galileoclub.ejb.datamodel.RolesDataModelRemote;
import galileoclub.ejb.dao.UrlsRolesDaoRemote;
import galileoclub.ejb.datamodel.UrlsRolesDataModelBean;
import galileoclub.ejb.datamodel.UrlsRolesDataModelRemote;
import galileoclub.ejb.service.UrlsRolesServiceRemote;
import galileoclub.jpa.Roles;
import galileoclub.jpa.UrlsRoles;
import galileoclub.jpa.UrlsRolesPK;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class UrlsRolesBean {

    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private UrlsRoles urlsRoles = null;
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    private UrlsRolesDataModelRemote urlsRolesDataModelRemote = null;
    @EJB
    private RolesDaoRemote rolesDaoRemote = null;
    @EJB
    private UrlsRolesDaoRemote urlsRolesDaoRemote = null;
    @EJB
    private RolesDataModelRemote rolesDataModelRemote = null;
    @EJB
    private UrlsRolesServiceRemote urlsRolesServiceRemote = null;

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public DatabaseDataModel getDataModel() {
        return dataModel;
    }

    public PropertyResourceBundle getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(PropertyResourceBundle messageSource) {
        this.messageSource = messageSource;
    }

    public VisitBean getVisit() {
        return visit;
    }

    public void setVisit(VisitBean visit) {
        this.visit = visit;
    }

    public String create() {
        urlsRoles = new UrlsRoles();
        urlsRoles.setUrlsRolesPK(new UrlsRolesPK());
        urlsRoles.setRoles(new Roles());
        return "redirect:secure/urls_rolesCreate";
    }

    @SuppressWarnings("unchecked")
    public String saveCreate() {
        String result = "secure/urls_rolesCreate";

        List<String> errorList = urlsRolesServiceRemote.saveCreate(urlsRoles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/urls_roles";
        }

        return result;
    }

    public String read() {
        urlsRoles = (UrlsRoles) dataModel.getRowData();
        return "redirect:secure/urls_rolesRead";
    }

    public String delete() {
        urlsRoles = (UrlsRoles) dataModel.getRowData();
        return "redirect:secure/urls_rolesDelete";
    }

    @SuppressWarnings("unchecked")
    public String saveDelete() {
        String result = "secure/urls_rolesDelete";

        List<String> errorList = urlsRolesServiceRemote.saveDelete(urlsRoles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/urls_roles";
        }

        return result;
    }

    public List<SelectItem> getRoleNameList() {
        List<SelectItem> roleNameList = new ArrayList<SelectItem>();
        List rolesList = new ArrayList();
        rolesList = rolesDataModelRemote.getAll(RolesDataModelBean.SELECT_ALL, null, 0, Short.MAX_VALUE);
        for (int idx = 0; idx < rolesList.size(); idx++) {
            Roles roles = (Roles) rolesList.get(idx);
            SelectItem selectItem = new SelectItem(roles.getRoleName());
            roleNameList.add(selectItem);
        }
        return roleNameList;
    }

    public RolesDaoRemote getRolesDaoRemote() {
        return rolesDaoRemote;
    }

    public void setRolesDaoRemote(RolesDaoRemote rolesDaoRemote) {
        this.rolesDaoRemote = rolesDaoRemote;
    }

    public RolesDataModelRemote getRolesDataModelRemote() {
        return rolesDataModelRemote;
    }

    public void setRolesDataModelRemote(RolesDataModelRemote rolesDataModelRemote) {
        this.rolesDataModelRemote = rolesDataModelRemote;
    }

    public UrlsRolesDaoRemote getUrlsRolesDaoRemote() {
        return urlsRolesDaoRemote;
    }

    public void setUrlsRolesDaoRemote(UrlsRolesDaoRemote urlsRolesDaoRemote) {
        this.urlsRolesDaoRemote = urlsRolesDaoRemote;
    }

    public UrlsRolesDataModelRemote getUrlsRolesDataModelRemote() {
        return urlsRolesDataModelRemote;
    }

    @EJB
    public void setUrlsRolesDataModelRemote(UrlsRolesDataModelRemote urlsRolesDataModelRemote) {
        dataModel.setSelect(UrlsRolesDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(UrlsRolesDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(urlsRolesDataModelRemote);
        this.urlsRolesDataModelRemote = urlsRolesDataModelRemote;
    }

    public UrlsRoles getUrlsRoles() {
        return urlsRoles;
    }

    public void setUrlsRoles(UrlsRoles urlsRoles) {
        this.urlsRoles = urlsRoles;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
