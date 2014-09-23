/*
 * ClaimsBean.java
 * 
 * Created on Feb 12, 2009, 11:59:11 AM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.dao.PointsDaoRemote;
import galileoclub.ejb.datamodel.ClaimsDataModelBean;
import galileoclub.ejb.datamodel.ClaimsDataModelRemote;
import galileoclub.ejb.service.ClaimsServiceRemote;
import galileoclub.jpa.Claims;
import galileoclub.jpa.Users;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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
public class ClaimsBean {

    private static final Logger log = Logger.getLogger(ClaimsBean.class.getName());

    public static Logger getLog() {
        return log;
    }
    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel claimsDataModel = new DatabaseDataModel();
    private final DatabaseDataModel userClaimsDataModel = new DatabaseDataModel();
    private VisitBean visit = null;
    private UsersBean usersBean = null;
    private Users users;
    private Claims claims;
    private Long pointCount = null;
    @EJB
    private PointsDaoRemote pointsDaoRemote;
    @EJB
    private ClaimsDataModelRemote claimsDataModelRemote;
    @EJB
    private ClaimsServiceRemote claimsServiceRemote;

    public String read() {
        users = (Users) usersBean.getDataModel().getRowData();
        pointCount = pointsDaoRemote.sumByPointUserCode(users.getUserCode());
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("claimUserCode", users.getUserCode());
        userClaimsDataModel.setSelect(ClaimsDataModelBean.SELECT_BY_CLAIMUSERCODE);
        userClaimsDataModel.setSelectCount(ClaimsDataModelBean.SELECT_BY_CLAIMUSERCODE_COUNT);
        userClaimsDataModel.setSelectParam(param);
        userClaimsDataModel.setWrappedData(claimsDataModelRemote);
        return "redirect:secure/claimsMaintenance";
    }

    public String create() {
        claims = new Claims();
        claims.setClaimUserCode(users.getUserCode());
        return "redirect:secure/claimsCreate";
    }

    public String saveCreate() {
        String result = "secure/claimsCreate";
        List<String> errorList = claimsServiceRemote.saveCreate(claims, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            pointCount = pointsDaoRemote.sumByPointUserCode(users.getUserCode());
            result = "redirect:secure/claimsMaintenance";
        }
        return result;
    }

    public String delete() {
        claims = (Claims) userClaimsDataModel.getRowData();
        return "redirect:secure/claimsDelete";
    }

    public String saveDelete() {
        String result = "secure/claimsDelete";
        List<String> errorList = claimsServiceRemote.saveDelete(claims, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            pointCount = pointsDaoRemote.sumByPointUserCode(users.getUserCode());
            result = "redirect:secure/claimsMaintenance";
        }
        return result;
    }

    public String update() {
        claims = (Claims) userClaimsDataModel.getRowData();
        return "redirect:secure/claimsUpdate";
    }

    public List<SelectItem> getStatusList() {
        List<SelectItem> statusList = new ArrayList<SelectItem>();
        SelectItem selectItem = new SelectItem("P", "Pending");
        statusList.add(selectItem);
        selectItem = new SelectItem("C", "Confirm");
        statusList.add(selectItem);
        selectItem = new SelectItem("X", "Reject");
        statusList.add(selectItem);
        selectItem = new SelectItem("R", "Reconfirm");
        statusList.add(selectItem);
        return statusList;
    }

    public String displayConfirmed() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("claimStatus", "C");
        claimsDataModel.setSelect(ClaimsDataModelBean.SELECT_BY_CLAIMSTATUS);
        claimsDataModel.setSelectCount(ClaimsDataModelBean.SELECT_BY_CLAIMSTATUS_COUNT);
        claimsDataModel.setSelectParam(param);
        claimsDataModel.setWrappedData(claimsDataModelRemote);
        return null;
    }

    public String displayPending() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("claimStatus", "P");
        claimsDataModel.setSelect(ClaimsDataModelBean.SELECT_BY_CLAIMSTATUS);
        claimsDataModel.setSelectCount(ClaimsDataModelBean.SELECT_BY_CLAIMSTATUS_COUNT);
        claimsDataModel.setSelectParam(param);
        claimsDataModel.setWrappedData(claimsDataModelRemote);
        return null;
    }

    public String displayCanceled() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("claimStatus", "X");
        claimsDataModel.setSelect(ClaimsDataModelBean.SELECT_BY_CLAIMSTATUS);
        claimsDataModel.setSelectCount(ClaimsDataModelBean.SELECT_BY_CLAIMSTATUS_COUNT);
        claimsDataModel.setSelectParam(param);
        claimsDataModel.setWrappedData(claimsDataModelRemote);
        return null;
    }

    public String displayAll() {
        claimsDataModel.setSelect(ClaimsDataModelBean.SELECT_ALL);
        claimsDataModel.setSelectCount(ClaimsDataModelBean.SELECT_ALL_COUNT);
        claimsDataModel.setSelectParam(null);
        claimsDataModel.setWrappedData(claimsDataModelRemote);
        return null;
    }

    public DatabaseDataModel getClaimsDataModel() {
        return claimsDataModel;
    }

    public DatabaseDataModel getUserClaimsDataModel() {
        return userClaimsDataModel;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
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

    public Long getPointCount() {
        return pointCount;
    }

    public void setPointCount(Long pointCount) {
        this.pointCount = pointCount;
    }

    public PointsDaoRemote getPointsDaoRemote() {
        return pointsDaoRemote;
    }

    public void setPointsDaoRemote(PointsDaoRemote pointsDaoRemote) {
        this.pointsDaoRemote = pointsDaoRemote;
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public ClaimsServiceRemote getClaimsServiceRemote() {
        return claimsServiceRemote;
    }

    public void setClaimsServiceRemote(ClaimsServiceRemote claimsServiceRemote) {
        this.claimsServiceRemote = claimsServiceRemote;
    }

    public VisitBean getVisit() {
        return visit;
    }

    public void setVisit(VisitBean visit) {
        this.visit = visit;
    }

    public ClaimsDataModelRemote getClaimsDataModelRemote() {
        return claimsDataModelRemote;
    }

    @EJB
    public void setClaimsDataModelRemote(ClaimsDataModelRemote claimsDataModelRemote) {
        claimsDataModel.setSelect(ClaimsDataModelBean.SELECT_ALL);
        claimsDataModel.setSelectCount(ClaimsDataModelBean.SELECT_ALL_COUNT);
        claimsDataModel.setSelectParam(null);
        claimsDataModel.setWrappedData(claimsDataModelRemote);
        this.claimsDataModelRemote = claimsDataModelRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
