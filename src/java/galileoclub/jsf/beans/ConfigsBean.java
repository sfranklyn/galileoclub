/*
 * ConfigsBean.java
 * 
 * Created on Nov 24, 2008, 5:42:02 PM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.datamodel.ConfigsDataModelBean;
import galileoclub.ejb.datamodel.ConfigsDataModelRemote;
import galileoclub.ejb.service.ConfigsServiceBean;
import galileoclub.ejb.service.ConfigsServiceRemote;
import galileoclub.jobs.CountSegmentJob;
import galileoclub.jpa.Configs;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Samuel Franklyn
 */
public class ConfigsBean {

    private static final Logger log = Logger.getLogger(ConfigsBean.class.getName());
    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private Configs configs = null;
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    @EJB
    private ConfigsDataModelRemote configsDataModelRemote;
    @EJB
    private ConfigsServiceRemote configsServiceRemote;

    private void reschedule() {
        if (configs.getConfigKey().equals(ConfigsServiceBean.SEGCOUNT_TIME)) {
            try {
                ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
                StdSchedulerFactory ssf = (StdSchedulerFactory) sc.getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
                Scheduler scheduler = ssf.getScheduler();
                scheduler.standby();
                scheduler.unscheduleJob("CountSegmentJob", Scheduler.DEFAULT_GROUP);
                scheduler.deleteJob("CountSegmentJob", Scheduler.DEFAULT_GROUP);
                JobDetail jobDetail = new JobDetail("CountSegmentJob", Scheduler.DEFAULT_GROUP, CountSegmentJob.class);
                CronTrigger trigger = new CronTrigger("CountSegmentJobTrigger", Scheduler.DEFAULT_GROUP, configs.getConfigValue());
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            } catch (Exception ex) {
                log.log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
            }
        }
    }

    public String create() {
        configs = new Configs();
        return "redirect:secure/configsCreate";
    }

    public String saveCreate() {
        String result = "secure/configsCreate";

        List<String> errorList = configsServiceRemote.saveCreate(configs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/configs";
            reschedule();
        }

        return result;
    }

    public String read() {
        configs = (Configs) dataModel.getRowData();
        return "redirect:secure/configsRead";
    }

    public String update() {
        configs = (Configs) dataModel.getRowData();
        return "redirect:secure/configsUpdate";
    }

    public String saveUpdate() {
        String result = "secure/configsUpdate";

        List<String> errorList = configsServiceRemote.saveUpdate(configs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/configs";
            reschedule();
        }

        return result;
    }

    public String delete() {
        configs = (Configs) dataModel.getRowData();
        return "redirect:secure/configsDelete";
    }

    public String saveDelete() {
        String result = "secure/configsDelete";

        List<String> errorList = configsServiceRemote.saveDelete(configs, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/configs";
            if (configs.getConfigKey().equals(ConfigsServiceBean.SEGCOUNT_TIME)) {
                try {
                    ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
                    StdSchedulerFactory ssf = (StdSchedulerFactory) sc.getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
                    Scheduler scheduler = ssf.getScheduler();
                    scheduler.standby();
                    scheduler.unscheduleJob("CountSegmentJob", Scheduler.DEFAULT_GROUP);
                    scheduler.deleteJob("CountSegmentJob", Scheduler.DEFAULT_GROUP);
                    scheduler.start();
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
                }
            }
        }

        return result;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public DatabaseDataModel getDataModel() {
        dataModel.setSelect(ConfigsDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(ConfigsDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(configsDataModelRemote);
        return dataModel;
    }

    public Configs getConfigs() {
        return configs;
    }

    public void setConfigs(Configs configs) {
        this.configs = configs;
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

    public ConfigsDataModelRemote getConfigsDataModelRemote() {
        return configsDataModelRemote;
    }

    public void setConfigsDataModelRemote(ConfigsDataModelRemote configsDataModelRemote) {
        this.configsDataModelRemote = configsDataModelRemote;
    }

    public ConfigsServiceRemote getConfigsServiceRemote() {
        return configsServiceRemote;
    }

    public void setConfigsServiceRemote(ConfigsServiceRemote configsServiceRemote) {
        this.configsServiceRemote = configsServiceRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
