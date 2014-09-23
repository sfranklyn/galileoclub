/*
 * UsersBean.java
 *
 * Created on August 6, 2007, 2:08 PM
 *
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.dao.PccsDaoRemote;
import galileoclub.ejb.dao.PointsDaoRemote;
import galileoclub.ejb.dao.UsersDaoRemote;
import galileoclub.ejb.datamodel.*;
import galileoclub.ejb.service.ClaimsServiceRemote;
import galileoclub.ejb.service.UsersServiceRemote;
import galileoclub.jpa.Claims;
import galileoclub.jpa.Pccs;
import galileoclub.jpa.Points;
import galileoclub.jpa.Users;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Samuel Franklyn
 */
public class UsersBean {

    private final Integer noOfRows = 20;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private final DatabaseDataModel dataModelNewMember = new DatabaseDataModel();
    private final ListDataModel dataModelPointClaim = new ListDataModel();
    private final ListDataModel dataModelClaimReconfirm = new ListDataModel();
    private final ListDataModel dataModelNewClaim = new ListDataModel();
    private final DateTimeFormatter dtfDMY = DateTimeFormat.forPattern("dd MMMM yyyy");
//    private final DateTimeFormatter dtfDMYT = DateTimeFormat.forPattern("dd MMMM yyyy HH:mm:ss aa");
    private final DateTimeFormatter dtfDMYT = DateTimeFormat.forPattern("dd MMM yyyy HH:mm:ss aa");
    private final DateTimeFormatter dtfMY = DateTimeFormat.forPattern("MMMM yyyy");
    private final DateTimeFormatter dtfYMD = DateTimeFormat.forPattern("yyyyMMdd");
    private Users users = new Users();
    private Pccs pccs = new Pccs();
    private Claims claims = null;
    private VisitBean visit = null;
    private AppBean app = null;
    private String userPassword1 = null;
    private String userPassword2 = null;
    private UsersDataModelRemote usersDataModelRemote = null;
    private Long pointCount = null;
    private PnrCountsBean pnrCountsBean = null;
    private String userPcc = null;
    private String userFullName = null;
    private String userCode = null;
    private Integer userPointValueFrom = null;
    private Integer userPointValueInto = null;
    private Boolean pointClaimHistoryAscending = null;
    @EJB
    private PccsDaoRemote pccsDaoRemote;
    @EJB
    private UsersDaoRemote usersDaoRemote;
    @EJB
    private UsersServiceRemote usersServiceRemote;
    @EJB
    private PointsDaoRemote pointsDaoRemote;
    @EJB
    private ClaimsServiceRemote claimsServiceRemote;
    @EJB
    private ClaimsDataModelRemote claimsDataModelRemote;
    @EJB
    private PnrcountsDataModelRemote pnrcountsDataModelRemote;

    public DatabaseDataModel getDataModel() {
        return dataModel;
    }

    public DatabaseDataModel getDataModelNewMember() {
        return dataModelNewMember;
    }

    public ListDataModel getDataModelNewClaim() {
        List newClaimsDbList = claimsDataModelRemote.getAll(ClaimsDataModelBean.SELECT_PENDING, null, 0, -1);
        List<Map> newClaimList = new ArrayList<Map>();
        for (Object obj : newClaimsDbList) {
            Map<String, Object> newClaimMap = new HashMap<String, Object>();
            Claims claim = (Claims) obj;
            newClaimMap.put("claimId", claim.getClaimId());
            newClaimMap.put("claimDate", dtfDMYT.print(new DateTime(claim.getClaimDate().getTime())));
            newClaimMap.put("claimUserCode", claim.getClaimUserCode());
            users = usersDaoRemote.selectByUserCode(claim.getClaimUserCode());
            newClaimMap.put("fullName", users.getFullName());
            newClaimMap.put("claim", claim);
            newClaimList.add(newClaimMap);
        }
        dataModelNewClaim.setWrappedData(newClaimList);
        return dataModelNewClaim;
    }

    public ListDataModel getDataModelPointClaim() {
        return dataModelPointClaim;
    }

    public ListDataModel getDataModelClaimReconfirm() {
        return dataModelClaimReconfirm;
    }

    public UsersDaoRemote getUsersDaoRemote() {
        return usersDaoRemote;
    }

    public void setUsersDaoRemote(final UsersDaoRemote usersDaoRemote) {
        this.usersDaoRemote = usersDaoRemote;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public String create() {
        users = new Users();
        users.setUserName("");
        users.setFullName("");
        users.setUserPassword("");
        userPassword1 = "";
        userPassword2 = "";
        claims = new Claims();
        return "redirect:secure/usersCreate";
    }

    @SuppressWarnings("unchecked")
    public String saveCreate() {
        String result = "secure/usersCreate";

        users.setUserCreated(new Date());
        users.setUserCreatedBy(visit.getUserName());
        List<String> errorList = usersServiceRemote.saveCreate(users,
                userPassword1, userPassword2, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            create();
            result = "redirect:secure/users";
        }
        return result;
    }

    public String read() {
        users = (Users) dataModel.getRowData();
        return "redirect:secure/usersRead";
    }

    public String update() {
        users = (Users) dataModel.getRowData();
        return "redirect:secure/usersUpdate";
    }

    public String saveUpdate() {
        String result = "secure/usersUpdate";

        users.setUserUpdate(new Date());
        users.setUserUpdatedBy(visit.getUserName());
        List<String> errorList = usersServiceRemote.saveUpdate(users, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/users";
        }

        return result;
    }

    public String delete() {
        users = (Users) dataModel.getRowData();
        return "redirect:secure/usersDelete";
    }

    public String saveDelete() {
        String result = "secure/usersDelete";

        List<String> errorList = usersServiceRemote.saveDelete(users, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/users";
        }

        return result;
    }

    public String transferToPoint() {
        users = (Users) dataModel.getRowData();
        List<String> errorList = usersServiceRemote.transferToPoint(users, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        }
        return "secure/users";
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(final Users users) {
        this.users = users;
    }

    public Pccs getPccs() {
        return pccs;
    }

    public void setPccs(final Pccs pccs) {
        this.pccs = pccs;
    }

    public VisitBean getVisit() {
        return visit;
    }

    public void setVisit(final VisitBean visit) {
        this.visit = visit;
    }

    public String changePassword() {
        users = usersDaoRemote.selectByUserName(visit.getUserName());
        userPassword1 = "";
        userPassword2 = "";
        return "redirect:secure/change_password";
    }

    @SuppressWarnings("unchecked")
    public String saveChangePassword() {
        String result = "secure/change_password";

        List<String> errorList = usersServiceRemote.saveChangePassword(users,
                userPassword1, userPassword2, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/index";
        }

        return result;
    }

    public String newMemberRegistration() {
        create();
        DateTime today = new DateTime();
        DateTime twentyFiveYearsAgo = today.minusYears(25);
        users.setUserBirthday(twentyFiveYearsAgo.toDate());
        return "newMemberRegistration";
    }

    public String newMemberRegistrationSave() {
        String result = "newMemberRegistration";

        users.setUserStatus("P");
        users.setUserCreated(new Date());
        users.setUserCreatedBy("Member");
        users.setUserPcc(users.getUserPcc().toUpperCase());
        users.setUserSon(users.getUserSon().toUpperCase());
        List<String> errorList = usersServiceRemote.saveCreate(users,
                userPassword1, userPassword2, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            create();
            result = "index";
        }
        return result;
    }

    public String newMemberRegistrationCancel() {
        create();
        return "index";
    }

    public String newMemberVerification() {
        users = (Users) dataModelNewMember.getRowData();
        return "redirect:secure/newMemberVerification";
    }

    public String newMemberActivate() {
        String result = "secure/newMemberVerification";

        List<String> errorList = usersServiceRemote.activate(users, visit.getLocale(), visit.getUserName());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/newMember";
        }

        return result;
    }

    public String newMemberReject() {
        String result = "secure/newMemberVerification";

        List<String> errorList = usersServiceRemote.reject(users, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/newMember";
        }

        return result;
    }

    public String editProfile() {
        users = usersDaoRemote.selectByUserName(visit.getUserName());
        return "redirect:secure/editProfile";
    }

    public String saveEditProfile() {
        String result = "secure/editProfile";

        users.setUserUpdate(new Date());
        users.setUserUpdatedBy("Member");
        List<String> errorList = usersServiceRemote.saveUpdate(users, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/index";
        }

        return result;
    }

    public String pointClaimHistorySort() {
        if (pointClaimHistoryAscending) {
            pointClaimHistoryAscending = false;
        } else {
            pointClaimHistoryAscending = true;
        }
        users = usersDaoRemote.selectByUserName(visit.getUserName());
        List<Map> pointClaimList = new ArrayList<Map>();
        List<Points> pointsList = pointsDaoRemote.findPointsByUserCode(users.getUserCode(), pointClaimHistoryAscending);
        pointClaimHistoryDisplay(pointsList, pointClaimList);
        return "redirect:secure/pointClaimHistory";
    }

    public String pointClaimHistory() {
        users = usersDaoRemote.selectByUserName(visit.getUserName());
        List<Map> pointClaimList = new ArrayList<Map>();
        pointClaimHistoryAscending = false;
        List<Points> pointsList = pointsDaoRemote.findPointsByUserCode(users.getUserCode(), pointClaimHistoryAscending);
        pointClaimHistoryDisplay(pointsList, pointClaimList);
        return "redirect:secure/pointClaimHistory";
    }

    private void pointClaimHistoryDisplay(List<Points> pointsList, List<Map> pointClaimList) {
        for (Points points : pointsList) {
            Map<String, Object> pointClaimMap = new HashMap<String, Object>();
            pointClaimMap.put("points", points);
            DateTime date = new DateTime(points.getPointYear(), points.getPointMonth(), points.getPointDay(), 0, 0, 0, 0);
            pointClaimMap.put("date", date);
            if (points.getPointDay() == 1) {
                pointClaimMap.put("dateFmt", dtfMY.print(date));
            } else {
                pointClaimMap.put("dateFmt", dtfDMY.print(date));
            }
            if (points.getClaims() == null) {
                pointClaimMap.put("desc", "Point");
                pointClaimMap.put("response", "");
                pointClaimMap.put("status", "-");
                DateTime firstDayOfMonth = date;
                DateTime lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("pnrcountsPcc", points.getPointPcc());
                param.put("pnrcountsSignOn", points.getPointSignon());
                param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
                param.put("pnrcountsEnd", dtfYMD.print(lastDayOfMonth));
                List pnrCounts5List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_BY_DATEPCCSIGNON, param, 0, -1);
                if (pnrCounts5List.size() > 0) {
                    pointClaimMap.put("detail", Boolean.TRUE);
                } else {
                    pointClaimMap.put("detail", Boolean.FALSE);
                }
            } else {
                pointClaimMap.put("desc", points.getClaims().getClaimDesc());
                pointClaimMap.put("response", points.getClaims().getClaimResponse());
                pointClaimMap.put("status", points.getClaims().getClaimStatus());
            }
            pointClaimMap.put("count", points.getPointCount());
            pointClaimList.add(pointClaimMap);
        }
        dataModelPointClaim.setWrappedData(pointClaimList);
    }

    public String cancelEditProfile() {
        create();
        return "redirect:secure/index";
    }

    public String getClaimDate() {
        DateTime date = new DateTime();
        return dtfDMY.print(date);
    }

    public String claimReward() {
        users = usersDaoRemote.selectByUserName(visit.getUserName());
        claims = new Claims();
        claims.setClaimUserCode(users.getUserCode());
        return "redirect:secure/claimReward";
    }

    public String saveClaimReward() {
        claims.setClaimDate(new Date());
        claims.setClaimStatus("P");
        List<String> errorList = claimsServiceRemote.saveClaimReward(claims, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            create();
        }
        return "redirect:secure/claimRewardResponse";
    }

    public String cancelClaimReward() {
        create();
        return "redirect:secure/index";
    }

    public String claimReconfirm() {
        users = usersDaoRemote.selectByUserName(visit.getUserName());
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("claimUserCode", users.getUserCode());
        List claimsList = claimsDataModelRemote.getAll(ClaimsDataModelBean.SELECT_PENDING_BY_CLAIMUSERCODE, param, 0, -1);
        List<Map> claimReconfirmList = new ArrayList<Map>();
        for (Object obj : claimsList) {
            Map<String, Object> claimReconfirmMap = new HashMap<String, Object>();
            Claims claim = (Claims) obj;
            claimReconfirmMap.put("claim", claim);
            claimReconfirmMap.put("date", dtfDMY.print(new DateTime(claim.getClaimDate().getTime())));
            claimReconfirmMap.put("desc", claim.getClaimDesc());
            claimReconfirmMap.put("response", claim.getClaimResponse());
            claimReconfirmMap.put("status", claim.getClaimStatus());
            claimReconfirmMap.put("count", claim.getClaimCount());
            claimReconfirmList.add(claimReconfirmMap);
        }
        dataModelClaimReconfirm.setWrappedData(claimReconfirmList);
        return "redirect:secure/claimReconfirm";
    }

    @SuppressWarnings("unchecked")
    public String saveClaimReconfirm() {
        String result = "secure/claimReconfirm";
        Map<String, Object> claimReconfirmMap = (Map<String, Object>) dataModelClaimReconfirm.getRowData();
        claims = (Claims) claimReconfirmMap.get("claim");
        List<String> errorList = claimsServiceRemote.saveReconfirm(claims, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            visit.setPointCount(pointsDaoRemote.sumByPointUserCode(claims.getClaimUserCode()));
            result = "redirect:secure/claimReconfirmResponse";
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String newClaimProcess() {
        Map<String, Object> newClaimMap = (Map<String, Object>) dataModelNewClaim.getRowData();
        claims = (Claims) newClaimMap.get("claim");
        users = usersDaoRemote.selectByUserCode(claims.getClaimUserCode());
        pccs = pccsDaoRemote.findByPccsPcc(users.getUserPcc());
        if (users.getUserCode() != null && !"".equals(users.getUserCode())) {
            pointCount = pointsDaoRemote.sumByPointUserCode(users.getUserCode());
        }
        return "redirect:secure/newClaimProcess";
    }

    public String saveNewClaimProcess() {
        String result = "secure/newClaimProcess";
        List<String> errorList = claimsServiceRemote.saveNewClaimProcess(claims, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/newClaim";
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public String pointDetail() {
        String result = "";
        Map<String, Object> pointClaimMap = (Map<String, Object>) dataModelPointClaim.getRowData();
        String status = (String) pointClaimMap.get("status");
        if (status.equals("-")) {
            DateTime firstDayOfMonth = (DateTime) pointClaimMap.get("date");
            DateTime lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
            Map<String, Object> param = new HashMap<String, Object>();
            Points point = (Points) pointClaimMap.get("points");
            param.put("pnrcountsPcc", point.getPointPcc());
            param.put("pnrcountsSignOn", point.getPointSignon());
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", dtfYMD.print(lastDayOfMonth));
            List pnrCounts5List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_BY_DATEPCCSIGNON, param, 0, -1);
            pnrCountsBean.getDataModel5().setWrappedData(pnrCounts5List);
            pnrCountsBean.setDisplayHeadLink(Boolean.FALSE);
            return "redirect:secure/pnrCounts5";
        }
        return result;
    }

    public String newClaimProcessCancel() {
        create();
        return "redirect:secure/newClaim";
    }

    public String findByPcc() {
        if (!userPcc.equals("")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userPcc", userPcc);
            dataModel.setSelect(UsersDataModelBean.SELECT_BY_PCC);
            dataModel.setSelectCount(UsersDataModelBean.SELECT_BY_PCC_COUNT);
            dataModel.setSelectParam(param);
            dataModel.setWrappedData(usersDataModelRemote);
        } else {
            dataModel.setSelect(UsersDataModelBean.SELECT_ALL);
            dataModel.setSelectCount(UsersDataModelBean.SELECT_ALL_COUNT);
            dataModel.setSelectParam(null);
            dataModel.setWrappedData(usersDataModelRemote);
        }
        return "redirect:secure/users";
    }

    public String findByFullName() {
        if (!userFullName.equals("")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("fullName", userFullName + "%");
            dataModel.setSelect(UsersDataModelBean.SELECT_BY_FULLNAME);
            dataModel.setSelectCount(UsersDataModelBean.SELECT_BY_FULLNAME_COUNT);
            dataModel.setSelectParam(param);
            dataModel.setWrappedData(usersDataModelRemote);
        } else {
            dataModel.setSelect(UsersDataModelBean.SELECT_ALL);
            dataModel.setSelectCount(UsersDataModelBean.SELECT_ALL_COUNT);
            dataModel.setSelectParam(null);
            dataModel.setWrappedData(usersDataModelRemote);
        }
        return "redirect:secure/users";
    }

    public String findByUserCode() {
        if (!userCode.equals("")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("userCode", userCode + "%");
            dataModel.setSelect(UsersDataModelBean.SELECT_BY_USERCODE);
            dataModel.setSelectCount(UsersDataModelBean.SELECT_BY_USERCODE_COUNT);
            dataModel.setSelectParam(param);
            dataModel.setWrappedData(usersDataModelRemote);
        } else {
            dataModel.setSelect(UsersDataModelBean.SELECT_ALL);
            dataModel.setSelectCount(UsersDataModelBean.SELECT_ALL_COUNT);
            dataModel.setSelectParam(null);
            dataModel.setWrappedData(usersDataModelRemote);
        }
        return "redirect:secure/users";
    }

    public String setValue() {
        userPointValueFrom = 0;
        userPointValueInto = 0;
        return "redirect:secure/usersSetValue";
    }

    public String setValueSave() {
        String result = "secure/usersSetValue";

        List<String> errorList = usersServiceRemote.setUserPointValue(userPointValueFrom,
                userPointValueInto, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            create();
            result = "redirect:secure/users";
        }
        return result;
    }

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public UsersDataModelRemote getUsersDataModelRemote() {
        return usersDataModelRemote;
    }

    @EJB
    public void setUsersDataModelRemote(UsersDataModelRemote usersDataModelRemote) {
        dataModel.setSelect(UsersDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(UsersDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(usersDataModelRemote);

        dataModelNewMember.setSelect(UsersDataModelBean.SELECT_NEW_MEMBER);
        dataModelNewMember.setSelectCount(UsersDataModelBean.SELECT_NEW_MEMBER_COUNT);
        dataModelNewMember.setSelectParam(null);
        dataModelNewMember.setWrappedData(usersDataModelRemote);
        this.usersDataModelRemote = usersDataModelRemote;
    }

    public String getUserPassword1() {
        return userPassword1;
    }

    public void setUserPassword1(String userPassword1) {
        this.userPassword1 = userPassword1;
    }

    public String getUserPassword2() {
        return userPassword2;
    }

    public void setUserPassword2(String userPassword2) {
        this.userPassword2 = userPassword2;
    }

    public Claims getClaims() {
        return claims;
    }

    public void setClaims(Claims claims) {
        this.claims = claims;
    }

    public Long getPointCount() {
        return pointCount;
    }

    public void setPointCount(Long pointCount) {
        this.pointCount = pointCount;
    }

    public List<SelectItem> getStatusList() {
        List<SelectItem> statusList = new ArrayList<SelectItem>();
        SelectItem selectItem = new SelectItem("P", "Pending");
        statusList.add(selectItem);
        selectItem = new SelectItem("C", "Confirm");
        statusList.add(selectItem);
        selectItem = new SelectItem("X", "Reject");
        statusList.add(selectItem);
        return statusList;
    }

    public PnrCountsBean getPnrCountsBean() {
        return pnrCountsBean;
    }

    public void setPnrCountsBean(PnrCountsBean pnrCountsBean) {
        this.pnrCountsBean = pnrCountsBean;
    }

    public String getUserPcc() {
        return userPcc;
    }

    public void setUserPcc(String userPcc) {
        this.userPcc = userPcc;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public Integer getUserPointValueFrom() {
        return userPointValueFrom;
    }

    public void setUserPointValueFrom(Integer userPointValueFrom) {
        this.userPointValueFrom = userPointValueFrom;
    }

    public Integer getUserPointValueInto() {
        return userPointValueInto;
    }

    public void setUserPointValueInto(Integer userPointValueInto) {
        this.userPointValueInto = userPointValueInto;
    }

    public Boolean getPointClaimHistoryAscending() {
        return pointClaimHistoryAscending;
    }

    public void setPointClaimHistoryAscending(Boolean pointClaimHistoryAscending) {
        this.pointClaimHistoryAscending = pointClaimHistoryAscending;
    }
}
