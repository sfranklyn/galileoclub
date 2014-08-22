/*
 * InitDatabase.java
 *
 * Created on July 31, 2007, 2:03 PM
 *
 */
package galileoclub.database;

import galileoclub.ejb.service.AppServiceBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Samuel Franklyn
 */
public class InitDatabase {

    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String jdbcUrl = "jdbc:mysql://localhost:3306/galileoclub";
    private static final String usersSql =
            "insert into users (" +
            "user_name," + //1
            "full_name," + //2
            "user_password," + //3
            "user_version," + //4
            "user_status" + //5
            ") values (" +
            "?," + //1
            "?," + //2
            "?," + //3
            "0," + //4
            "'A'" + //5
            ")";
    private static final String rolesSql =
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
    private final String usersRolesSql =
            "insert into users_roles (" +
            "user_id," +
            "role_id," +
            "users_roles_version" +
            ") values (" +
            "?," +
            "?," +
            "?" +
            ")";
    private final String urlsRolesSql =
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
        final InitDatabase init = new InitDatabase();
        init.init(args);
    }

    public void init(final String args[]) {
        Connection conn = null;
        try {

            Class.forName(driver);
            conn = DriverManager.getConnection(jdbcUrl, "galileoclub", "galileoclub");

            AppServiceBean app = new AppServiceBean();

            Map<String, Object> paramMap = new HashMap<String, Object>();

            String adminName1 = "adm1";
            String fullName = "Administrator One";
            String roleMenu = "adm.xhtml";
            String roleNameAdmin = "ADM";
            String roleDesc = "Administrator";
            String userPassword = app.hashPassword("5x5+5=30");

            final int adminUserId1 = insertUsers(adminName1, fullName, userPassword, conn);
            final int adminRoleId = insertRoles(roleDesc, roleNameAdmin, roleMenu, conn);
            insertUsersRoles(adminUserId1, adminRoleId, conn);

            String adminName2 = "adm2";
            fullName = "Administrator Two";
            userPassword = app.hashPassword("adm2");
            final int adminUserId2 = insertUsers(adminName2, fullName, userPassword, conn);
            insertUsersRoles(adminUserId2, adminRoleId, conn);

            String userName1 = "user1";
            fullName = "User One";
            userPassword = app.hashPassword("user1");
            final int userId1 = insertUsers(userName1, fullName, userPassword, conn);

            roleMenu = "user.xhtml";
            String roleNameUser = "USR";
            roleDesc = "User";
            final int userRoleId = insertRoles(roleDesc, roleNameUser, roleMenu, conn);
            insertUsersRoles(userId1, userRoleId, conn);

            String userName2 = "user2";
            fullName = "User Two";
            userPassword = app.hashPassword("user2");
            final int userId2 = insertUsers(userName2, fullName, userPassword, conn);
            insertUsersRoles(userId2, userRoleId, conn);

            insertUsersRoles(adminUserId1, userRoleId, conn);
            insertUsersRoles(adminUserId2, userRoleId, conn);

            paramMap = new HashMap<String, Object>();
            paramMap.put("role_id", adminRoleId);
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
            paramMap.put("url_role", "/galileoclub/faces/secure/segmentCounter.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts1.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts1TransferSuccess.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts2.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts3.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts4.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts5.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts6.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts7.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pccs.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pccsCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pccsDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pccsRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pccsUpdate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/configs.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/configsCreate.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/configsDelete.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/configsRead.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/configsUpdate.xhtml");
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

            paramMap.put("role_id", userRoleId);
            paramMap.put("url_role", "/galileoclub/faces/secure/index.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/editProfile.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pointClaimHistory.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/claimReward.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/claimRewardResponse.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/claimReconfirm.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/claimReconfirmResponse.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/change_password.xhtml");
            insertUrlsRoles(paramMap, conn);
            paramMap.put("url_role", "/galileoclub/faces/secure/pnrCounts5.xhtml");
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

    private void insertUrlsRoles(final Map paramMap,
            final Connection conn)
            throws SQLException {
        PreparedStatement urlsRolesStmt = null;
        try {
            urlsRolesStmt = conn.prepareStatement(urlsRolesSql);
            urlsRolesStmt.setString(1, (String) paramMap.get("url_role"));
            urlsRolesStmt.setInt(2, (Integer) paramMap.get("role_id"));
            urlsRolesStmt.setInt(3, Integer.valueOf(0));
            urlsRolesStmt.executeUpdate();
        } finally {
            urlsRolesStmt.close();
        }

    }

    private void insertUsersRoles(final int userId, final int roleId,
            final Connection conn)
            throws SQLException {
        final PreparedStatement usersRolesStmt = conn.prepareStatement(usersRolesSql);
        usersRolesStmt.setInt(1, userId);
        usersRolesStmt.setInt(2, roleId);
        usersRolesStmt.setInt(3, Integer.valueOf(0));
        usersRolesStmt.executeUpdate();
        usersRolesStmt.close();
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
            rolesStmt = conn.prepareStatement(rolesSql, Statement.RETURN_GENERATED_KEYS);
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

    private int insertUsers(final String userName,
            final String fullName,
            final String userPassword,
            final Connection conn)
            throws SQLException {
        int userId = 0;
        PreparedStatement usersStmt = null;
        ResultSet resultSet = null;
        try {
            usersStmt = conn.prepareStatement(usersSql, Statement.RETURN_GENERATED_KEYS);
            usersStmt.setString(1, userName);
            usersStmt.setString(2, fullName);
            usersStmt.setString(3, userPassword);
            usersStmt.executeUpdate();
            resultSet = usersStmt.getGeneratedKeys();
            if (resultSet.next()) {
                userId = resultSet.getInt(1);
            }
        } finally {
            resultSet.close();
            usersStmt.close();
        }

        return userId;
    }
}
