/*
 * MigrateDatabase.java
 * 
 * Created on Dec 2, 2008, 9:22:58 AM
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
public class MigrateDatabase implements Runnable {

    private static final Logger log = Logger.getLogger(MigrateDatabase.class.getName());
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String sourceUrl = "jdbc:mysql://localhost:3306/giws";
    private static final String destinationUrl = "jdbc:mysql://localhost:3306/galileoclub";

    public static void main(final String args[]) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        MigrateDatabase migrateDatabase = new MigrateDatabase();
        es.submit(migrateDatabase);
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

            migratePccs(connSource, connDestination);
            migratePnrs(connSource, connDestination);
            migratePnrcounts(connSource, connDestination);

            final String updatePccs =
                    "update pccs " +
                    "set pccs_pcc=concat('0',pccs_pcc) " +
                    "where " +
                    "length(pccs_pcc)=3";
            PreparedStatement psUpdatePccs = connDestination.prepareStatement(updatePccs);
            psUpdatePccs.executeUpdate();

            final String updatePnrs =
                    "update pnrs " +
                    "set pnrs_pcc=concat('0',pnrs_pcc) " +
                    "where " +
                    "length(pnrs_pcc)=3";
            PreparedStatement psUpdatePnrs = connDestination.prepareStatement(updatePnrs);
            psUpdatePnrs.executeUpdate();

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

    private void migratePccs(Connection connSource, Connection connDestination) throws SQLException {
        final String deletePccs =
                "delete " +
                "from pccs";
        final String selectSegCount3 =
                "select " +
                "segcount3_pcc," + //1
                "segcount3_desc " + //2
                "from segcount3";
        final String insertPccs =
                "insert into pccs (" +
                "pccs_pcc," + //1
                "pccs_desc" + //2
                ") values (" +
                "?," + //1
                "?" + //2
                ")";
        PreparedStatement psSelectSegCount3 = connSource.prepareStatement(selectSegCount3);

        PreparedStatement psDeletePccs = connDestination.prepareStatement(deletePccs);
        psDeletePccs.executeUpdate();

        PreparedStatement psInsertPccs = connDestination.prepareStatement(insertPccs);
        ResultSet rsSelectSegCount3 = psSelectSegCount3.executeQuery();
        while (rsSelectSegCount3.next()) {
            psInsertPccs.setString(1, rsSelectSegCount3.getString(1));
            psInsertPccs.setString(2, rsSelectSegCount3.getString(2));
            try {
                psInsertPccs.executeUpdate();
            } catch (SQLException ex) {
                if (!ex.toString().contains("Duplicate entry")) {
                    log.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void migratePnrs(Connection connSource, Connection connDestination) throws SQLException {
        final String selectSegCount1 =
                "select " +
                "segcount1_pcc," + //1
                "segcount1_signon," + //2
                "segcount1_recloc," + //3
                "segcount1_created," + //4
                "segcount1_departed," + //5
                "segcount1_active," + //6
                "segcount1_new," + //7
                "segcount1_count," + //8
                "segcount1_waitcount " + //9
                "from segcount1";
        final String deletePnrs =
                "delete from pnrs";
        final String insertPnrs =
                "insert into pnrs (" +
                "pnrs_pcc," + //1
                "pnrs_signon," + //2
                "pnrs_recloc," + //3
                "pnrs_created," + //4
                "pnrs_departed," + //5
                "pnrs_active," + //6
                "pnrs_new," + //7
                "pnrs_count," + //8
                "pnrs_waitcount " + //9
                ") values (" +
                "?," + //1
                "?," + //2
                "?," + //3
                "?," + //4
                "?," + //5
                "?," + //6
                "?," + //7
                "?," + //8
                "?" + //9
                ")";

        PreparedStatement psSelectSegCount1 = connSource.prepareStatement(selectSegCount1,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        psSelectSegCount1.setFetchSize(Integer.MIN_VALUE);

        PreparedStatement psDeletePnrs = connDestination.prepareStatement(deletePnrs);
        psDeletePnrs.executeUpdate();

        PreparedStatement psInsertPnrs = connDestination.prepareStatement(insertPnrs);
        ResultSet rsSelectSegCount1 = psSelectSegCount1.executeQuery();
        while (rsSelectSegCount1.next()) {
            psInsertPnrs.setString(1, rsSelectSegCount1.getString(1));
            psInsertPnrs.setString(2, rsSelectSegCount1.getString(2));
            psInsertPnrs.setString(3, rsSelectSegCount1.getString(3));
            psInsertPnrs.setDate(4, rsSelectSegCount1.getDate(4));
            psInsertPnrs.setDate(5, rsSelectSegCount1.getDate(5));
            psInsertPnrs.setBoolean(6, rsSelectSegCount1.getBoolean(6));
            psInsertPnrs.setBoolean(7, rsSelectSegCount1.getBoolean(7));
            psInsertPnrs.setInt(8, rsSelectSegCount1.getInt(8));
            psInsertPnrs.setInt(9, rsSelectSegCount1.getInt(9));
            try {
                psInsertPnrs.executeUpdate();
            } catch (SQLException ex) {
                if (!ex.toString().contains("Duplicate entry")) {
                    log.log(Level.SEVERE, null, ex);
                }
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
                "from segcount7";
        final String deletePnrcounts =
                "delete from pnrcounts";
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
