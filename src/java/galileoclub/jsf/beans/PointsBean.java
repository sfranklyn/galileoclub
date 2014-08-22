/*
 * PointsBean.java
 * 
 * Created on Jan 15, 2009, 2:55:25 PM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.dao.PointsDaoRemote;
import galileoclub.ejb.datamodel.PnrcountsDataModelBean;
import galileoclub.ejb.datamodel.PnrcountsDataModelRemote;
import galileoclub.ejb.service.PointsServiceRemote;
import galileoclub.jpa.Points;
import galileoclub.jpa.Users;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Samuel Franklyn
 */
public class PointsBean {

    private static final Logger log = Logger.getLogger(PointsBean.class.getName());
    private final DateTimeFormatter dtfDMY = DateTimeFormat.forPattern("dd MMMM yyyy");
    private final DateTimeFormatter dtfMY = DateTimeFormat.forPattern("MMMM yyyy");
    private final DateTimeFormatter dtfYMD = DateTimeFormat.forPattern("yyyyMMdd");
    private final ListDataModel dataModelPointClaim = new ListDataModel();
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    private UsersBean usersBean = null;
    private Users users = null;
    private Points points = null;
    private Long pointCount = null;
    private PnrCountsBean pnrCountsBean = null;
    private Boolean pointAscending = null;
    private String response = null;
    @EJB
    private PointsDaoRemote pointsDaoRemote = null;
    @EJB
    private PnrcountsDataModelRemote pnrcountsDataModelRemote = null;
    @EJB
    private PointsServiceRemote pointsServiceRemote = null;

    @SuppressWarnings("unchecked")
    public String pointReport() {
        Map<String, Object> pointClaimMap = (Map<String, Object>) dataModelPointClaim.getRowData();
        response = (String) pointClaimMap.get("response");
        return "redirect:secure/pointsReport";
    }

    public String sortPoints() {
        if (pointAscending) {
            pointAscending = false;
        } else {
            pointAscending = true;
        }
        List<Map> pointClaimList = new ArrayList<Map>();
        List<Points> pointsList = pointsDaoRemote.findPointsByUserCode(users.getUserCode(), pointAscending);
        displayPoints(pointsList, pointClaimList);
        return "redirect:secure/points";
    }

    private void readPoints() {
        pointCount = pointsDaoRemote.sumByPointUserCode(users.getUserCode());
        List<Map> pointClaimList = new ArrayList<Map>();
        pointAscending = false;
        List<Points> pointsList = pointsDaoRemote.findPointsByUserCode(users.getUserCode(), pointAscending);
        displayPoints(pointsList, pointClaimList);
    }

    private void displayPoints(List<Points> pointsList, List<Map> pointClaimList) {
        for (Points pointsItem : pointsList) {
            Map<String, Object> pointClaimMap = new HashMap<String, Object>();
            pointClaimMap.put("points", pointsItem);
            DateTime date = new DateTime(pointsItem.getPointYear(), pointsItem.getPointMonth(), pointsItem.getPointDay(), 0, 0, 0, 0);
            pointClaimMap.put("date", date);
            if (pointsItem.getPointDay() == 1) {
                pointClaimMap.put("dateFmt", dtfMY.print(date));
            } else {
                pointClaimMap.put("dateFmt", dtfDMY.print(date));
            }
            if (pointsItem.getClaims() == null) {
                pointClaimMap.put("desc", "Point");
                pointClaimMap.put("response", "");
                pointClaimMap.put("status", "-");
                DateTime firstDayOfMonth = date;
                DateTime lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("pnrcountsPcc", pointsItem.getPointPcc());
                param.put("pnrcountsSignOn", pointsItem.getPointSignon());
                param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
                param.put("pnrcountsEnd", dtfYMD.print(lastDayOfMonth));
                List pnrCounts5List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_BY_DATEPCCSIGNON, param, 0, -1);
                if (pnrCounts5List.size() > 0) {
                    pointClaimMap.put("detail", Boolean.TRUE);
                } else {
                    pointClaimMap.put("detail", Boolean.FALSE);
                }
            } else {
                pointClaimMap.put("desc", pointsItem.getClaims().getClaimDesc());
                pointClaimMap.put("response", pointsItem.getClaims().getClaimResponse());
                pointClaimMap.put("status", pointsItem.getClaims().getClaimStatus());
            }
            pointClaimMap.put("count", pointsItem.getPointCount());
            pointClaimList.add(pointClaimMap);
        }
        dataModelPointClaim.setWrappedData(pointClaimList);
        usersBean.getDataModelPointClaim().setWrappedData(pointClaimList);
    }

    public String read() {
        users = (Users) usersBean.getDataModel().getRowData();
        readPoints();
        usersBean.setUsers(users);
        return "redirect:secure/points";
    }

    @SuppressWarnings("unchecked")
    public String pointDetail() {
        String result = "secure/points";
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

    public String create() {
        points = new Points();
        points.setPointUserCode(users.getUserCode());
        points.setPointPcc(users.getUserPcc());
        points.setPointSignon(users.getUserSon());
        return "redirect:secure/pointsCreate";
    }

    public String saveCreate() {
        String result = "secure/pointsCreate";

        List<String> errorList = pointsServiceRemote.saveCreate(points, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            readPoints();
            result = "redirect:secure/points";
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public String delete() {
        Map<String, Object> pointClaimMap = (Map<String, Object>) dataModelPointClaim.getRowData();
        points = (Points) pointClaimMap.get("points");
        return "redirect:secure/pointsDelete";
    }

    public String saveDelete() {
        String result = "secure/pointsDelete";

        List<String> errorList = pointsServiceRemote.saveDelete(points, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            readPoints();
            result = "redirect:secure/points";
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public String update() {
        Map<String, Object> pointClaimMap = (Map<String, Object>) dataModelPointClaim.getRowData();
        points = (Points) pointClaimMap.get("points");
        return "redirect:secure/pointsUpdate";
    }

    public String saveUpdate() {
        String result = "secure/pointsUpdate";

        List<String> errorList = pointsServiceRemote.saveUpdate(points, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            readPoints();
            result = "redirect:secure/points";
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public String exportToExcel() {
        ServletOutputStream out = null;
        try {
            List<Map> pointClaimList = (List<Map>) usersBean.getDataModelPointClaim().getWrappedData();

            String filename = users.getUserName();
            filename = filename.replaceAll("/", "");
            filename = filename.replaceAll("\\\\", "");
            filename = filename.replaceAll("\\*", "");
            filename = filename.replaceAll("\\?", "");
            filename = filename.replaceAll("\\[", "");
            filename = filename.replaceAll("\\]", "");
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet(filename);
            HSSFHeader header = sheet.getHeader();
            header.setCenter(filename);

            String contentType = "application/vnd.ms-excel";
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
            filename = filename + ".xls";
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setContentType(contentType);
            out = response.getOutputStream();

            HSSFFont boldFont = wb.createFont();
            boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            HSSFCellStyle boldStyle = wb.createCellStyle();
            boldStyle.setFont(boldFont);

            HSSFRow row = sheet.createRow((short) 0);
            HSSFCell cell = row.createCell((short) 0);
            HSSFRichTextString text = new HSSFRichTextString(messageSource.getString("date"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 1);
            text = new HSSFRichTextString(messageSource.getString("desc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 2);
            text = new HSSFRichTextString(messageSource.getString("response"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 3);
            text = new HSSFRichTextString(messageSource.getString("point"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 4);
            text = new HSSFRichTextString(messageSource.getString("point_value"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 5);
            text = new HSSFRichTextString(messageSource.getString("status"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 6);
            text = new HSSFRichTextString(messageSource.getString("point_pcc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 7);
            text = new HSSFRichTextString(messageSource.getString("point_son"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            HSSFCellStyle date1CellStyle = wb.createCellStyle();
            HSSFCellStyle date2CellStyle = wb.createCellStyle();
            HSSFDataFormat dataFormat = wb.createDataFormat();
            date1CellStyle.setDataFormat(dataFormat.getFormat("MMMM yyyy"));
            date2CellStyle.setDataFormat(dataFormat.getFormat("dd MMMM yyyy"));

            short rowNum = 1;
            for (Map pointClaimMap : pointClaimList) {
                row = sheet.createRow((short) rowNum);

                int idx = 0;

                cell = row.createCell((short) idx);
                idx++;
                DateTime dt = (DateTime) pointClaimMap.get("date");
                cell.setCellValue(dt.toDate());
                if (dt.getDayOfMonth() == 1) {
                    cell.setCellStyle(date1CellStyle);
                } else {
                    cell.setCellStyle(date2CellStyle);
                }

                cell = row.createCell((short) idx);
                idx++;
                String str = (String) pointClaimMap.get("desc");
                text = new HSSFRichTextString((String) str);
                cell.setCellValue(text);

                cell = row.createCell((short) idx);
                idx++;
                str = (String) pointClaimMap.get("response");
                text = new HSSFRichTextString((String) str);
                cell.setCellValue(text);

                cell = row.createCell((short) idx);
                idx++;
                Integer intVal = (Integer) pointClaimMap.get("count");
                cell.setCellValue(intVal);

                Points pointClaimPoints = (Points) pointClaimMap.get("points");

                cell = row.createCell((short) idx);
                idx++;
                cell.setCellValue(pointClaimPoints.getPointValue());

                cell = row.createCell((short) idx);
                idx++;
                str = (String) pointClaimMap.get("status");
                text = new HSSFRichTextString((String) str);
                cell.setCellValue(text);

                cell = row.createCell((short) idx);
                idx++;
                text = new HSSFRichTextString(pointClaimPoints.getPointPcc());
                cell.setCellValue(text);

                cell = row.createCell((short) idx);
                idx++;
                text = new HSSFRichTextString(pointClaimPoints.getPointSignon());
                cell.setCellValue(text);

                rowNum++;
            }

            wb.write(out);
            fc.responseComplete();
        } catch (Exception ex) {
            log.log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                log.log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
            }
        }
        return "";
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

    public Long getPointCount() {
        return pointCount;
    }

    public void setPointCount(Long pointCount) {
        this.pointCount = pointCount;
    }

    public ListDataModel getDataModelPointClaim() {
        return dataModelPointClaim;
    }

    public PnrCountsBean getPnrCountsBean() {
        return pnrCountsBean;
    }

    public void setPnrCountsBean(PnrCountsBean pnrCountsBean) {
        this.pnrCountsBean = pnrCountsBean;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
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
}
