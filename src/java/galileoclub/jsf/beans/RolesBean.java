/*
 * RolesBean.java
 *
 * Created on August 6, 2007, 3:39 PM
 *
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.datamodel.RolesDataModelBean;
import galileoclub.ejb.datamodel.RolesDataModelRemote;
import galileoclub.ejb.service.RolesServiceRemote;
import galileoclub.jpa.Roles;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.List;
import java.util.PropertyResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class RolesBean {

    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private Roles roles = null;
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    @EJB
    private RolesDataModelRemote rolesDataModelRemote = null;
    @EJB
    private RolesServiceRemote rolesServiceRemote = null;

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public DatabaseDataModel getDataModel() {
        dataModel.setSelect(RolesDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(RolesDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(rolesDataModelRemote);
        return dataModel;
    }

    public String create() {
        roles = new Roles();
        return "redirect:secure/rolesCreate";
    }

    public String saveCreate() {
        String result = "secure/rolesCreate";

        List<String> errorList = rolesServiceRemote.saveCreate(roles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/roles";
        }

        return result;
    }

    public String read() {
        roles = (Roles) dataModel.getRowData();
        return "redirect:secure/rolesRead";
    }

    public String update() {
        roles = (Roles) dataModel.getRowData();
        return "redirect:secure/rolesUpdate";
    }

    public String saveUpdate() {
        String result = "secure/rolesUpdate";

        List<String> errorList = rolesServiceRemote.saveUpdate(roles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/roles";
        }

        return result;
    }

    public String delete() {
        roles = (Roles) dataModel.getRowData();
        return "redirect:secure/rolesDelete";
    }

    public String saveDelete() {
        String result = "secure/rolesDelete";

        List<String> errorList = rolesServiceRemote.saveDelete(roles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/roles";
        }

        return result;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(final Roles roles) {
        this.roles = roles;
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

    public RolesDataModelRemote getRolesDataModelRemote() {
        return rolesDataModelRemote;
    }

    public void setRolesDataModelRemote(RolesDataModelRemote rolesDataModelRemote) {
        this.rolesDataModelRemote = rolesDataModelRemote;
    }

    public RolesServiceRemote getRolesServiceRemote() {
        return rolesServiceRemote;
    }

    public void setRolesServiceRemote(RolesServiceRemote rolesServiceRemote) {
        this.rolesServiceRemote = rolesServiceRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
