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
import com.erprest.model.Address;
import com.erprest.model.City;
import com.erprest.model.Customer;
import com.erprest.model.CustomerType;
import com.erprest.model.District;
import com.erprest.model.Neighborhood;
import com.erprest.model.Tenant;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.erprest.model.UserRole;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author msi_ge72
 */
public class UserDao {

    public void addUser(User authUser, User newuser) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        boolean isUserExist = false;
        if (!authUser.getTenantUsers().getUserRole().isIsTenantAdmin() && newuser.getTenantUsers().getUserRole().isIsTenantAdmin()) {
            throw new CustomException("Admin olmadığınızdan admin yapma yetkiniz bulunmamaktadır");
        }
        try {
            conn = dbhelper.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement("select * from users where email=?");
            ps.setString(1, newuser.getEmail());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                isUserExist = true;
                newuser.setId(rs.getString("id"));
            }
            UserRole newUserRole = newuser.getTenantUsers().getUserRole();
            ps = conn.prepareStatement("insert into user_roles (user,customer,tenant,item,invoice,account,is_tenant_admin,is_einvoice_admin) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newUserRole.getUser());
            ps.setString(2, newUserRole.getCustomer());
            ps.setString(3, newUserRole.getTenant());
            ps.setString(4, newUserRole.getItem());
            ps.setString(5, newUserRole.getInvoice());
            ps.setString(6, newUserRole.getAccount());
            ps.setBoolean(7, newUserRole.isIsTenantAdmin());
            ps.setBoolean(8, newUserRole.isIsEinvoiceAdmin());
            ps.executeUpdate();
            ResultSet keys1 = ps.getGeneratedKeys();
            keys1.next();
            long insertedRoleId = keys1.getInt(1);

            ps = conn.prepareStatement("insert into tenant_users (tenant_id,user_id,user_roles_id) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, newuser.getTenantUsers().getTenant().getId());
            ps.setString(2, newuser.getId());
            ps.setLong(3, insertedRoleId);
            ps.executeUpdate();
            ResultSet keys2 = ps.getGeneratedKeys();
            keys2.next();
            newuser.getTenantUsers().setId(keys2.getInt(1));

            if (!isUserExist) {
                newuser.setConfirmationCode(UUID.randomUUID().toString());
                ps = conn.prepareStatement("insert into users(id,email,password,default_tenant_user_id,name,description,status,confirmation_code) values (?,?,?,?,?,?,?,?)");
                ps.setString(1, newuser.getId());
                ps.setString(2, newuser.getEmail());

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(newuser.getPassword());

                ps.setString(3, hashedPassword);
                ps.setLong(4, newuser.getTenantUsers().getId());
                ps.setString(5, newuser.getName());
                ps.setString(6, newuser.getDescription());
                ps.setString(7, newuser.getStatus());
                ps.setString(8, newuser.getConfirmationCode());
                ps.executeUpdate();

                if (newuser.getStatus().equals("DISABLED")) {
                    String redirectCode = Base64.getEncoder().encodeToString((newuser.getEmail() + ":" + newuser.getConfirmationCode()).getBytes());
                    //EmailConnection emailConnection = new TenantDao().emailSetting(authUser.getTenantUsers().getTenant().getId());
                    EMailConnectionHelper econ = new EMailConnectionHelper();
                    String angularUrl=ProjectConfig.getInstance().getConfig().getErpAngularUrl();
                    econ.sendPassword(newuser.getEmail(), angularUrl + "validator?code=" + redirectCode + "&action=confirmation");
                }
            }
            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public User updateUser(User updateUser, String defaultTenantID) {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (updateUser.getPassword() != null && !updateUser.getPassword().equals("")) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String newtoken = passwordEncoder.encode(updateUser.getPassword());
            whereParameters += ",password=?";
            paramList.add(newtoken);
        }
        if (updateUser.getName() != null && !updateUser.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(updateUser.getName());
        }
        if (updateUser.getDescription() != null && !updateUser.getDescription().equals("")) {
            whereParameters += ",description=?";
            paramList.add(updateUser.getDescription());
        }
        if (updateUser.getStatus() != null && (updateUser.getStatus().equals("ENABLED") || updateUser.getStatus().equals("DISABLED"))) {
            whereParameters += ",status=?";
            paramList.add(updateUser.getStatus());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update users set id=id" + whereParameters + " where id=?");
            paramList.add(updateUser.getId());
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ps.executeUpdate();

            if (defaultTenantID != null && !defaultTenantID.equals("")) {
                ps = conn.prepareStatement("update users set default_tenant_user_id="
                        + "(select id from tenant_users where tenant_id=? and user_id=?) where id=?");
                ps.setString(1, defaultTenantID);
                ps.setString(2, updateUser.getId());
                ps.setString(3, updateUser.getId());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        User user = getUserInfo(updateUser.getId());
        /*GibDao gibdao = new GibDao();
        List<GibUser> gibUser = gibdao.getGibUsers(user.getTenantUsers().getTenant().getTenantInfo().getTc());
        if (!gibUser.isEmpty()) {
            user.getTenantUsers().getTenant().getTenantInfo().setIsEfaturaUser(true);
        }*/
        return user;
    }

    public User getUserInfo(String user_id) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        User user = new User();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select u.id as user_id,u.email,u.name as user_name,u.description,u.status,u.created_at,\n"
                    + "                     t.id as t_id,t.owner as t_owner,t.owner_email as t_owner_email,t.name as t_name,t.description as t_description,t.logo as t_logo,t.signature as t_signature,t.status as t_status,t.einvoice_username,t.einvoice_password,t.tenantType as t_tenantType,t.invoice_note as t_invoice_note,t.expire_date as t_expire_date,t.created_at as t_created_at,\n"
                    + "                     ur.id as ur_id,ur.user as ur_user,ur.customer as ur_customer,ur.tenant as ur_tenant,ur.item as ur_item,ur.invoice as ur_invoice,ur.account as ur_account,ur.is_tenant_admin as ur_is_tenant_admin,ur.is_super_admin as ur_is_super_admin,ur.is_einvoice_admin as ur_is_einvoice_admin,\n"
                    + "                     tc.id as tc_id,tc.tenant_id as tc_tenant_id,tc.name as tc_name,tc.surname as tc_surname,tc.appellation as tc_appellation,tc.full_appellation as tc_full_appellation,tc.customer_type_id as tc_customer_type_id,tc.tax_administration as tc_tax_administration,tc.tc as tc_tc,tc.trade_register_number as tc_trade_register_number,tc.mersis_number as tc_mersis_number,tc.basic_discount as tc_basic_discount,tc.credit_limit as tc_credit_limit,tc.assent as tc_assent,tc.sender_identifier as tc_sender_identifier,tc.default_address_id as tc_default_address_id,tc.is_efatura_user as tc_is_efatura_user,tc.created_at as tc_created_at, \n"
                    + "                     ct.id as ct_id,ct.name as ct_name,ct.description as ct_description,\n"
                    + "     a2.id as a2_id,a2.customer_id as a2_customer_id,a2.address_name as a2_address_name,a2.city_id as a2_city_id,a2.district_id as a2_district_id,a2.neighborhood_id as a2_neighborhood_id,a2.phone_number as a2_phone_number,a2.email as a2_email,a2.fax as a2_fax,a2.full_address as a2_full_address,\n"
                    + "		ct2.id as ct2_id,ct2.name as ct2_name,\n"
                    + "        dt2.id as dt2_id,dt2.name as dt2_name,\n"
                    + "        nb2.id as nb2_id,nb2.name as nb2_name,nb2.PK as nb2_PK\n"
                    + "                 from users u\n"
                    + "                      join tenant_users tu on tu.id=u.default_tenant_user_id\n"
                    + "                      join tenants t on t.id=tu.tenant_id\n"
                    + "                      join user_roles ur on ur.id=tu.user_roles_id\n"
                    + "                      join customers tc on tc.id=t.tenant_info_id\n"
                    + "                      join customers_type ct on tc.customer_type_id=ct.id"
                    + "        left join address a2 on a2.id=tc.default_address_id\n"
                    + "        left join cities ct2 on ct2.id =a2.city_id\n"
                    + "        left join districts dt2 on dt2.id=a2.district_id\n"
                    + "        left join neighborhoods nb2 on nb2.id=a2.neighborhood_id\n"
                    + "                      where u.id=?");
            ps.setString(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserRole role = new UserRole();
                role.setId(rs.getLong("ur_id"));
                role.setUser(rs.getString("ur_user"));
                role.setCustomer(rs.getString("ur_customer"));
                role.setTenant(rs.getString("ur_tenant"));
                role.setItem(rs.getString("ur_item"));
                role.setInvoice(rs.getString("ur_invoice"));
                role.setAccount(rs.getString("ur_account"));
                role.setIsTenantAdmin(rs.getBoolean("ur_is_tenant_admin"));
                role.setIsSuperAdmin(rs.getBoolean("ur_is_super_admin"));
                role.setIsEinvoiceAdmin(rs.getBoolean("ur_is_einvoice_admin"));

                Tenant tenant = new Tenant();
                tenant.setId(rs.getString("t_id"));
                tenant.setOwner(rs.getString("t_owner"));
                tenant.setEmail(rs.getString("t_owner_email"));
                tenant.setName(rs.getString("t_name"));
                tenant.setDescription(rs.getString("t_description"));
                tenant.setLogo(rs.getString("t_logo"));
                tenant.setSignature(rs.getString("t_signature"));
                tenant.setStatus(rs.getString("t_status"));
                tenant.setEinvoiceUsername(rs.getString("einvoice_username"));
                tenant.setEinvoicePassword(rs.getString("einvoice_password"));
                tenant.setInvoiceNote(rs.getString("t_invoice_note"));
                tenant.setTenantType(rs.getString("t_tenantType"));
                tenant.setExpireDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("t_expire_date")));
                Date expireDate = new Date();
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    expireDate = df.parse(tenant.getExpireDate());
                } catch (ParseException e) {
                }
                long diff = expireDate.getTime() - new Date().getTime();
                tenant.setExpireCount(diff / (1000 * 60 * 60 * 24));

                tenant.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("t_created_at")));

                Customer tenantInfo = new Customer();
                tenantInfo.setId(rs.getLong("tc_id"));
                tenantInfo.setTenantId(rs.getString("tc_tenant_id"));
                tenantInfo.setName(rs.getString("tc_name"));
                tenantInfo.setSurname(rs.getString("tc_surname"));
                tenantInfo.setAppellation(rs.getString("tc_appellation"));
                tenantInfo.setFullAppellation(rs.getString("tc_full_appellation"));
                tenantInfo.setCustomerTypeId(rs.getLong("tc_customer_type_id"));
                tenantInfo.setTaxAdministration(rs.getString("tc_tax_administration"));
                tenantInfo.setTc(rs.getString("tc_tc"));
                tenantInfo.setTradeRegisterNumber(rs.getString("tc_trade_register_number"));
                tenantInfo.setMersisNumber(rs.getString("tc_mersis_number"));
                tenantInfo.setBasicDiscount(rs.getLong("tc_basic_discount"));
                tenantInfo.setCreditLimit(rs.getLong("tc_credit_limit"));
                tenantInfo.setIsAssent(rs.getBoolean("tc_assent"));
                tenantInfo.setSenderIdentifier(rs.getString("tc_sender_identifier"));
                tenantInfo.setDefaultAddressId(rs.getLong("tc_default_address_id"));
                tenantInfo.setIsEfaturaUser(rs.getBoolean("tc_is_efatura_user"));
                tenantInfo.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tc_created_at")));

                CustomerType customerype = new CustomerType();
                customerype.setId(rs.getLong("ct_id"));
                customerype.setName(rs.getString("ct_name"));
                customerype.setDescription(rs.getString("ct_description"));
                tenantInfo.setCustomerType(customerype);

                List<Address> addresses2 = new ArrayList<>();
                Address address2 = new Address();
                address2.setId(rs.getLong("a2_id"));
                address2.setCustomerId(rs.getLong("a2_customer_id"));
                address2.setAddressName(rs.getString("a2_address_name"));
                address2.setCityId(rs.getLong("a2_city_id"));
                address2.setDistrictId(rs.getLong("a2_district_id"));
                address2.setNeighborhoodId(rs.getLong("a2_neighborhood_id"));
                address2.setPhoneNumber(rs.getString("a2_phone_number"));
                address2.setEmail(rs.getString("a2_email"));
                address2.setFax(rs.getString("a2_fax"));
                address2.setFullAddress(rs.getString("a2_full_address"));

                City city2 = new City();
                city2.setId(rs.getLong("ct2_id"));
                city2.setName(rs.getString("ct2_name"));
                address2.setCity(city2);

                District district2 = new District();
                district2.setId(rs.getLong("dt2_id"));
                district2.setName(rs.getString("dt2_name"));
                address2.setDistrict(district2);

                Neighborhood neigh2 = new Neighborhood();
                neigh2.setId(rs.getLong("nb2_id"));
                neigh2.setName(rs.getString("nb2_name"));
                neigh2.setPostCode(rs.getLong("nb2_PK"));
                address2.setNeighborhood(neigh2);
                addresses2.add(address2);
                tenantInfo.setAddress(addresses2);

                tenant.setTenantInfo(tenantInfo);

                TenantUsers tu = new TenantUsers();
                tu.setTenant(tenant);
                tu.setUserRole(role);

                user.setId(rs.getString("user_id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("user_name"));
                user.setDescription(rs.getString("description"));
                user.setStatus(rs.getString("status"));
                user.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("created_at")));
                user.setTenantUsers(tu);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return user;

    }

    public List<TenantUsers> getUserTenants(User authUser) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<TenantUsers> tenantList = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select tu.id as tuid, t.id, t.name,t.description from users u, tenant_users tu \n"
                    + "join tenants as t on t.id=tu.tenant_id where tu.user_id=u.id and u.id=?");
            ps.setString(1, authUser.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TenantUsers tenantUsers = new TenantUsers();
                Tenant tenant = new Tenant();
                tenant.setId(rs.getString("t.id"));
                tenant.setName(rs.getString("t.name"));
                tenant.setDescription(rs.getString("t.description"));
                tenantUsers.setTenant(tenant);
                tenantUsers.setId(rs.getLong("tuid"));
                tenantList.add(tenantUsers);
            }
        } catch (SQLException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return tenantList;
    }

    public void deleteUser(User authUser, String userId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update users set status=\"DISABLED\" where id=? and id=<>?");
            ps.setString(1, userId);
            ps.setString(2, authUser.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public boolean isValidEmailAddress(String email) {
        Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailRegex.matcher(email);
        return matcher.find();
    }
}
