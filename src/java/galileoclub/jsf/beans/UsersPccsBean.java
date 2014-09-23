/*
 * UsersPccsBean.java
 * 
 * Created on Sep 18, 2014, 10:38:35 AM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.datamodel.PccsDataModelBean;
import galileoclub.ejb.datamodel.PccsDataModelRemote;
import galileoclub.ejb.datamodel.UsersPccsDataModelBean;
import galileoclub.ejb.service.UsersPccsServiceRemote;
import galileoclub.jpa.Pccs;
import galileoclub.jpa.Users;
import galileoclub.jpa.UsersPccs;
import galileoclub.jpa.UsersPccsPK;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.*;
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
public class UsersPccsBean {

    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    private UsersBean usersBean = null;
    private Users users = null;
    private UsersPccs usersPccs = null;
    private String response = null;
    @EJB
    private PccsDataModelRemote pccsDataModelRemote;
    @EJB
    private UsersPccsServiceRemote usersPccsServiceRemote;

    public DatabaseDataModel getDataModel() {
        dataModel.setSelect(UsersPccsDataModelBean.SELECT_BY_USER);
        dataModel.setSelectCount(UsersPccsDataModelBean.SELECT_BY_USER_COUNT);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("users", users);
        dataModel.setSelectParam(param);
        dataModel.setWrappedData(pccsDataModelRemote);
        return dataModel;
    }
    
    public List<SelectItem> getPccList() {
        List<SelectItem> roleNameList = new ArrayList<SelectItem>();
        List pccsList = pccsDataModelRemote.getAll(PccsDataModelBean.SELECT_ALL, null, 0, Short.MAX_VALUE);
        for (int idx = 0; idx < pccsList.size(); idx++) {
            Pccs pccs = (Pccs) pccsList.get(idx);
            SelectItem selectItem = new SelectItem(pccs.getPccsPcc());
            roleNameList.add(selectItem);
        }
        return roleNameList;
    }    

    public String read() {
        users = (Users) usersBean.getDataModel().getRowData();
        return "redirect:secure/usersPccs";
    }

    public String create() {
        usersPccs = new UsersPccs();
        usersPccs.setUsersPccsPK(new UsersPccsPK());
        usersPccs.setUsers(users);
        usersPccs.setPccs(new Pccs());
        return "redirect:secure/usersPccsCreate";
    }

    public String saveCreate() {
        String result = "secure/usersPccsCreate";

        List<String> errorList = usersPccsServiceRemote.saveCreate(usersPccs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/usersPccs";
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public String delete() {
        usersPccs = (UsersPccs) dataModel.getRowData();
        return "redirect:secure/usersPccsDelete";
    }

    public String saveDelete() {
        String result = "secure/usersPccsDelete";

        List<String> errorList = usersPccsServiceRemote.saveDelete(usersPccs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/usersPccs";
        }

        return result;
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

    public UsersBean getUsersBean() {
        return usersBean;
    }

    public void setUsersBean(UsersBean usersBean) {
        this.usersBean = usersBean;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public UsersPccs getUsersPccs() {
        return usersPccs;
    }

    public void setUsersPccs(UsersPccs usersPccs) {
        this.usersPccs = usersPccs;
    }
}
