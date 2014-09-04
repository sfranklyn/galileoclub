/*
 * PccsBean.java
 * 
 * Created on Nov 18, 2008, 2:18:19 PM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.datamodel.PccsDataModelBean;
import galileoclub.ejb.datamodel.PccsDataModelRemote;
import galileoclub.ejb.service.PccsServiceRemote;
import galileoclub.ejb.service.SegmentCounterServiceRemote;
import galileoclub.jpa.Pccs;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class PccsBean {

    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private Pccs pccs = new Pccs();
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    private PccsDataModelRemote pccsDataModelRemote = null;
    @EJB
    private PccsServiceRemote pccsServiceRemote = null;
    @EJB
    private SegmentCounterServiceRemote segmentCounterServiceRemote = null;

    public String create() {
        pccs = new Pccs();
        return "redirect:secure/pccsCreate";
    }

    public String saveCreate() {
        String result = "secure/pccsCreate";

        List<String> errorList = pccsServiceRemote.saveCreate(pccs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/pccs";
            pccs = new Pccs();
        }

        return result;
    }

    public String delete() {
        pccs = (Pccs) dataModel.getRowData();
        return "redirect:secure/pccsDelete";
    }

    public String saveDelete() {
        String result = "secure/pccsDelete";

        List<String> errorList = pccsServiceRemote.saveDelete(pccs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/pccs";
        }

        return result;
    }

    public String read() {
        pccs = (Pccs) dataModel.getRowData();
        return "redirect:secure/pccsRead";
    }

    public String update() {
        pccs = (Pccs) dataModel.getRowData();
        return "redirect:secure/pccsUpdate";
    }

    public String saveUpdate() {
        String result = "secure/pccsUpdate";

        List<String> errorList = pccsServiceRemote.saveUpdate(pccs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/pccs";
        }

        return result;
    }

    public String createPccs() {
        pccsServiceRemote.createPccs();
        return "redirect:secure/pccs";
    }

    class ExecuteJob implements Runnable {

        @Override
        public void run() {
            segmentCounterServiceRemote.countSegment();
        }
    }

    public String countSegment() {
        ExecutorService es = Executors.newSingleThreadExecutor();
        ExecuteJob executeJob = new ExecuteJob();
        es.submit(executeJob);
        es.shutdown();
        return "redirect:secure/segmentCounter";
    }

    public String findByPcc() {
        if (!pccs.getPccsPcc().equals("")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("pccsPcc", pccs.getPccsPcc());
            dataModel.setSelect(PccsDataModelBean.SELECT_BY_PCC);
            dataModel.setSelectCount(PccsDataModelBean.SELECT_BY_PCC_COUNT);
            dataModel.setSelectParam(param);
            dataModel.setWrappedData(pccsDataModelRemote);
        } else {
            dataModel.setSelect(PccsDataModelBean.SELECT_ALL);
            dataModel.setSelectCount(PccsDataModelBean.SELECT_ALL_COUNT);
            dataModel.setSelectParam(null);
            dataModel.setWrappedData(pccsDataModelRemote);
        }
        return "redirect:secure/pccs";
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public DatabaseDataModel getDataModel() {
        return dataModel;
    }

    public Pccs getPccs() {
        return pccs;
    }

    public void setPccs(Pccs pccs) {
        this.pccs = pccs;
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

    public PccsDataModelRemote getPccsDataModelRemote() {
        return pccsDataModelRemote;
    }

    @EJB
    public void setPccsDataModelRemote(PccsDataModelRemote pccsDataModelRemote) {
        dataModel.setSelect(PccsDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(PccsDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(pccsDataModelRemote);
        this.pccsDataModelRemote = pccsDataModelRemote;
    }

    public PccsServiceRemote getPccsServiceRemote() {
        return pccsServiceRemote;
    }

    public void setPccsServiceRemote(PccsServiceRemote pccsServiceRemote) {
        this.pccsServiceRemote = pccsServiceRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
