/*
 * PnrCountsBean.java
 * 
 * Created on Dec 18, 2008, 11:59:18 AM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.datamodel.PnrcountsDataModelBean;
import galileoclub.ejb.datamodel.PnrcountsDataModelRemote;
import galileoclub.ejb.service.SegmentCounterServiceRemote;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.poi.hssf.usermodel.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Samuel Franklyn
 */
public class PnrCountsBean {

    private static final DateTimeFormatter dtfYMD = DateTimeFormat.forPattern("yyyyMMdd");
    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final ListDataModel dataModel1 = new ListDataModel();
    private final ListDataModel dataModel2 = new ListDataModel();
    private final ListDataModel dataModel3 = new ListDataModel();
    private final ListDataModel dataModel4 = new ListDataModel();
    private final ListDataModel dataModel5 = new ListDataModel();
    private final ListDataModel dataModel6 = new ListDataModel();
    private final ListDataModel dataModel7 = new ListDataModel();
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    private Object[] pnrCount1Vector;
    private TimeSeriesCollection timeSeriesDataSet1 = null;
    private TimeSeriesCollection timeSeriesDataSet2 = null;
    private DefaultPieDataset pieDataset = null;
    private Map pnrCount2Map = null;
    private Object[] pnrCount3Vector = null;
    private Object[] pnrCount4Vector = null;
    private Boolean displayHeadLink = Boolean.TRUE;
    private String pnrCountsPcc = null;
    private String pnrCountsPccDesc = null;
    private Boolean pnrCounts1SortAsc = null;
    private Boolean read7SortAsc = null;
    private Map<String, Object> param7 = null;
    private List<Map> dayList = null;
    private Boolean read1SortAsc = null;
    private List<Object[]> pnrCounts5List = null;
    private Boolean read5SortCreatedAsc = true;
    private Boolean read5SortCountAsc = true;
    private Boolean read5SortDepartedAsc = true;
    @EJB
    private PnrcountsDataModelRemote pnrcountsDataModelRemote;
    @EJB
    private SegmentCounterServiceRemote segmentCounterServiceRemote;

    public String pnrCounts1() {
        List pnrCounts1List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_YEARMONTH_DSC, null, 0, -1);
        pnrCounts1SortAsc = false;
        dataModel1.setWrappedData(pnrCounts1List);
        return "redirect:secure/pnrCounts1";
    }

    public String pnrCounts1Sort() {
        List pnrCounts1List;
        if (pnrCounts1SortAsc) {
            pnrCounts1List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_YEARMONTH_DSC, null, 0, -1);
            pnrCounts1SortAsc = false;
        } else {
            pnrCounts1List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_YEARMONTH, null, 0, -1);
            pnrCounts1SortAsc = true;
        }
        dataModel1.setWrappedData(pnrCounts1List);
        return "redirect:secure/pnrCounts1";
    }

    class Read1ComparatorDsc implements Comparator<Map> {

        @Override
        public int compare(Map o1, Map o2) {
            String yearmonthday1 = (String) o1.get("yearmonthday");
            String yearmonthday2 = (String) o2.get("yearmonthday");
            return yearmonthday2.compareTo(yearmonthday1);
        }
    }

    class Read1ComparatorAsc implements Comparator<Map> {

        @Override
        public int compare(Map o1, Map o2) {
            String yearmonthday1 = (String) o1.get("yearmonthday");
            String yearmonthday2 = (String) o2.get("yearmonthday");
            return yearmonthday1.compareTo(yearmonthday2);
        }
    }

    public String sort1() {
        if (read1SortAsc) {
            Collections.sort(dayList, new Read1ComparatorDsc());
            read1SortAsc = false;
            dataModel2.setWrappedData(dayList);
        } else {
            Collections.sort(dayList, new Read1ComparatorAsc());
            read1SortAsc = true;
            dataModel2.setWrappedData(dayList);
        }

        return "redirect:secure/pnrCounts2";
    }

    public String read1() {
        pnrCount1Vector = (Object[]) dataModel1.getRowData();
        String yearMonth = (String) pnrCount1Vector[0];
        DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonth + "01");
        DateTime lastDayOfMonth = firstDayOfMonth.dayOfMonth().withMaximumValue();
        DateTime today = new DateTime();
        dayList = new ArrayList<Map>();
        if (lastDayOfMonth.isAfter(today)) {
            lastDayOfMonth = today.minusDays(1);
        }
        Long previousCount = Long.valueOf(0);
        timeSeriesDataSet1 = new TimeSeriesCollection();
        TimeSeries timeSeries1 = new TimeSeries("Month To Date", Day.class);
        TimeSeries timeSeries2 = new TimeSeries("Waitlist", Day.class);
        TimeSeries timeSeries3 = new TimeSeries("Daily", Day.class);
        ExecutorService es = Executors.newFixedThreadPool(5);

        for (int idx = 0; idx < lastDayOfMonth.getDayOfMonth(); idx++) {
            Map<String, Object> pnrcount = new HashMap<String, Object>();

            pnrcount.put("yearmonthday", dtfYMD.print(firstDayOfMonth.plusDays(idx)));

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));

            DateTime pnrcountsEnd = firstDayOfMonth.plusDays(idx);
            param.put("pnrcountsEnd", dtfYMD.print(pnrcountsEnd));

            GetCount getCount = new GetCount(param);
            GetNameCount getNameCount = new GetNameCount(param);
            GetWaitCount getWaitCount = new GetWaitCount(param);
            GetPlusCount getPlusCount = new GetPlusCount(param);
            GetMinusCount getMinusCount = new GetMinusCount(param);

            Future<Long> futureCount = es.submit(getCount);
            Future<Long> futureNameCount = es.submit(getNameCount);
            Future<Long> futureWaitCount = es.submit(getWaitCount);
            Future<Long> futurePlusCount = es.submit(getPlusCount);
            Future<Long> futureMinusCount = es.submit(getMinusCount);

            Long count = Long.valueOf(0);
            Long nameCount = Long.valueOf(0);
            Long waitCount = Long.valueOf(0);
            Long plusCount = Long.valueOf(0);
            Long minusCount = Long.valueOf(0);

            try {
                count = futureCount.get();
                nameCount = futureNameCount.get();
                waitCount = futureWaitCount.get();
                plusCount = futurePlusCount.get();
                minusCount = futureMinusCount.get();
            } catch (InterruptedException ex) {
                Logger.getLogger(PnrCountsBean.class.getName()).log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(PnrCountsBean.class.getName()).log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
            }

            timeSeries1.add(new Day(firstDayOfMonth.plusDays(idx).toDate()), count);
            timeSeries2.add(new Day(firstDayOfMonth.plusDays(idx).toDate()), waitCount);
            timeSeries3.add(new Day(firstDayOfMonth.plusDays(idx).toDate()), count - previousCount);

            pnrcount.put("count", count);
            pnrcount.put("nameCount", nameCount);
            pnrcount.put("plusCount", plusCount);
            pnrcount.put("minusCount", minusCount);
            pnrcount.put("waitCount", waitCount);
            pnrcount.put("daytodate", count - previousCount);

            previousCount = count;
            dayList.add(pnrcount);
        }
        es.shutdown();

        Collections.sort(dayList, new Read1ComparatorDsc());
        read1SortAsc = false;
        dataModel2.setWrappedData(dayList);

        timeSeriesDataSet1.addSeries(timeSeries1);
        timeSeriesDataSet1.addSeries(timeSeries2);
        timeSeriesDataSet1.addSeries(timeSeries3);

        return "redirect:secure/pnrCounts2";
    }

    class GetCount implements Callable<Long> {

        Map<String, Object> param;

        public GetCount(Map<String, Object> param) {
            this.param = param;
        }

        @Override
        public Long call() throws Exception {
            Long count = pnrcountsDataModelRemote.getAllCount(PnrcountsDataModelBean.SUM_BY_DATE, param);
            if (count == null) {
                count = Long.valueOf(0);
            }
            return count;
        }
    }

    class GetNameCount implements Callable<Long> {

        Map<String, Object> param;

        public GetNameCount(Map<String, Object> param) {
            this.param = param;
        }

        @Override
        public Long call() throws Exception {
            Long count = pnrcountsDataModelRemote.getAllCount(PnrcountsDataModelBean.SUM_NAME_BY_DATE, param);
            if (count == null) {
                count = Long.valueOf(0);
            }
            return count;
        }
    }

    class GetWaitCount implements Callable<Long> {

        Map<String, Object> param;

        public GetWaitCount(Map<String, Object> param) {
            this.param = param;
        }

        @Override
        public Long call() throws Exception {
            Long count = pnrcountsDataModelRemote.getAllCount(PnrcountsDataModelBean.SUM_WAIT_BY_DATE, param);
            if (count == null) {
                count = Long.valueOf(0);
            }
            return count;
        }
    }

    class GetPlusCount implements Callable<Long> {

        Map<String, Object> param;

        public GetPlusCount(Map<String, Object> param) {
            this.param = param;
        }

        @Override
        public Long call() throws Exception {
            Long count = pnrcountsDataModelRemote.getAllCount(PnrcountsDataModelBean.SUM_BY_DATE_PLUS, param);
            if (count == null) {
                count = Long.valueOf(0);
            }
            return count;
        }
    }

    class GetMinusCount implements Callable<Long> {

        Map<String, Object> param;

        public GetMinusCount(Map<String, Object> param) {
            this.param = param;
        }

        @Override
        public Long call() throws Exception {
            Long count = pnrcountsDataModelRemote.getAllCount(PnrcountsDataModelBean.SUM_BY_DATE_MINUS, param);
            if (count == null) {
                count = Long.valueOf(0);
            }
            return count;
        }
    }

    public String read2() {
        pnrCount2Map = (Map) dataModel2.getRowData();
        String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
        DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
        param.put("pnrcountsEnd", yearMonthDay);
        List pnrCounts3List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCC, param, 0, -1);
        dataModel3.setWrappedData(pnrCounts3List);
        return "redirect:secure/pnrCounts3";
    }

    public String read3() {
        String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
        pnrCount3Vector = (Object[]) dataModel3.getRowData();
        DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pnrcountsPcc", pnrCount3Vector[0]);
        param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
        param.put("pnrcountsEnd", yearMonthDay);
        List pnrCounts4List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCCSIGNON, param, 0, -1);
        dataModel4.setWrappedData(pnrCounts4List);
        return "redirect:secure/pnrCounts4";
    }

    public String exportToExcel3() {
        ServletOutputStream out = null;
        try {
            String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
            pnrCount3Vector = (Object[]) dataModel3.getRowData();
            String pcc = (String) pnrCount3Vector[0];
            DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsPcc", pcc);
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", yearMonthDay);
            List pnrCounts4List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCCSIGNON, param, 0, -1);

            String filename = yearMonthDay + "-" + pcc;
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

            HSSFRow row = sheet.createRow(0);
            HSSFCell cell = row.createCell((short) 0);
            HSSFRichTextString text = new HSSFRichTextString(messageSource.getString("pccs_pcc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 1);
            text = new HSSFRichTextString(messageSource.getString("desc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 2);
            text = new HSSFRichTextString(messageSource.getString("user_son"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 3);
            text = new HSSFRichTextString(messageSource.getString("month_to_date"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 4);
            text = new HSSFRichTextString(messageSource.getString("waitlist"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 5);
            text = new HSSFRichTextString(messageSource.getString("name"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            int rowNum = 1;
            for (Object obj1 : pnrCounts4List) {
                Object[] pnrCountV = (Object[]) obj1;
                row = sheet.createRow(rowNum);

                for (int idx = 0; idx < 6; idx++) {
                    cell = row.createCell((short) idx);
                    Object value = pnrCountV[idx];
                    if (value.getClass().getSimpleName().equals("String")) {
                        text = new HSSFRichTextString((String) value);
                        cell.setCellValue(text);
                    }
                    if (value.getClass().getSimpleName().equals("BigDecimal")) {
                        BigDecimal bd = (BigDecimal) value;
                        cell.setCellValue(bd.doubleValue());
                    }
                }
                rowNum++;
            }

            wb.write(out);
            fc.responseComplete();
        } catch (Exception ex) {
            Logger.getLogger(PnrCountsBean.class.getName()).log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(PnrCountsBean.class.getName()).log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
            }
        }
        return "secure/pnrCounts3";
    }

    public String exportDetailToExcel3() {
        ServletOutputStream out = null;
        try {
            String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
            pnrCount3Vector = (Object[]) dataModel3.getRowData();
            String pcc = (String) pnrCount3Vector[0];
            DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsPcc", pcc);
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", yearMonthDay);
            List pnrCounts4List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_BY_DATEPCC, param, 0, -1);

            String filename = yearMonthDay + "-" + pcc + "-Detail";
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
            HSSFRichTextString text = new HSSFRichTextString(messageSource.getString("pccs_pcc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 1);
            text = new HSSFRichTextString(messageSource.getString("desc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 2);
            text = new HSSFRichTextString(messageSource.getString("user_son"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 3);
            text = new HSSFRichTextString(messageSource.getString("created"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 4);
            text = new HSSFRichTextString(messageSource.getString("recloc"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 5);
            text = new HSSFRichTextString(messageSource.getString("count_date"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 6);
            text = new HSSFRichTextString(messageSource.getString("departed"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 7);
            text = new HSSFRichTextString(messageSource.getString("month_to_date"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 8);
            text = new HSSFRichTextString(messageSource.getString("waitlist"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            cell = row.createCell((short) 9);
            text = new HSSFRichTextString(messageSource.getString("name"));
            cell.setCellValue(text);
            cell.setCellStyle(boldStyle);

            short rowNum = 1;
            HSSFCellStyle date1CellStyle = wb.createCellStyle();
            HSSFCellStyle date2CellStyle = wb.createCellStyle();
            HSSFDataFormat dataFormat = wb.createDataFormat();
            date1CellStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd"));
            date2CellStyle.setDataFormat(dataFormat.getFormat("yyyy-mm-dd hh:mm:ss"));
            for (Object obj1 : pnrCounts4List) {
                Object[] pnrCountV = (Object[]) obj1;
                row = sheet.createRow((short) rowNum);

                for (int idx = 0; idx < 10; idx++) {
                    cell = row.createCell((short) idx);
                    Object value = pnrCountV[idx];
                    if (value.getClass().getSimpleName().equals("String")) {
                        text = new HSSFRichTextString((String) value);
                        cell.setCellValue(text);
                    }
                    if (value.getClass().getSimpleName().equals("Date")) {
                        Date dt = (Date) value;
                        cell.setCellValue(new java.util.Date(dt.getTime()));
                        cell.setCellStyle(date1CellStyle);
                    }
                    if (value.getClass().getSimpleName().equals("Timestamp")) {
                        Timestamp ts = (Timestamp) value;
                        cell.setCellValue(new java.util.Date(ts.getTime()));
                        cell.setCellStyle(date2CellStyle);
                    }
                    if (value.getClass().getSimpleName().equals("Integer")) {
                        Integer intv = (Integer) value;
                        cell.setCellValue(intv);
                    }
                }
                rowNum++;
            }

            wb.write(out);
            fc.responseComplete();
        } catch (Exception ex) {
            Logger.getLogger(PnrCountsBean.class.getName()).log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
        } finally {
            try {
                out.close();
            } catch (Exception ex) {
                Logger.getLogger(PnrCountsBean.class.getName()).log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
            }
        }
        return "secure/pnrCounts3";
    }

    @SuppressWarnings("unchecked")
    public String read4() {
        String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
        pnrCount4Vector = (Object[]) dataModel4.getRowData();
        DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("pnrcountsPcc", pnrCount4Vector[0]);
        param.put("pnrcountsSignOn", pnrCount4Vector[2]);
        param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
        param.put("pnrcountsEnd", yearMonthDay);
        pnrCounts5List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_BY_DATEPCCSIGNON, param, 0, -1);
        dataModel5.setWrappedData(pnrCounts5List);
        read5SortCreatedAsc = true;
        read5SortCountAsc = true;
        read5SortDepartedAsc = true;
        displayHeadLink = Boolean.TRUE;
        return "redirect:secure/pnrCounts5";
    }

    class Read5ComparatorCreatedAsc implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Date created1 = (Date) o1[0];
            Date created2 = (Date) o2[0];
            return created1.compareTo(created2);
        }
    }

    class Read5ComparatorCreatedDsc implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Date created1 = (Date) o1[0];
            Date created2 = (Date) o2[0];
            return created2.compareTo(created1);
        }
    }

    @SuppressWarnings("unchecked")
    public String sort5Created() {
        if (pnrCounts5List == null) {
            pnrCounts5List = (List<Object[]>) dataModel5.getWrappedData();
        }
        if (read5SortCreatedAsc) {
            Collections.sort(pnrCounts5List, new Read5ComparatorCreatedDsc());
            read5SortCreatedAsc = false;
        } else {
            Collections.sort(pnrCounts5List, new Read5ComparatorCreatedAsc());
            read5SortCreatedAsc = true;
        }
        dataModel5.setWrappedData(pnrCounts5List);
        return "redirect:secure/pnrCounts5";
    }

    class Read5ComparatorCountAsc implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Timestamp count1 = (Timestamp) o1[2];
            Timestamp count2 = (Timestamp) o2[2];
            return count1.compareTo(count2);
        }
    }

    class Read5ComparatorCountDsc implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Timestamp count1 = (Timestamp) o1[2];
            Timestamp count2 = (Timestamp) o2[2];
            return count2.compareTo(count1);
        }
    }

    @SuppressWarnings("unchecked")
    public String sort5Count() {
        if (pnrCounts5List == null) {
            pnrCounts5List = (List<Object[]>) dataModel5.getWrappedData();
        }
        if (read5SortCountAsc) {
            Collections.sort(pnrCounts5List, new Read5ComparatorCountDsc());
            read5SortCountAsc = false;
        } else {
            Collections.sort(pnrCounts5List, new Read5ComparatorCountAsc());
            read5SortCountAsc = true;
        }
        dataModel5.setWrappedData(pnrCounts5List);
        return "redirect:secure/pnrCounts5";
    }

    class Read5ComparatorDepartedAsc implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Date created1 = (Date) o1[3];
            Date created2 = (Date) o2[3];
            return created1.compareTo(created2);
        }
    }

    class Read5ComparatorDepartedDsc implements Comparator<Object[]> {

        @Override
        public int compare(Object[] o1, Object[] o2) {
            Date created1 = (Date) o1[3];
            Date created2 = (Date) o2[3];
            return created2.compareTo(created1);
        }
    }

    @SuppressWarnings("unchecked")
    public String sort5Departed() {
        if (pnrCounts5List == null) {
            pnrCounts5List = (List<Object[]>) dataModel5.getWrappedData();
        }
        if (read5SortDepartedAsc) {
            Collections.sort(pnrCounts5List, new Read5ComparatorDepartedDsc());
            read5SortDepartedAsc = false;
        } else {
            Collections.sort(pnrCounts5List, new Read5ComparatorDepartedAsc());
            read5SortDepartedAsc = true;
        }
        dataModel5.setWrappedData(pnrCounts5List);
        return "redirect:secure/pnrCounts5";
    }

    public String transferToPoint() {
        pnrCount1Vector = (Object[]) dataModel1.getRowData();
        String yearMonth = (String) pnrCount1Vector[0];
        segmentCounterServiceRemote.transferToPoint(yearMonth);
        return "redirect:secure/pnrCounts1TransferSuccess";
    }

    public String findByPcc() {
        if (!pnrCountsPcc.equals("")) {
            String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
            DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", yearMonthDay);
            param.put("pnrcountsPcc", pnrCountsPcc);
            List pnrCounts3List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCCFIND, param, 0, -1);
            dataModel3.setWrappedData(pnrCounts3List);
        } else {
            String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
            DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", yearMonthDay);
            List pnrCounts3List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCC, param, 0, -1);
            dataModel3.setWrappedData(pnrCounts3List);
        }
        return "redirect:secure/pnrCounts3";
    }

    public String findByPccDesc() {
        if (!pnrCountsPccDesc.equals("")) {
            String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
            DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", yearMonthDay);
            param.put("pnrCountsPccDesc", pnrCountsPccDesc + "%");
            List pnrCounts3List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCCDESCFIND, param, 0, -1);
            dataModel3.setWrappedData(pnrCounts3List);
        } else {
            String yearMonthDay = (String) pnrCount2Map.get("yearmonthday");
            DateTime firstDayOfMonth = dtfYMD.parseDateTime(yearMonthDay.substring(0, yearMonthDay.length() - 2) + "01");
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsStart", dtfYMD.print(firstDayOfMonth));
            param.put("pnrcountsEnd", yearMonthDay);
            List pnrCounts3List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCC, param, 0, -1);
            dataModel3.setWrappedData(pnrCounts3List);
        }
        return "redirect:secure/pnrCounts3";
    }

    public String findByPcc2() {
        if (!pnrCountsPcc.equals("")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrcountsPcc", pnrCountsPcc);
            List pnrCounts6List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCCFIND2, param, 0, -1);
            dataModel6.setWrappedData(pnrCounts6List);
        } else {
            List pnrCounts6List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_ALL_BY_PCC, null, 0, -1);
            dataModel6.setWrappedData(pnrCounts6List);
        }
        setPieDataSet();
        return "redirect:secure/pnrCounts6";
    }

    public String findByPccDesc2() {
        if (!pnrCountsPccDesc.equals("")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pnrCountsPccDesc", pnrCountsPccDesc + "%");
            List pnrCounts6List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCCDESCFIND2, param, 0, -1);
            dataModel6.setWrappedData(pnrCounts6List);
        } else {
            List pnrCounts6List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_ALL_BY_PCC, null, 0, -1);
            dataModel6.setWrappedData(pnrCounts6List);
        }
        setPieDataSet();
        return "redirect:secure/pnrCounts6";
    }

    public String pnrCounts6() {
        List pnrCounts6List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_ALL_BY_PCC, null, 0, -1);
        dataModel6.setWrappedData(pnrCounts6List);
        setPieDataSet();
        return "redirect:secure/pnrCounts6";
    }

    private void setPieDataSet() {
        List pnrCounts6List = (List) dataModel6.getWrappedData();
        pieDataset = new DefaultPieDataset();
        int nSpecific = 12;
        for (int idx = 0; idx < nSpecific; idx++) {
            if (idx < pnrCounts6List.size()) {
                Object[] pnrCount = (Object[]) pnrCounts6List.get(idx);
                String pcc = (String) pnrCount[0];
                BigDecimal monthToDate = (BigDecimal) pnrCount[2];
                pieDataset.setValue(pcc, monthToDate);
            }
        }
        if (pnrCounts6List.size() > nSpecific) {
            BigDecimal monthToDateOther = new BigDecimal(BigInteger.ZERO);
            for (int idx = nSpecific; idx < pnrCounts6List.size(); idx++) {
                Object[] pnrCount = (Object[]) pnrCounts6List.get(idx);
                BigDecimal monthToDate = (BigDecimal) pnrCount[2];
                monthToDateOther = monthToDateOther.add(monthToDate);
            }
            pieDataset.setValue("Other", monthToDateOther);
        }
    }

    public String sort7() {
        List pnrCounts7List;
        if (read7SortAsc) {
            pnrCounts7List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCC_YEARMONTH_DSC, param7, 0, -1);
            read7SortAsc = false;
        } else {
            pnrCounts7List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCC_YEARMONTH, param7, 0, -1);
            read7SortAsc = true;
        }
        dataModel7.setWrappedData(pnrCounts7List);
        return "redirect:secure/pnrCounts7";
    }

    public String read7() {
        Object[] pnrCount6ObjArray = (Object[]) dataModel6.getRowData();
        param7 = new HashMap<String, Object>();
        param7.put("pnrcountsPcc", pnrCount6ObjArray[0]);
        List pnrCounts7List = pnrcountsDataModelRemote.getAll(PnrcountsDataModelBean.SELECT_GROUP_BY_PCC_YEARMONTH_DSC, param7, 0, -1);
        read7SortAsc = false;
        dataModel7.setWrappedData(pnrCounts7List);

        timeSeriesDataSet2 = new TimeSeriesCollection();
        TimeSeries timeSeries1 = new TimeSeries("Month To Date", Month.class);
        for (int idx = 0; idx < pnrCounts7List.size(); idx++) {
            Object[] pnrCount7ObjArray = (Object[]) pnrCounts7List.get(idx);
            String yearMonth = (String) pnrCount7ObjArray[2];
            BigDecimal count = (BigDecimal) pnrCount7ObjArray[3];
            Month month = new Month(Integer.valueOf(yearMonth.substring(4, 6)), Integer.valueOf(yearMonth.substring(0, 4)));
            timeSeries1.add(month, count);
        }
        timeSeriesDataSet2.addSeries(timeSeries1);
        return "redirect:secure/pnrCounts7";
    }

    public DefaultPieDataset getPieDataset() {
        return pieDataset;
    }

    public void setPieDataset(DefaultPieDataset pieDataset) {
        this.pieDataset = pieDataset;
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

    public ListDataModel getDataModel1() {
        return dataModel1;
    }

    public ListDataModel getDataModel2() {
        return dataModel2;
    }

    public ListDataModel getDataModel3() {
        return dataModel3;
    }

    public ListDataModel getDataModel4() {
        return dataModel4;
    }

    public ListDataModel getDataModel5() {
        return dataModel5;
    }

    public ListDataModel getDataModel6() {
        return dataModel6;
    }

    public ListDataModel getDataModel7() {
        return dataModel7;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public PnrcountsDataModelRemote getPnrcountsDataModelRemote() {
        return pnrcountsDataModelRemote;
    }

    public void setPnrcountsDataModelRemote(PnrcountsDataModelRemote pnrcountsDataModelRemote) {
        this.pnrcountsDataModelRemote = pnrcountsDataModelRemote;
    }

    public Object[] getSegCount1Vector() {
        return pnrCount1Vector;
    }

    public void setSegCount1Vector(Object[] segCount1Vector) {
        this.pnrCount1Vector = segCount1Vector;
    }

    public TimeSeriesCollection getTimeSeriesDataSet1() {
        return timeSeriesDataSet1;
    }

    public void setTimeSeriesDataSet1(TimeSeriesCollection timeSeriesDataSet1) {
        this.timeSeriesDataSet1 = timeSeriesDataSet1;
    }

    public TimeSeriesCollection getTimeSeriesDataSet2() {
        return timeSeriesDataSet2;
    }

    public void setTimeSeriesDataSet2(TimeSeriesCollection timeSeriesDataSet2) {
        this.timeSeriesDataSet2 = timeSeriesDataSet2;
    }

    public Map getPnrCount2Map() {
        return pnrCount2Map;
    }

    public void setPnrCount2Map(Map pnrCount2Map) {
        this.pnrCount2Map = pnrCount2Map;
    }

    public Object[] getPnrCount3Vector() {
        return pnrCount3Vector;
    }

    public void setPnrCount3Vector(Object[] pnrCount3Vector) {
        this.pnrCount3Vector = pnrCount3Vector;
    }

    public Object[] getPnrCount4Vector() {
        return pnrCount4Vector;
    }

    public void setPnrCount4Vector(Object[] pnrCount4Vector) {
        this.pnrCount4Vector = pnrCount4Vector;
    }

    public Boolean getDisplayHeadLink() {
        return displayHeadLink;
    }

    public void setDisplayHeadLink(Boolean displayHeadLink) {
        this.displayHeadLink = displayHeadLink;
    }

    public String getPnrCountsPcc() {
        return pnrCountsPcc;
    }

    public void setPnrCountsPcc(String pnrCountsPcc) {
        this.pnrCountsPcc = pnrCountsPcc;
    }

    public String getPnrCountsPccDesc() {
        return pnrCountsPccDesc;
    }

    public void setPnrCountsPccDesc(String pnrCountsPccDesc) {
        this.pnrCountsPccDesc = pnrCountsPccDesc;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
