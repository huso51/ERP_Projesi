/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.filter.CustomException;
import com.erprest.model.Address;
import com.erprest.model.City;
import com.erprest.model.Customer;
import com.erprest.model.CustomerType;
import com.erprest.model.District;
import com.erprest.model.EmailConnection;
import com.erprest.model.ExpireRequest;
import com.erprest.model.Neighborhood;
import com.erprest.model.Tenant;
import com.erprest.model.TenantPrefix;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.erprest.model.UserRole;
import com.erprest.service.ImageService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 * @author msi_ge72
 */
public class TenantDao {

    public List<User> getTenantUsers(User authUser, String idfilter, String userfilter, String emailfilter, String dateAfterfilter, String dateBeforefilter, String statusfilter, String order_by, long limit, long offset) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<User> userList = new ArrayList<>();
        String whereParameter, temp = "";
        ArrayList paramList = new ArrayList();

        if (userfilter != null && !userfilter.equals("")) {
            temp += " and u.name like ?";
            paramList.add("%" + userfilter + "%");
        }
        if (idfilter != null && !idfilter.equals("")) {
            temp += " and u.id=?";
            paramList.add(idfilter);
        }
        if (emailfilter != null && !emailfilter.equals("")) {
            temp += " and email like ?";
            paramList.add("%" + emailfilter + "%");
        }
        if (dateAfterfilter != null && !dateAfterfilter.equals("")) {
            temp += " and u.created_at > ?";
            paramList.add(dateAfterfilter);
        }
        if (dateBeforefilter != null && !dateBeforefilter.equals("")) {
            temp += " and u.created_at < ?";
            paramList.add(dateBeforefilter);
        }
        if (statusfilter != null && (statusfilter.equals("ENABLED") || statusfilter.equals("DISABLED"))) {
            temp += " and status=?";
            paramList.add(statusfilter);
        }
        if (!authUser.getTenantUsers().getTenant().getTenantType().equals("SUPERADMIN")) {
            temp += " and tenant_id=?";
            paramList.add(authUser.getTenantUsers().getTenant().getId());
        }
        if (order_by == null) {
            order_by = "";
        } else {
            order_by = " order by " + order_by;
        }
        whereParameter = temp;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select u.id,u.email,u.name,u.description,u.status,u.created_at,t.id as tenant_id,t.name as tenant_name,"
                    + "             r.user,r.customer,r.tenant,r.item,r.invoice,r.account,r.is_tenant_admin,r.is_super_admin from users u\n"
                    + "                 join tenant_users tu on tu.user_id=u.id\n"
                    + "                 join tenants t on t.id=tu.tenant_id\n"
                    + "                 join user_roles r on tu.user_roles_id=r.id"
                    + "                         where 1=1 " + whereParameter + order_by + " limit " + limit + " offset " + offset);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setEmail(rs.getString("email"));
                user.setName(rs.getString("name"));
                user.setDescription(rs.getString("description"));
                user.setStatus(rs.getString("status"));
                user.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("u.created_at")));
                TenantUsers tu = new TenantUsers();

                UserRole role = new UserRole();
                role.setUser(rs.getString("user"));
                role.setCustomer(rs.getString("customer"));
                role.setTenant(rs.getString("tenant"));
                role.setItem(rs.getString("item"));
                role.setInvoice(rs.getString("invoice"));
                role.setAccount(rs.getString("account"));
                role.setIsTenantAdmin(rs.getBoolean("is_tenant_admin"));
                role.setIsSuperAdmin(rs.getBoolean("is_super_admin"));
                tu.setUserRole(role);

                Tenant tenant = new Tenant();
                tenant.setId(rs.getString("tenant_id"));
                tenant.setName(rs.getString("tenant_name"));
                tu.setTenant(tenant);
                user.setTenantUsers(tu);
                userList.add(user);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return userList;
    }

    public List<Tenant> getSubTenants(String idfilter, String namefilter, String ownerId, String emailFilter, String typeFilter, String vkn, String statusfilter, String order_by, long limit, long offset) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<Tenant> tenants = new ArrayList<>();
        String whereParameter, temp = "";
        ArrayList paramList = new ArrayList();

        if (namefilter != null && !namefilter.equals("")) {
            temp += " and t.name like ?";
            paramList.add("%" + namefilter + "%");
        }
        if (idfilter != null && !idfilter.equals("")) {
            temp += " and t.id=?";
            paramList.add(idfilter);
        }
        if (ownerId != null && !ownerId.equals("")) {
            temp += " and t.owner=?";
            paramList.add(ownerId);
        }
        if (statusfilter != null && (statusfilter.equals("ENABLED") || statusfilter.equals("DISABLED"))) {
            temp += " and t.status=?";
            paramList.add(statusfilter);
        }
        if (typeFilter != null && !typeFilter.equals("")) {
            temp += " and t.tenantType=?";
            paramList.add(typeFilter);
        }
        if (vkn != null && !vkn.equals("")) {
            temp += "tc.tc=?";
            paramList.add(vkn);
        }
        if (emailFilter != null && !emailFilter.equals("")) {//authUser != null && !authUser.getTenantUsers().getTenant().getTenantType().equals("SUPERADMIN")
            temp += " and t.owner_email=?";
            paramList.add(emailFilter);
        }
        if (order_by == null) {
            order_by = " order by " + "t.created_at desc";
        } else {
            order_by = " order by " + order_by;
        }
        whereParameter = temp;

        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select t.id,t.owner,t.owner_email,t.name,t.description,t.logo,t.signature,t.status,t.tenantType,t.einvoice_username,t.einvoice_password,t.tenant_info_id,t.expire_date,t.created_at,\n"
                    + "	tow.id as tow_id,tow.owner as tow_owner,tow.name as tow_name,tow.description as tow_description,tow.status as tow_status,tow.tenantType as tow_tenantType,tow.created_at as tow_created_at ,\n"
                    + "       	tc.id as tc_id,tc.tenant_id as tc_tenant_id,tc.name as tc_name,tc.surname as tc_surname,tc.appellation as tc_appellation,tc.full_appellation as tc_full_appellation,tc.customer_type_id as tc_customer_type_id,tc.tax_administration as tc_tax_administration,tc.tc as tc_tc,tc.trade_register_number as tc_trade_register_number,tc.mersis_number as tc_mersis_number,tc.basic_discount as tc_basic_discount,tc.credit_limit as tc_credit_limit,tc.assent as tc_assent,tc.default_address_id as tc_default_address_id,tc.is_efatura_user as tc_is_efatura_user,tc.created_at as tc_created_at, \n"
                    + "         ct.id as ct_id,ct.name as ct_name,ct.description as ct_description,\n"
                    + "         a2.id as a2_id,a2.customer_id as a2_customer_id,a2.address_name as a2_address_name,a2.city_id as a2_city_id,a2.district_id as a2_district_id,a2.neighborhood_id as a2_neighborhood_id,a2.phone_number as a2_phone_number,a2.email as a2_email,a2.fax as a2_fax,a2.full_address as a2_full_address,\n"
                    + "		ct2.id as ct2_id,ct2.name as ct2_name,\n"
                    + "        dt2.id as dt2_id,dt2.name as dt2_name,\n"
                    + "        nb2.id as nb2_id,nb2.name as nb2_name,nb2.PK as nb2_PK\n"
                    + "        \n"
                    + "        from tenants t \n"
                    + "        join tenants tow on tow.id=t.owner\n"
                    + "        join customers tc on tc.id = t.tenant_info_id\n"
                    + "        join customers_type ct on ct.id=tc.customer_type_id\n"
                    + "        left join address a2 on a2.id=tc.default_address_id\n"
                    + "        left join cities ct2 on ct2.id =a2.city_id\n"
                    + "        left join districts dt2 on dt2.id=a2.district_id\n"
                    + "        left join neighborhoods nb2 on nb2.id=a2.neighborhood_id\n "
                    + "             where 1=1 " + whereParameter + order_by + " limit " + limit + " offset " + offset);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tenant tenant = new Tenant();
                tenant.setId(rs.getString("id"));
                tenant.setEmail(rs.getString("owner_email"));
                tenant.setOwner(rs.getString("owner"));
                tenant.setName(rs.getString("name"));
                tenant.setDescription(rs.getString("description"));
                tenant.setLogo(rs.getString("logo"));
                tenant.setSignature(rs.getString("signature"));
                tenant.setStatus(rs.getString("status"));
                tenant.setDescription(rs.getString("description"));
                tenant.setTenantType(rs.getString("tenantType"));
                tenant.setEinvoiceUsername(rs.getString("einvoice_username"));
                tenant.setEinvoicePassword(rs.getString("einvoice_password"));
                tenant.setTenantInfoId(rs.getLong("tenant_info_id"));

                tenant.setExpireDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("expire_date")));

                Date expireDate = new Date();
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    expireDate = df.parse(tenant.getExpireDate());
                } catch (ParseException e) {
                }
                long diff = expireDate.getTime() - new Date().getTime();
                tenant.setExpireCount(diff / (1000 * 60 * 60 * 24));

                tenant.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("created_at")));

                Tenant ownerTenant = new Tenant();
                ownerTenant.setId(rs.getString("tow_id"));
                ownerTenant.setName(rs.getString("tow_name"));
                ownerTenant.setOwner(rs.getString("tow_owner"));
                ownerTenant.setDescription(rs.getString("tow_description"));
                ownerTenant.setStatus(rs.getString("tow_status"));
                ownerTenant.setTenantType(rs.getString("tow_tenantType"));
                ownerTenant.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tow_created_at")));
                tenant.setOwnerTenant(ownerTenant);

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
                tenantInfo.setDefaultAddressId(rs.getLong("tc_default_address_id"));
                tenantInfo.setIsEfaturaUser(rs.getBoolean("tc_is_efatura_user"));
                tenantInfo.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("tc_created_at")));

                CustomerType customerType = new CustomerType();
                customerType.setId(rs.getLong("ct_id"));
                customerType.setName(rs.getString("ct_name"));
                customerType.setDescription(rs.getString("ct_description"));
                tenantInfo.setCustomerType(customerType);

                tenant.setTenantInfo(tenantInfo);

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
                tenants.add(tenant);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return tenants;
    }

    public void addSubTenant(User authUser, User newUser) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        UserDao userdao = new UserDao();
        Tenant newTenant = newUser.getTenantUsers().getTenant();
        try {
            conn = dbhelper.getConnection();
            conn.setAutoCommit(false);
            CustomerDao customerdao = new CustomerDao();
            newTenant.getTenantInfo().setTenantId(newTenant.getId());

            Customer newTenantInfo = newTenant.getTenantInfo();
            newTenant.setTenantInfoId(customerdao.addCustomer(newTenantInfo));

            int psIndex = 1;
            ps = conn.prepareStatement("insert into tenants (id,owner,owner_email,name,description,tenantType,einvoice_username,einvoice_password,tenant_info_id)"
                    + " values (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(psIndex++, newTenant.getId());
            ps.setString(psIndex++, newTenant.getOwner());
            ps.setString(psIndex++, newTenant.getEmail());
            ps.setString(psIndex++, newTenant.getName());
            ps.setString(psIndex++, newTenant.getDescription());
            ps.setString(psIndex++, newTenant.getTenantType());
            if (newTenant.getEinvoicePassword() != null && newTenant.getEinvoiceUsername() != null) {
                ps.setString(psIndex++, newTenant.getEinvoiceUsername());
                ps.setString(psIndex++, newTenant.getEinvoicePassword());
            } else {
                ps.setNull(psIndex++, java.sql.Types.VARCHAR);
                ps.setNull(psIndex++, java.sql.Types.VARCHAR);
            }
            ps.setLong(psIndex++, newTenant.getTenantInfoId());
            ps.executeUpdate();

            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        userdao.addUser(authUser, newUser);
        addExpireDate(newTenant.getId(), newTenant.getExpireCount());
    }

    public void updateTenantUsers(User userUpdate, String tenantId) throws CustomException {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().equals("")) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String newtoken = passwordEncoder.encode(userUpdate.getPassword());
            whereParameters += ",password=?";
            paramList.add(newtoken);
        }
        if (userUpdate.getName() != null && !userUpdate.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null && !userUpdate.getEmail().equals("")) {
            whereParameters += ",email=?";
            paramList.add(userUpdate.getEmail());
        }
        if (userUpdate.getDescription() != null && !userUpdate.getDescription().equals("")) {
            whereParameters += ",description=?";
            paramList.add(userUpdate.getDescription());
        }
        if (userUpdate.getStatus() != null && (userUpdate.getStatus().equals("ENABLED") || userUpdate.getStatus().equals("DISABLED"))) {
            whereParameters += ",status=?";
            paramList.add(userUpdate.getStatus());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from users where id=? and exists(select * from tenant_users where tenant_id=? and user_id=?)");
            ps.setString(1, userUpdate.getId());
            ps.setString(2, tenantId);
            ps.setString(3, userUpdate.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CustomException("Bu kullanıcıyı güncellemek için yetkiniz yoktur.");
            }

            ps = conn.prepareStatement("update users set id=id" + whereParameters + " where id=?");
            paramList.add(userUpdate.getId());
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ps.executeUpdate();

            if (userUpdate.getTenantUsers().getUserRole() != null) {
                ps = conn.prepareStatement(" update user_roles set "
                        + "user=?,customer=?,tenant=?,item=?,invoice=?, account=?,is_tenant_admin=?,is_einvoice_admin=? "
                        + "where id=(select user_roles_id from tenant_users "
                        + "where tenant_id=? and user_id=?)");
                ps.setString(1, userUpdate.getTenantUsers().getUserRole().getUser());
                ps.setString(2, userUpdate.getTenantUsers().getUserRole().getCustomer());
                ps.setString(3, userUpdate.getTenantUsers().getUserRole().getTenant());
                ps.setString(4, userUpdate.getTenantUsers().getUserRole().getItem());
                ps.setString(5, userUpdate.getTenantUsers().getUserRole().getInvoice());
                ps.setString(6, userUpdate.getTenantUsers().getUserRole().getAccount());
                ps.setBoolean(7, userUpdate.getTenantUsers().getUserRole().isIsTenantAdmin());
                ps.setBoolean(8, userUpdate.getTenantUsers().getUserRole().isIsEinvoiceAdmin());
                ps.setString(9, tenantId);
                ps.setString(10, userUpdate.getId());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void updateTenant(Tenant authTenant, Tenant tenant) throws CustomException {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (tenant.getName() != null && !tenant.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(tenant.getName());
        }
        if (tenant.getDescription() != null && !tenant.getDescription().equals("")) {
            whereParameters += ",description=?";
            paramList.add(tenant.getDescription());
        }
        if (tenant.getStatus() != null && (tenant.getStatus().equals("ENABLED") || tenant.getStatus().equals("DISABLED"))) {
            whereParameters += ",status=?";
            paramList.add(tenant.getStatus());
        }
        if (tenant.getTenantType() != null && !tenant.getTenantType().equals("")) {
            whereParameters += ",tenantType=?";
            paramList.add(tenant.getTenantType());
        }
        /*if (tenant.getCredits() != 0) {
                addCredit(tenant.getId(), tenant.getCredits());
                addCredit(authTenant.getId(), -tenant.getCredits());
            }*/
        if (tenant.getExpireCount() != 0) {
            ExpireRequest expireRequest = new ExpireRequest();
            expireRequest.setTenantId(tenant.getId());
            expireRequest.setOwnerId(authTenant.getOwner());
            expireRequest.setAmount(tenant.getExpireCount());
            expireRequest.setDescription("üyelik uzatma talebi");
            expireRequest(expireRequest);
        }
        if (tenant.getLogo() != null) {
            ImageService imageService = new ImageService();
            tenant.setLogo(imageService.processImage(tenant.getLogo()));
            whereParameters += ",logo=?";
            paramList.add(tenant.getLogo());
        }
        if (tenant.getSignature() != null) {
            ImageService imageService = new ImageService();
            tenant.setSignature(imageService.processImage(tenant.getSignature()));
            whereParameters += ",signature=?";
            paramList.add(tenant.getSignature());
        }
        if (tenant.getEinvoiceUsername() != null) {
            whereParameters += ",einvoice_username=?";
            paramList.add(tenant.getEinvoiceUsername());
        }
        if (tenant.getEinvoicePassword() != null) {
            whereParameters += ",einvoice_password=?";
            paramList.add(tenant.getEinvoicePassword());
        }
        if (tenant.getOwner() != null && !tenant.getOwner().equals("") && authTenant.getTenantType().equals("SUPERADMIN")) {
            whereParameters += ",owner=?";
            paramList.add(tenant.getOwner());
        }
        if (tenant.getInvoiceNote() != null) {
            whereParameters += ",invoice_note=?";
            paramList.add(tenant.getInvoiceNote());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from tenants where id=? and ((owner=? or ?=?) or ?)");
            ps.setString(1, tenant.getId());
            ps.setString(2, authTenant.getId());
            ps.setString(3, tenant.getId());
            ps.setString(4, authTenant.getId());
            ps.setBoolean(5, authTenant.getTenantType().equals("SUPERADMIN"));
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CustomException("Bu organizasyonu güncellemek için yetkiniz yoktur.");
            }

            if (tenant.getTenantInfo() != null && tenant.getTenantInfo().getAddress() != null) {
                CustomerDao customerdao = new CustomerDao();
                customerdao.updateCustomer(tenant.getTenantInfo());
                customerdao.updateAddress(tenant.getTenantInfo().getAddress().get(0));
            }
            ps = conn.prepareStatement("update tenants set id=id" + whereParameters + " where id=?");
            paramList.add(tenant.getId());
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void addPrefix(TenantPrefix prefix) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into tenant_prefix (tenant_id,name,code,description) \n"
                    + "                 	values(?,?,?,?)");
            ps.setString(1, prefix.getTenantId());
            ps.setString(2, prefix.getName().toUpperCase());
            ps.setString(3, prefix.getCode());
            ps.setString(4, prefix.getDescription());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void updatePrefix(TenantPrefix prefix) throws CustomException {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (prefix.getName() != null && !prefix.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(prefix.getName());
        }
        if (prefix.getDescription() != null) {
            whereParameters += ",description=?";
            paramList.add(prefix.getDescription());
        }
        if (prefix.getCode() != null && !prefix.getCode().equals("")) {
            whereParameters += ",code=?";
            paramList.add(prefix.getCode());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from tenant_prefix where id=? and tenant_id=?");
            ps.setLong(1, prefix.getId());
            ps.setString(2, prefix.getTenantId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CustomException("Bu prefixi güncellemek için yetkiniz yoktur.");
            }
            ps = conn.prepareStatement("update tenant_prefix set id=id" + whereParameters + " where id=?");
            paramList.add(prefix.getId());
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<TenantPrefix> getPrefix(String tenantId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<TenantPrefix> prefixes = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from tenant_prefix where tenant_id=?");
            ps.setString(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TenantPrefix prefix = new TenantPrefix();
                prefix.setId(rs.getLong("id"));
                prefix.setName(rs.getString("name"));
                prefix.setCode(rs.getString("code"));
                prefix.setDescription(rs.getString("description"));
                prefixes.add(prefix);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return prefixes;
    }

    public void deletePrefix(TenantPrefix prefix) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("delete from tenant_prefix where id=? and tenant_id=?");
            ps.setLong(1, prefix.getId());
            ps.setString(2, prefix.getTenantId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void expireRequest(ExpireRequest request) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into expire_request (tenant_id,owner_id,amount,description) \n"
                    + "		values(?,?,?,?)");
            ps.setString(1, request.getTenantId());
            ps.setString(2, request.getOwnerId());
            ps.setLong(3, request.getAmount());
            ps.setString(4, request.getDescription());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<ExpireRequest> showExpireRequest(boolean confirmed) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<ExpireRequest> requests = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select er.*,t1.name as tenant_name,t2.name as owner_name\n"
                    + "                   from expire_request er\n"
                    + "                     left join tenants t1 on t1.id=er.tenant_id\n"
                    + "                     left join tenants t2 on t2.id=er.owner_id\n"
                    + "                         where er.confirmed=?");
            ps.setBoolean(1, confirmed);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExpireRequest request = new ExpireRequest();
                request.setId(rs.getLong("id"));
                request.setTenantId(rs.getString("tenant_id"));
                request.setTenantName(rs.getString("tenant_name"));
                request.setOwnerId(rs.getString("owner_id"));
                request.setOwnerName(rs.getString("owner_name"));
                request.setAmount(rs.getLong("amount"));
                request.setDescription(rs.getString("description"));
                request.setConfirmed(rs.getBoolean("confirmed"));
                request.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("created_at")));
                requests.add(request);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return requests;
    }

    public void acceptExpireRequest(long expireId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        String tenantId = "";
        long amount = 0;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from expire_request where id=?");
            ps.setLong(1, expireId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tenantId = rs.getString("tenant_id");
                amount = rs.getLong("amount");
            }
            addExpireDate(tenantId, amount);
            ps = conn.prepareStatement("update expire_request set confirmed=true where id=?");
            ps.setLong(1, expireId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void addExpireDate(String tenantId, long amount) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update tenants set expire_date= if(now()<expire_date,expire_date,now()) + interval ? day where id=?");
            ps.setLong(1, amount);
            ps.setString(2, tenantId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void emailSetup(EmailConnection emailConnection) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("INSERT INTO email_connection (tenant_id,host,port,username,password) VALUES (?,?,?,?,?)\n"
                    + "ON DUPLICATE KEY UPDATE host=VALUES(host),port=VALUES(port),username=VALUES(username),password=VALUES(password)");
            ps.setString(1, emailConnection.getTenantId());
            ps.setString(2, emailConnection.getHost());
            ps.setLong(3, emailConnection.getPort());
            ps.setString(4, emailConnection.getUsername());
            ps.setString(5, emailConnection.getPassword());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public EmailConnection emailSetting(String tenantId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        EmailConnection emailConnection = new EmailConnection();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from email_connection where tenant_id=?");
            ps.setString(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                emailConnection.setId(rs.getLong("id"));
                emailConnection.setTenantId(rs.getString("tenant_id"));
                emailConnection.setHost(rs.getString("host"));
                emailConnection.setPort(rs.getLong("port"));
                emailConnection.setUsername(rs.getString("username"));
                emailConnection.setPassword(rs.getString("password"));
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return emailConnection;
    }

}
