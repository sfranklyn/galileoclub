/*
 * UpgradeDatabase1.java
 * 
 * Created on Feb 18, 2009, 5:01:39 PM
 */
package galileoclub.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Samuel Franklyn
 */
public class UpgradeDatabase1 implements Runnable {

    private static final Logger log = Logger.getLogger(UpgradeDatabase1.class.getName());
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/galileoclub";
    private static final String selectRoleIdByRoleName = "" +
            "select role_id from roles where " +
            "role_name=?";
    private static final String insertRoles =
            "insert into roles (" +
            "role_name," +
            "role_desc," +
            "role_menu," +
            "role_version" +
            ") values (" +
            "?," +
            "?," +
            "?," +
            "?" +
            ")";
    private static final String updateUsersRoles = "" +
            "update users_roles set " +
            "role_id=? " +
            "where " +
            "user_id=? and " +
            "role_id=?";
    private static final String selectUserIdByUserName = "" +
            "select user_id from users where " +
            "user_name=?";
    private static final String deleteUsersRoles = "" +
            "delete from users_roles " +
            "where " +
            "user_id=? and " +
            "role_id=?";
    private final String insertUrlsRoles =
            "insert into urls_roles (" +
            "url_role," +
            "role_id," +
            "url_role_version" +
            ") values (" +
            "?," +
            "?," +
            "?" +
            ")";

    public static void main(final String args[]) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        UpgradeDatabase1 upgradeDatabase1 = new UpgradeDatabase1();
        es.submit(upgradeDatabase1);
        es.shutdown();
    }

    @Override
    public void run() {
        Connection conn = null;
        HashMap<String, Object> paramMap;
        try {

            Class.forName(driver);
            conn = DriverManager.getConnection(jdbcUrl, "galileoclub", "galileoclub");

            String roleMenu = "csv.xhtml";
            String roleNameUser = "CSV";
            String roleDesc = "Customer Service";

            int csvRoleId = 0;
            int admRoleId = 0;
            int usrRoleId = 0;
            PreparedStatement psSelectRoleIdByRoleName = conn.prepareStatement(selectRoleIdByRoleName);
            psSelectRoleIdByRoleName.setString(1, "CSV");
            ResultSet rsSelectRoleIdByRoleName = psSelectRoleIdByRoleName.executeQuery();
            if (rsSelectRoleIdByRoleName.next()) {
                csvRoleId = rsSelectRoleIdByRoleName.getInt(1);
            } else {
                csvRoleId = insertRoles(roleDesc, roleNameUser, roleMenu, conn);
            }

            psSelectRoleIdByRoleName.setString(1, "ADM");
            rsSelectRoleIdByRoleName = psSelectRoleIdByRoleName.executeQuery();
            rsSelectRoleIdByRoleName.next();
            admRoleId = rsSelectRoleIdByRoleName.getInt(1);

            psSelectRoleIdByRoleName.setString(1, "USR");
            rsSelectRoleIdByRoleName = psSelectRoleIdByRoleName.executeQuery();
            rsSelectRoleIdByRoleName.next();
            usrRoleId = rsSelectRoleIdByRoleName.getInt(1);

            String userName = "danielcs";
            changeRole(conn, userName, csvRoleId, admRoleId, usrRoleId);
            userName = "jenifercs";
            changeRole(conn, userName, csvRoleId, admRoleId, usrRoleId);
            userName = "shantycs";
            changeRole(conn, userName, csvRoleId, admRoleId, usrRoleId);

            int adm1Id = getUserId(conn, "adm1");
            deleteRole(conn, adm1Id, usrRoleId);
            int adm2Id = getUserId(conn, "adm2");
            deleteRole(conn, adm2Id, usrRoleId);

            paramMap = new HashMap<String, Object>();
            paramMap.put("role_id", admRoleId);
            paramMap.put("url_role", "/galileoclub/faces/secure/index.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/editProfile.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/change_password.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("role_id", csvRoleId);
            paramMap.put("url_role", "/galileoclub/faces/secure/index.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/editProfile.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/change_password.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/users.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/roles.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/users_roles.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/urls_roles.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/rolesCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/rolesDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/rolesRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/rolesUpdate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/urls_rolesCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/urls_rolesDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/urls_rolesRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/usersCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/usersDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/usersRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/usersUpdate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/users_rolesCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/users_rolesDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/users_rolesRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newMember.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newMemberVerification.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newClaim.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newClaimProcess.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/points.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pointsCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pointsDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pointsUpdate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/news.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newsCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newsDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newsRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/newsUpdate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/claims.xhtml");
            insertUrlsRoles(paramMap, conn);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void changeRole(Connection conn, String userName, int csvRoleId, int admRoleId, int usrRoleId) throws SQLException {
        int userId = getUserId(conn, userName);

        PreparedStatement psUpdateUsersRoles = conn.prepareStatement(updateUsersRoles);
        psUpdateUsersRoles.setInt(1, csvRoleId);
        psUpdateUsersRoles.setInt(2, userId);
        psUpdateUsersRoles.setInt(3, admRoleId);
        psUpdateUsersRoles.executeUpdate();
        psUpdateUsersRoles.close();

        deleteRole(conn, userId, usrRoleId);
    }

    private void deleteRole(Connection conn, int userId, int roleId) throws SQLException {
        PreparedStatement psDeleteUsersRoles = conn.prepareStatement(deleteUsersRoles);
        psDeleteUsersRoles.setInt(1, userId);
        psDeleteUsersRoles.setInt(2, roleId);
        psDeleteUsersRoles.executeUpdate();
        psDeleteUsersRoles.close();
    }

    private int getUserId(Connection conn, String userName) throws SQLException {
        PreparedStatement psSelectUserIdByUserName = conn.prepareStatement(selectUserIdByUserName);
        psSelectUserIdByUserName.setString(1, userName);
        ResultSet rsSelectUserIdByUserName = psSelectUserIdByUserName.executeQuery();
        rsSelectUserIdByUserName.next();
        int userId = rsSelectUserIdByUserName.getInt(1);
        rsSelectUserIdByUserName.close();
        psSelectUserIdByUserName.close();
        return userId;
    }

    private int insertRoles(final String roleDesc,
            final String roleName,
            final String roleMenu,
            final Connection conn)
            throws SQLException {
        int roleId = 0;
        PreparedStatement rolesStmt = null;
        ResultSet resultSet = null;
        try {
            rolesStmt = conn.prepareStatement(insertRoles, Statement.RETURN_GENERATED_KEYS);
            rolesStmt.setString(1, roleName);
            rolesStmt.setString(2, roleDesc);
            rolesStmt.setString(3, roleMenu);
            rolesStmt.setInt(4, Integer.valueOf(0));
            rolesStmt.executeUpdate();
            resultSet = rolesStmt.getGeneratedKeys();
            if (resultSet.next()) {
                roleId = resultSet.getInt(1);
            }

        } finally {
            resultSet.close();
            rolesStmt.close();
        }

        return roleId;
    }

    private void insertUrlsRoles(final Map paramMap,
            final Connection conn)
            throws SQLException {
        PreparedStatement urlsRolesStmt = null;
        try {
            urlsRolesStmt = conn.prepareStatement(insertUrlsRoles);
            urlsRolesStmt.setString(1, (String) paramMap.get("url_role"));
            urlsRolesStmt.setInt(2, (Integer) paramMap.get("role_id"));
            urlsRolesStmt.setInt(3, Integer.valueOf(0));
            urlsRolesStmt.executeUpdate();
        } catch (Exception ex) {
            if (!ex.getMessage().contains("Duplicate entry")) {
                log.log(Level.SEVERE, ex.getMessage(), ex);
            }
        } finally {
            urlsRolesStmt.close();
        }

    }
}
