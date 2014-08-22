/*
 * FixDatabase2.java
 * 
 * Created on Mar 15, 2010, 10:46:22 AM
 */
package galileoclub.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samuel Franklyn
 */
public class FixDatabase2 implements Runnable {

    private static final Logger log = Logger.getLogger(MigrateDatabase.class.getName());
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/galileoclub";

    public static void main(final String args[]) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        FixDatabase2 fixDatabase2 = new FixDatabase2();
        es.submit(fixDatabase2);
        es.shutdown();
    }

    @Override
    public void run() {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, "galileoclub", "galileoclub");

            final String deleteDoubleCount = "delete from pnrcounts " +
                    "where " +
                    "pnrcounts_id=?";
            PreparedStatement psDeleteDoubleCount = conn.prepareStatement(deleteDoubleCount);

            final String selectDoubleCount = "select "+
                "pnrcounts_id, "+
                "pnrcounts.pnrcounts_countdate, "+
                "pnrcounts.pnrcounts_recloc, "+
                "pnrcounts.pnrcounts_count, "+
                "pnrcounts.pnrcounts_waitcount, "+
                "pnrcounts.pnrcounts_pcc, "+
                "pnrcounts.pnrcounts_signon, "+
                "pnrcounts.pnrcounts_namecount "+
                "from pnrcounts join pnrcounts_double on "+
                "pnrcounts.pnrcounts_recloc=pnrcounts_double.pnrcounts_recloc "+
                "where  "+
                "pnrcounts.pnrcounts_yearmonth='201012' and "+
                "pnrcounts.pnrcounts_countdate>'2010-12-16' and "+
                "pnrcounts.pnrcounts_count>0 ";
            PreparedStatement psSelectDoubleCount = conn.prepareStatement(selectDoubleCount);

            ResultSet rsSelectDoubleCount = psSelectDoubleCount.executeQuery();
            while (rsSelectDoubleCount.next()) {
                Integer id = rsSelectDoubleCount.getInt(1);
                psDeleteDoubleCount.setInt(1, id);
                psDeleteDoubleCount.executeUpdate();
            }

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }
}
