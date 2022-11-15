/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.connection.ProjectConfig;
import com.erprest.filter.CustomException;
import com.erprest.model.Address;
import com.erprest.model.City;
import com.erprest.model.Config;
import com.erprest.model.Customer;
import com.erprest.model.CustomerType;
import com.erprest.model.District;
import com.erprest.model.Integration;
import com.erprest.model.Neighborhood;
import com.erprest.model.Tenant;
import com.erprest.model.TenantUsers;
import com.erprest.model.User;
import com.erprest.model.UserRole;
import com.erprest.service.TokenUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
public class EInvoiceDao {

    private static final Logger logger = Logger.getLogger(EInvoiceDao.class.getName());
    TokenUtilities token;
    Config config;

    public EInvoiceDao() {
        config = ProjectConfig.getInstance().getConfig();
        this.token = new TokenUtilities();
    }

    public void registerAdmin(String name, String email, String einvoiceUsername, String einvoicePassword) throws CustomException {
        TenantDao tenantdao = new TenantDao();
        UserDao userdao = new UserDao();
        Tenant authTenant = (tenantdao.getSubTenants(null, null, null, null, "SUPERADMIN", null, null, null, 1, 0).get(0));
        List<Tenant> existingTenant = tenantdao.getSubTenants(null, null, null, email, null, null, null, null, 1, 0);
        if (!existingTenant.isEmpty()) {
            existingTenant.get(0).setEinvoiceUsername(einvoiceUsername);
            existingTenant.get(0).setEinvoicePassword(einvoicePassword);
            tenantdao.updateTenant(authTenant, existingTenant.get(0));
            User admin = getConnectedAdmin(einvoiceUsername, einvoicePassword);
            admin.getTenantUsers().getUserRole().setIsEinvoiceAdmin(true);
            tenantdao.updateTenantUsers(admin, existingTenant.get(0).getId());
        } else {
            Tenant newTenant = new Tenant();
            newTenant.setEmail(email);
            if (!userdao.isValidEmailAddress(newTenant.getEmail())) {
                throw new CustomException("Geçerli bir email adresi giriniz.");
            }
            newTenant.setId(UUID.randomUUID().toString());
            newTenant.setTenantType("RESELLER");
            Customer tenantInfo = new Customer();
            tenantInfo.setAppellation(name);
            tenantInfo.setFullAppellation(name);
            newTenant.setName(name);
            tenantInfo.setTc("1111111111");
            Address address = new Address();
            address.setEmail(email);
            address.setCity(new City());
            address.setDistrict(new District());
            address.setNeighborhood(new Neighborhood());
            List<Address> addresses = new ArrayList<>();
            addresses.add(address);
            tenantInfo.setAddress(addresses);
            CustomerType customerType = new CustomerType();
            customerType.setId(1);
            tenantInfo.setCustomerType(customerType);
            newTenant.setTenantInfo(tenantInfo);
            newTenant.setExpireCount(365);
            newTenant.setEinvoiceUsername(einvoiceUsername);
            newTenant.setEinvoicePassword(einvoicePassword);

            newTenant.setOwner(authTenant.getId());
            User authUser = new User();
            authUser.setTenantUsers(new TenantUsers());
            authUser.getTenantUsers().setTenant(authTenant);

            User newUser = new User();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setPassword(einvoicePassword);
            newUser.setEmail(newTenant.getEmail());
            newUser.setName(name);
            newUser.setDescription("EFATURAADMİN");
            newUser.setStatus("ENABLED");

            newUser.setTenantUsers(new TenantUsers());
            UserRole userRole = new UserRole();
            userRole.setIsTenantAdmin(true);
            authUser.getTenantUsers().setUserRole(userRole);
            newUser.getTenantUsers().setTenant(newTenant);
            UserRole role = new UserRole();
            role.setUser("a");
            role.setCustomer("x");
            role.setTenant("a");
            role.setItem("x");
            role.setInvoice("x");
            role.setAccount("x");
            role.setIsTenantAdmin(true);
            role.setIsEinvoiceAdmin(true);
            newUser.getTenantUsers().setUserRole(role);
            tenantdao.addSubTenant(authUser, newUser);
        }
    }

    public User getConnectedAdmin(String einvoiceUsername, String einvoicePassword) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        UserDao userDao = new UserDao();
        User adminUser = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select u.id from users u\n"
                    + "                     join tenant_users tu on u.id=tu.user_id\n"
                    + "                     join tenants t on t.id=tu.tenant_id and t.einvoice_username=? and t.einvoice_password=?\n"
                    + "                     join user_roles ur on ur.id=tu.user_roles_id \n"
                    + "                         limit 1;");
            ps.setString(1, einvoiceUsername);
            ps.setString(2, einvoicePassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                adminUser = userDao.getUserInfo(rs.getString("id"));
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return adminUser;
    }

    public void registerMukellef(User authAdmin, Integration integration) throws CustomException {
        TenantDao tenantdao = new TenantDao();
        UserDao userdao = new UserDao();
        List<Tenant> existingTenant = tenantdao.getSubTenants(null, null, null, integration.getEmail(), null, null, null, null, 1, 0);
        if (!existingTenant.isEmpty()) {
            existingTenant.get(0).setEinvoiceUsername(integration.getEmail());
            existingTenant.get(0).setEinvoicePassword(integration.getPassword());
            existingTenant.get(0).getTenantInfo().setTc(integration.getPartyId());
            existingTenant.get(0).getTenantInfo().setIsEfaturaUser(true);
            existingTenant.get(0).getTenantInfo().setSenderIdentifier(integration.getGb());
            tenantdao.updateTenant(authAdmin.getTenantUsers().getTenant(), existingTenant.get(0));
        } else {
            Tenant newTenant = new Tenant();

            newTenant.setEmail(integration.getEmail());
            if (!userdao.isValidEmailAddress(newTenant.getEmail())) {
                throw new CustomException("Geçerli bir email adresi giriniz.");
            }
            newTenant.setId(UUID.randomUUID().toString());
            newTenant.setTenantType("CUSTOMER");
            Customer tenantInfo = new Customer();
            if (integration.getPartyId().length() == 11) {
                tenantInfo.setName(integration.getPartyName());
                tenantInfo.setSurname(integration.getPartyName());
            } else if (integration.getPartyId().length() == 10) {
                tenantInfo.setAppellation(integration.getPartyName());
                tenantInfo.setFullAppellation(integration.getPartyName());
            }
            newTenant.setName(integration.getPartyName());
            tenantInfo.setTc(integration.getPartyId());
            Address address = new Address();
            address.setEmail(integration.getEmail());
            address.setCity(new City());
            address.setDistrict(new District());
            address.setNeighborhood(new Neighborhood());
            List<Address> addresses = new ArrayList<>();
            addresses.add(address);
            tenantInfo.setAddress(addresses);
            CustomerType customerType = new CustomerType();
            customerType.setId(2);
            tenantInfo.setCustomerType(customerType);
            tenantInfo.setIsEfaturaUser(true);
            tenantInfo.setSenderIdentifier(integration.getGb());
            newTenant.setTenantInfo(tenantInfo);
            newTenant.setExpireCount(365);
            newTenant.setEinvoiceUsername(integration.getEmail());
            newTenant.setEinvoicePassword(integration.getPassword());

            newTenant.setOwner(authAdmin.getTenantUsers().getTenant().getId());
            User newUser = new User();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setPassword(integration.getPassword());
            newUser.setEmail(newTenant.getEmail());
            newUser.setName(integration.getPartyName());
            newUser.setDescription("EFATURAUSER");
            newUser.setStatus("ENABLED");

            newUser.setTenantUsers(new TenantUsers());
            newUser.getTenantUsers().setTenant(newTenant);
            UserRole role = new UserRole();
            role.setUser("x");
            role.setCustomer("a");
            role.setTenant("x");
            role.setItem("x");
            role.setInvoice("a");
            role.setAccount("a");
            role.setIsTenantAdmin(true);
            role.setIsEinvoiceAdmin(false);
            newUser.getTenantUsers().setUserRole(role);
            tenantdao.addSubTenant(authAdmin, newUser);
        }
    }

}
