/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.connection.EMailConnectionHelper;
import com.erprest.connection.ProjectConfig;
import com.erprest.filter.CustomException;
import com.erprest.model.EmailConnection;
import com.erprest.model.Tenant;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.erprest.model.UserRole;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.UUID;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author msi_ge72
 */
public class AuthDao {

    public User login(String email, String password) {

        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        User user = new User();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select u.id,u.name,u.email,u.password,u.status,u.default_tenant_user_id,tu.tenant_id,tu.user_id,tu.user_roles_id,"
                    + "                     ur.user as uruser,ur.customer as urcustomer,ur.tenant as urtenant,ur.item as uritem,ur.invoice as urinvoice,ur.account as uraccount,ur.is_tenant_admin as uris_tenant_admin,ur.is_super_admin as uris_super_admin from users u\n"
                    + "                         join tenant_users tu on tu.id=u.default_tenant_user_id\n"
                    + "                         join user_roles ur on ur.id=tu.user_roles_id "
                    + "                             where email=? ");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String dbBycryptPass = rs.getString("password");
                if (!dbBycryptPass.equals("")) {
                    dbBycryptPass = "$2a" + dbBycryptPass.substring(3);
                }
                if (BCrypt.checkpw(password, dbBycryptPass)) {
                    user.setEmail(rs.getString("email"));
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    //user.setDescription(rs.getString("description"));
                    //user.setSecretKey(rs.getString("secretKey"));
                    //user.setMetadata(rs.getString("metadata"));
                    user.setStatus(rs.getString("status"));

                    TenantUsers tu = new TenantUsers();
                    tu.setId(rs.getLong("default_tenant_user_id"));
                    tu.setTenantId(rs.getString("tenant_id"));
                    tu.setUserId(rs.getString("user_id"));

                    UserRole userRole = new UserRole();
                    userRole.setId(rs.getLong("user_roles_id"));
                    userRole.setUser(rs.getString("uruser"));
                    userRole.setCustomer(rs.getString("urcustomer"));
                    userRole.setTenant(rs.getString("urtenant"));
                    userRole.setItem(rs.getString("uritem"));
                    userRole.setInvoice(rs.getString("urinvoice"));
                    userRole.setAccount(rs.getString("uraccount"));
                    userRole.setIsTenantAdmin(rs.getBoolean("uris_tenant_admin"));
                    userRole.setIsSuperAdmin(rs.getBoolean("uris_super_admin"));
                    tu.setUserRole(userRole);

                    user.setTenantUsers(tu);
                }
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return user;
    }

    public User parseAuthentication(String authorization) {
        final String encodedUserPassword = authorization.replaceFirst("Basic" + " ", "");
        String usernameAndPassword = null;
        User user = new User();
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(encodedUserPassword);
            usernameAndPassword = new String(decodedBytes, "UTF-8");
        } catch (IOException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");

        user.setEmail(tokenizer.nextToken());
        user.setPassword(tokenizer.nextToken());
        user.setRememberToken("Basic " + new String(Base64.getEncoder().encode((user.getEmail() + ":" + user.getPassword()).getBytes())));
        return user;
    }

    public void checkConfirmatinCode(String email, String code, String password) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select email,status,confirmation_code from users where email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!rs.getString("confirmation_code").equals(code)) {
                    throw new CustomException("Doğrulama kodu hatalı");
                } else {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    ps = conn.prepareStatement("update users set status=?,password=?, confirmed_at=now(),confirmation_code=\"\" where email=?");
                    ps.setString(1, "ENABLED");
                    ps.setString(2, passwordEncoder.encode(password));
                    ps.setString(3, email);
                    ps.executeUpdate();
                }
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void sendPasswordCode(String email) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        TenantDao tenantdao = new TenantDao();
        Tenant authTenant = (tenantdao.getSubTenants(null, null, null, null, "SUPERADMIN", null, null, null, 1, 0).get(0));
        String passwordCode = UUID.randomUUID().toString();
        try {
            conn = dbhelper.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("update users set password_code=? where email=?");
            ps.setString(1, passwordCode);
            ps.setString(2, email);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new CustomException("Sistemde böyle bir email kayıtlı değildir.");
            }

            String redirectCode = Base64.getEncoder().encodeToString((email + ":" + passwordCode).getBytes());
            EmailConnection emailConnection = tenantdao.emailSetting(authTenant.getId());
            EMailConnectionHelper econ = new EMailConnectionHelper(emailConnection);
            String angularUrl=ProjectConfig.getInstance().getConfig().getErpAngularUrl();
            econ.sendPassword(email, angularUrl + "validator?code=" + redirectCode + "&action=password");
            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void checkPasswordCode(String email, String code, String password) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select email,password_code from users where email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (!rs.getString("password_code").equals(code)) {
                    throw new CustomException("Doğrulama kodu hatalı");
                } else {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    ps = conn.prepareStatement("update users set password=?, password_code=\"\" where email=?");
                    ps.setString(1, passwordEncoder.encode(password));
                    ps.setString(2, email);
                    ps.executeUpdate();
                }
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }
}
