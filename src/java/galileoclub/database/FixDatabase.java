/*
 * FixDatabase.java
 * 
 * Created on Feb 16, 2009, 3:37:21 PM
 */
package galileoclub.database;

import java.sql.Connection;
import java.sql.Date;
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
public class FixDatabase implements Runnable {

    private static final Logger log = Logger.getLogger(MigrateDatabase.class.getName());
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String sourceUrl = "jdbc:mysql://localhost:3306/giws";
    private static final String destinationUrl = "jdbc:mysql://localhost:3306/galileoclub";

    public static void main(final String args[]) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        FixDatabase fixDatabase = new FixDatabase();
        es.submit(fixDatabase);
        es.shutdown();
    }

    @Override
    public void run() {
        Connection connSource = null;
        Connection connDestination = null;
        try {
            Class.forName(driver);
            connSource = DriverManager.getConnection(sourceUrl, "giws", "giws");
            connSource.setReadOnly(true);
            connDestination = DriverManager.getConnection(destinationUrl, "galileoclub", "galileoclub");

            final String selectThreeLetterPCC = "select " +
                    "pnrs_id, " + //1
                    "pnrs_pcc," + //2
                    "pnrs_signon," + //3
                    "pnrs_recloc," + //4
                    "pnrs_created," + //5
                    "pnrs_departed," + //6
                    "pnrs_active," + //7
                    "pnrs_new," + //8
                    "pnrs_count," + //9
                    "pnrs_waitcount " + //10
                    "from pnrs " +
                    "where " +
                    "length(pnrs_pcc)=3";
            final String selectFourLetterPCC = "select " +
                    "pnrs_id, " +
                    "pnrs_pcc," +
                    "pnrs_signon," +
                    "pnrs_recloc," +
                    "pnrs_created," +
                    "pnrs_departed," +
                    "pnrs_active," +
                    "pnrs_new," +
                    "pnrs_count," +
                    "pnrs_waitcount " +
                    "from pnrs " +
                    "where " +
                    "pnrs_pcc=? and " +
                    "pnrs_recloc=? and " +
                    "pnrs_created=?";
            final String updateThreeLetterPCC = "update pnrs set " +
                    "pnrs_pcc=? " + //1
                    "where " +
                    "pnrs_id=?"; //2
            PreparedStatement psSelectThreeLetterPCC = connDestination.prepareStatement(selectThreeLetterPCC);
            ResultSet rsSelectThreeLetterPCC = psSelectThreeLetterPCC.executeQuery();
            PreparedStatement psSelectFourLetterPCC = connDestination.prepareStatement(selectFourLetterPCC);
            PreparedStatement psUpdateThreeLetterPCC = connDestination.prepareStatement(updateThreeLetterPCC);
            while (rsSelectThreeLetterPCC.next()) {
                Integer id3 = rsSelectThreeLetterPCC.getInt(1);
                String pcc3 = rsSelectThreeLetterPCC.getString(2);
                String recLoc3 = rsSelectThreeLetterPCC.getString(4);
                Date created3 = rsSelectThreeLetterPCC.getDate(5);
                psSelectFourLetterPCC.setString(1, "0"+pcc3);
                psSelectFourLetterPCC.setString(2, recLoc3);
                psSelectFourLetterPCC.setDate(3, created3);
                ResultSet rsSelectFourLetterPCC = psSelectFourLetterPCC.executeQuery();
                if (!rsSelectFourLetterPCC.next()) {
                    psUpdateThreeLetterPCC.setString(1, "0"+pcc3);
                    psUpdateThreeLetterPCC.setInt(2, id3);
                    psUpdateThreeLetterPCC.executeUpdate();
                }
            }
            final String deleteThreeLetterPCC = "delete from pnrs " +
                    "where " +
                    "length(pnrs_pcc)=3";
            PreparedStatement psDeleteThreeLetterPCC = connDestination.prepareStatement(deleteThreeLetterPCC);
            psDeleteThreeLetterPCC.executeUpdate();

            migratePnrcounts(connSource, connDestination);

            final String updatePnrcounts =
                    "update pnrcounts " +
                    "set pnrcounts_pcc=concat('0',pnrcounts_pcc) " +
                    "where " +
                    "length(pnrcounts_pcc)=3";
            PreparedStatement psUpdatePnrcounts = connDestination.prepareStatement(updatePnrcounts);
            psUpdatePnrcounts.executeUpdate();

        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try {
                connSource.close();
            } catch (SQLException ex) {
                log.log(Level.SEVERE, null, ex);
            }
            try {
                connDestination.close();
            } catch (SQLException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }

    private void migratePnrcounts(Connection connSource, Connection connDestination) throws SQLException {
        final String selectSegCount7 =
                "select " +
                "segcount7_countdate," +
                "segcount7_yearmonth," +
                "segcount7_recloc," +
                "segcount7_pcc," +
                "segcount7_signon," +
                "segcount7_count," +
                "segcount7_waitcount," +
                "segcount7_created," +
                "segcount7_departed," +
                "segcount7_yearmonthday " +
                "from segcount7 where " +
                "segcount7_yearmonthday='20090209'";
        final String deletePnrcounts =
                "delete from pnrcounts where " +
                "pnrcounts_yearmonthday='20090209'";
        final String insertPnrcounts =
                "insert into pnrcounts (" +
                "pnrcounts_countdate," + //1
                "pnrcounts_yearmonth," + //2
                "pnrcounts_recloc," + //3
                "pnrcounts_pcc," + //4
                "pnrcounts_signon," + //5
                "pnrcounts_count," + //6
                "pnrcounts_waitcount," + //7
                "pnrcounts_created," + //8
                "pnrcounts_departed," + //9
                "pnrcounts_yearmonthday " + //10
                ") values (" +
                "?," + //1
                "?," + //2
                "?," + //3
                "?," + //4
                "?," + //5
                "?," + //6
                "?," + //7
                "?," + //8
                "?," + //9
                "? " + //10
                ")";
        PreparedStatement psSelectSegCount7 = connSource.prepareStatement(selectSegCount7,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        psSelectSegCount7.setFetchSize(Integer.MIN_VALUE);

        PreparedStatement psDeletePnrcounts = connDestination.prepareStatement(deletePnrcounts);
        psDeletePnrcounts.executeUpdate();

        PreparedStatement psInsertPnrcounts = connDestination.prepareStatement(insertPnrcounts);
        ResultSet rsSelectSegCount7 = psSelectSegCount7.executeQuery();
        while (rsSelectSegCount7.next()) {
            psInsertPnrcounts.setTimestamp(1, rsSelectSegCount7.getTimestamp(1));
            psInsertPnrcounts.setString(2, rsSelectSegCount7.getString(2));
            psInsertPnrcounts.setString(3, rsSelectSegCount7.getString(3));
            psInsertPnrcounts.setString(4, rsSelectSegCount7.getString(4));
            psInsertPnrcounts.setString(5, rsSelectSegCount7.getString(5));
            psInsertPnrcounts.setInt(6, rsSelectSegCount7.getInt(6));
            psInsertPnrcounts.setInt(7, rsSelectSegCount7.getInt(7));
            psInsertPnrcounts.setDate(8, rsSelectSegCount7.getDate(8));
            psInsertPnrcounts.setDate(9, rsSelectSegCount7.getDate(9));
            psInsertPnrcounts.setString(10, rsSelectSegCount7.getString(10));
            try {
                psInsertPnrcounts.executeUpdate();
            } catch (SQLException ex) {
                if (!ex.toString().contains("Duplicate entry")) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
