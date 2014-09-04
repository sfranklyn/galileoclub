/*
 * ContextListener.java
 * 
 * Created on Nov 21, 2008, 1:21:34 PM
 */
package galileoclub.listener;

import galileoclub.ejb.service.ConfigsServiceBean;
import galileoclub.ejb.service.ConfigsServiceRemote;
import galileoclub.jobs.CountSegmentJob;
import galileoclub.jpa.Configs;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Web application lifecycle listener.
 * @author Samuel Franklyn
 */
public class ContextListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(ContextListener.class.getName());
    @EJB
    private ConfigsServiceRemote configsServiceRemote = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServletContext sc = sce.getServletContext();
            log.info("GCLUB0001: Context initialized " + sc.getContextPath());
            Configs configs = configsServiceRemote.getByKey(ConfigsServiceBean.SEGCOUNT_SIGNON_PASSWORD);
            if (configs == null) {
                configs = new Configs();
                configs.setConfigKey(ConfigsServiceBean.SEGCOUNT_SIGNON_PASSWORD);
                configs.setConfigDesc("Sign on and password for Segment Counter.For example AA/PWD");
                configs.setConfigType("string");
                configs.setConfigValue("N36975/AUG2014");
                configsServiceRemote.saveCreate(configs, Locale.getDefault());
            }
            configs = configsServiceRemote.getByKey(ConfigsServiceBean.SEGCOUNT_HCM);
            if (configs == null) {
                configs = new Configs();
                configs.setConfigKey(ConfigsServiceBean.SEGCOUNT_HCM);
                configs.setConfigDesc("HCM for Segment Counter.For example Galileo2");
                configs.setConfigType("string");
                configs.setConfigValue("Galileo2");
                configsServiceRemote.saveCreate(configs, Locale.getDefault());
            }
            configs = configsServiceRemote.getByKey(ConfigsServiceBean.SEGCOUNT_TIME);
            if (configs == null) {
                /*
                configs = new Configs();
                configs.setConfigKey(ConfigsServiceBean.SEGCOUNT_TIME);
                configs.setConfigDesc("Day of the month and time for Segment Counter.");
                configs.setConfigType("cron");
                configs.setConfigValue("0 0 1 * * ?");
                configsServiceRemote.saveCreate(configs, Locale.getDefault());
                 */
            } else {
                StdSchedulerFactory ssf = (StdSchedulerFactory) sc.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);
                log.info("StdSchedulerFactory:" + ssf.toString());
                Scheduler scheduler = ssf.getScheduler();
                scheduler.standby();
                JobDetail jobDetail = new JobDetail("CountSegmentJob", Scheduler.DEFAULT_GROUP, CountSegmentJob.class);
                CronTrigger trigger = new CronTrigger("CountSegmentJobTrigger", Scheduler.DEFAULT_GROUP, configs.getConfigValue());
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            }
            /*
            configs = configsServiceRemote.getByKey(ConfigsServiceBean.ADMIN_MOBILE);
            if (configs == null) {
                configs = new Configs();
                configs.setConfigKey(ConfigsServiceBean.ADMIN_MOBILE);
                configs.setConfigDesc("Admin mobile phone number");
                configs.setConfigType("string");
                configs.setConfigValue("+6283899294520");
                configsServiceRemote.saveCreate(configs, Locale.getDefault());
            }
            configs = configsServiceRemote.getByKey(ConfigsServiceBean.ADMIN_MAIL);
            if (configs == null) {
                configs = new Configs();
                configs.setConfigKey(ConfigsServiceBean.ADMIN_MAIL);
                configs.setConfigDesc("Admin mail address");
                configs.setConfigType("string");
                configs.setConfigValue("pokian@gmail.com");
                configsServiceRemote.saveCreate(configs, Locale.getDefault());
            }
             */
        } catch (Exception ex) {
            log.log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("GCLUB0001: Context destroyed " + sce.getServletContext().getContextPath());
    }
}