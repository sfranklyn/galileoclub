/*
 * UsersRolesBean.java
 *
 * Created on August 7, 2007, 10:37 AM
 *
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.dao.RolesDaoRemote;
import galileoclub.ejb.datamodel.RolesDataModelBean;
import galileoclub.ejb.datamodel.RolesDataModelRemote;
import galileoclub.ejb.dao.UsersDaoRemote;
import galileoclub.ejb.datamodel.UsersDataModelRemote;
import galileoclub.ejb.dao.UsersRolesDaoRemote;
import galileoclub.ejb.datamodel.UsersRolesDataModelBean;
import galileoclub.ejb.datamodel.UsersRolesDataModelRemote;
import galileoclub.ejb.service.UsersRolesServiceRemote;
import galileoclub.jpa.Roles;
import galileoclub.jpa.Users;
import galileoclub.jpa.UsersRoles;
import galileoclub.jpa.UsersRolesPK;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.ArrayList;
import java.util.List;
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
public class UsersRolesBean {

    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private UsersRoles usersRoles = null;
    private VisitBean visit = null;
    @EJB
    private UsersRolesDataModelRemote usersRolesDataModelRemote;
    @EJB
    private UsersDataModelRemote usersDataModelRemote;
    @EJB
    private UsersDaoRemote usersDaoRemote;
    @EJB
    private RolesDaoRemote rolesDaoRemote;
    @EJB
    private UsersRolesDaoRemote usersRolesDaoRemote;
    @EJB
    private RolesDataModelRemote rolesDataModelRemote;
    @EJB
    private UsersRolesServiceRemote usersRolesServiceRemote;

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public DatabaseDataModel getDataModel() {
        return dataModel;
    }

    public UsersRolesDaoRemote getUsersRolesDaoRemote() {
        return usersRolesDaoRemote;
    }

    public void setUsersRolesDaoRemote(final UsersRolesDaoRemote usersRolesDaoRemote) {
        this.usersRolesDaoRemote = usersRolesDaoRemote;
    }

    public String create() {
        usersRoles = new UsersRoles();
        usersRoles.setUsersRolesPK(new UsersRolesPK());
        usersRoles.setUsers(new Users());
        usersRoles.setRoles(new Roles());
        return "redirect:secure/users_rolesCreate";
    }

    @SuppressWarnings("unchecked")
    public String saveCreate() {
        String result = "secure/users_rolesCreate";

        List<String> errorList = usersRolesServiceRemote.saveCreate(usersRoles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/users_roles";
        }

        return result;
    }

    public String read() {
        usersRoles = (UsersRoles) dataModel.getRowData();
        return "redirect:secure/users_rolesRead";
    }

    public String delete() {
        usersRoles = (UsersRoles) dataModel.getRowData();
        return "redirect:secure/users_rolesDelete";
    }

    @SuppressWarnings("unchecked")
    public String saveDelete() {
        String result = "secure/users_rolesDelete";

        List<String> errorList = usersRolesServiceRemote.saveDelete(usersRoles, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/users_roles";
        }

        return result;
    }

    public UsersRoles getUsersRoles() {
        return usersRoles;
    }

    public void setUsersRoles(UsersRoles usersRoles) {
        this.usersRoles = usersRoles;
    }

    public VisitBean getVisit() {
        return visit;
    }

    public void setVisit(VisitBean visit) {
        this.visit = visit;
    }

    public UsersDaoRemote getUsersDaoRemote() {
        return usersDaoRemote;
    }

    public void setUsersDaoRemote(UsersDaoRemote usersDaoRemote) {
        this.usersDaoRemote = usersDaoRemote;
    }

    public List autoCompleteUsers(Object suggest) {
        String strSuggest = (String) suggest;
        return usersDaoRemote.selectLikeUserName(strSuggest);
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

    public UsersDataModelRemote getUsersDataModelRemote() {
        return usersDataModelRemote;
    }

    public void setUsersDataModelRemote(UsersDataModelRemote usersDataModelRemote) {
        this.usersDataModelRemote = usersDataModelRemote;
    }

    public UsersRolesDataModelRemote getUsersRolesDataModelRemote() {
        return usersRolesDataModelRemote;
    }

    @EJB
    public void setUsersRolesDataModelRemote(UsersRolesDataModelRemote usersRolesDataModelRemote) {
        dataModel.setSelect(UsersRolesDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(UsersRolesDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(usersRolesDataModelRemote);
        this.usersRolesDataModelRemote = usersRolesDataModelRemote;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
