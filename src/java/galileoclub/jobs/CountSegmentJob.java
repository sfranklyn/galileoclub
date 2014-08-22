/*
 * CountSegmentJob.java
 * 
 * Created on Dec 24, 2008, 9:23:39 AM
 */
package galileoclub.jobs;

import galileoclub.ejb.service.SegmentCounterServiceRemote;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Samuel Franklyn
 */
public class CountSegmentJob implements Job {

    private static final Logger log = Logger.getLogger(CountSegmentJob.class.getName());

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        try {
            InitialContext ic = new InitialContext();
            SegmentCounterServiceRemote segmentCounterServiceRemote = (SegmentCounterServiceRemote) ic.lookup("galileoclub.ejb.service.SegmentCounterServiceRemote");
            segmentCounterServiceRemote.countSegment();
        } catch (NamingException ex) {
            log.log(Level.SEVERE, "GCLUB0001:" + ex.toString(), ex);
        }
    }
}
