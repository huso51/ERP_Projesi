/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.filter.CustomException;
import com.erprest.model.City;
import com.erprest.model.Customer;
import com.erprest.model.Address;
import com.erprest.model.CustomerType;
import com.erprest.model.District;
import com.erprest.model.Neighborhood;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
public class CustomerDao {

    public List<City> getCities() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<City> cities = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from cities order by name");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                City city = new City();
                city.setId(rs.getLong("id"));
                city.setName(rs.getString("name"));
                cities.add(city);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return cities;
    }

    public List<District> getDistricts(long city_id) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<District> districts = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from districts where city_id=? order by name");
            ps.setLong(1, city_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                District district = new District();
                district.setId(rs.getLong("id"));
                district.setName(rs.getString("name"));
                districts.add(district);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return districts;
    }

    public List<Neighborhood> getNeighborhoods(long district_id) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<Neighborhood> neighborhoods = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from neighborhoods where district_id=? order by name");
            ps.setLong(1, district_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Neighborhood neighbor = new Neighborhood();
                neighbor.setId(rs.getLong("id"));
                neighbor.setName(rs.getString("name"));
                neighbor.setPostCode(rs.getLong("PK"));
                neighborhoods.add(neighbor);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return neighborhoods;
    }

    public long addCustomer(Customer customer) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        //GibDao gibdao = new GibDao();
        try {
            conn = dbhelper.getConnection();
            int psIndex = 1;
            ps = conn.prepareStatement("insert into customers (tenant_id,name,surname,appellation,full_appellation,customer_type_id,tax_administration,tc,trade_register_number,mersis_number,basic_discount,credit_limit,assent,sender_identifier,is_efatura_user)\n"
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(psIndex++, customer.getTenantId());
            ps.setString(psIndex++, customer.getName());
            ps.setString(psIndex++, customer.getSurname());
            ps.setString(psIndex++, customer.getAppellation());
            ps.setString(psIndex++, customer.getFullAppellation());
            ps.setLong(psIndex++, customer.getCustomerType().getId());
            ps.setString(psIndex++, customer.getTaxAdministration());
            ps.setString(psIndex++, customer.getTc());
            ps.setString(psIndex++, customer.getTradeRegisterNumber());
            ps.setString(psIndex++, customer.getMersisNumber());
            ps.setLong(psIndex++, customer.getBasicDiscount());
            ps.setLong(psIndex++, customer.getCreditLimit());
            ps.setBoolean(psIndex++, customer.isIsAssent());
            ps.setString(psIndex++, customer.getSenderIdentifier());
            ps.setBoolean(psIndex++, customer.isIsEfaturaUser());
            ps.executeUpdate();

            ResultSet keys1 = ps.getGeneratedKeys();
            keys1.next();
            customer.setId(keys1.getInt(1));

            psIndex = 1;
            ps = conn.prepareStatement("insert into address (customer_id,address_name,city_id,district_id,neighborhood_id,phone_number,email,fax,full_address)\n"
                    + "values (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(psIndex++, customer.getId());
            ps.setString(psIndex++, customer.getAddress().get(0).getAddressName());
            ps.setLong(psIndex++, customer.getAddress().get(0).getCity().getId());
            ps.setLong(psIndex++, customer.getAddress().get(0).getDistrict().getId());
            ps.setLong(psIndex++, customer.getAddress().get(0).getNeighborhood().getId());
            ps.setString(psIndex++, customer.getAddress().get(0).getPhoneNumber());
            ps.setString(psIndex++, customer.getAddress().get(0).getEmail());
            ps.setString(psIndex++, customer.getAddress().get(0).getFax());
            ps.setString(psIndex++, customer.getAddress().get(0).getFullAddress());
            ps.executeUpdate();

            ResultSet keys2 = ps.getGeneratedKeys();
            keys2.next();
            customer.getAddress().get(0).setId((keys2.getInt(1)));

            ps = conn.prepareStatement("update customers set default_address_id=? where id=?");
            ps.setLong(1, customer.getAddress().get(0).getId());
            ps.setLong(2, customer.getId());
            ps.executeUpdate();

            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return customer.getId();
    }

    public List<Customer> getCustomers(String tenantId, long whereId, String whereName, String whereAppellation, long whereCustomerTypeId, String whereTaxAdministration, String whereDateAfter, String whereDateBefore, String order_by, long limit, long offset) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<Customer> customers = new ArrayList<>();
        String whereParameter = "", temp = "";
        ArrayList paramList = new ArrayList();

        temp += " and c.tenant_id=?";
        paramList.add(tenantId);
        if (whereName != null && !whereName.equals("")) {
            temp += " and c.name like ?";
            paramList.add("%" + whereName + "%");
        }
        if (whereId != 0) {
            temp += " and c.id=?";
            paramList.add(whereId);
        }
        if (whereAppellation != null && !whereAppellation.equals("")) {
            temp += " and full_appellation like ?";
            paramList.add("%" + whereAppellation + "%");
        }
        if (whereCustomerTypeId != 0) {
            temp += " and customer_type_id=?";
            paramList.add(whereCustomerTypeId);
        }
        if (whereTaxAdministration != null && !whereTaxAdministration.equals("")) {
            temp += " and tax_administration=?";
            paramList.add(whereTaxAdministration);
        }
        if (whereDateAfter != null && !whereDateAfter.equals("")) {
            temp += " and created_at > ?";
            paramList.add(whereDateAfter);
        }
        if (whereDateBefore != null && !whereDateBefore.equals("")) {
            temp += " and created_at < ?";
            paramList.add(whereDateBefore);
        }
        if (order_by == null) {
            order_by = "";
        } else {
            order_by = " order by " + order_by;
        }
        whereParameter = temp;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select c.id,c.tenant_id,c.name,c.surname,c.appellation,c.full_appellation,c.customer_type_id,c.tax_administration,c.tc,c.trade_register_number,c.mersis_number,c.basic_discount,c.credit_limit,c.assent,c.remainder,c.confirmed_at,c.created_at,\n"
                    + "                     ct.id as ct_id,ct.name as ct_name,ct.description as ct_description,\n"
                    + "                     a1.id as a1_id,a1.customer_id as a1_customer_id,a1.address_name as a1_address_name,a1.city_id as a1_city_id,a1.district_id as a1_district_id,a1.neighborhood_id as a1_neighborhood_id,a1.email as a1_email,a1.fax as a1_fax,a1.full_address as a1_full_address,\n"
                    + "                     ct1.id as ct1_id,ct1.name as ct1_name,\n"
                    + "                     dt1.id as dt1_id,dt1.name as dt1_name,\n"
                    + "                     nb1.id as nb1_id,nb1.name as nb1_name,nb1.PK as nb1_PK\n"
                    + "                 from customers c\n"
                    + "				join customers_type ct on c.customer_type_id=ct.id\n"
                    + "                         left join address a1 on a1.id=c.default_address_id\n"
                    + "				left join cities ct1 on ct1.id =a1.city_id\n"
                    + "				left join districts dt1 on dt1.id=a1.district_id\n"
                    + "				left join neighborhoods nb1 on nb1.id=a1.neighborhood_id\n"
                    + "                             where 1=1 and c.id<>(select tenant_info_id from tenants where id=c.tenant_id) \n"
                    + whereParameter + order_by + " limit " + limit + " offset " + offset);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CustomerType ctype = new CustomerType();
                ctype.setId(rs.getLong("ct_id"));
                ctype.setName(rs.getString("ct_name"));
                ctype.setDescription(rs.getString("ct_description"));

                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
                //customer.setTenantId(tenantId);
                customer.setName(rs.getString("name"));
                customer.setSurname(rs.getString("surname"));
                customer.setAppellation(rs.getString("appellation"));
                customer.setFullAppellation(rs.getString("full_Appellation"));
                customer.setCustomerTypeId(rs.getLong("customer_type_id"));
                customer.setTaxAdministration(rs.getString("tax_administration"));
                customer.setTc(rs.getString("tc"));
                customer.setTradeRegisterNumber(rs.getString("trade_register_number"));
                customer.setMersisNumber(rs.getString("mersis_number"));
                customer.setBasicDiscount(rs.getLong("basic_discount"));
                customer.setCreditLimit(rs.getLong("credit_limit"));
                customer.setIsAssent(rs.getBoolean("assent"));
                customer.setRemainder(rs.getDouble("remainder"));
                customer.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("created_at")));
                if (rs.getString("confirmed_at") != null && !rs.getString("confirmed_at").equals("")) {
                    customer.setConfirmedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("confirmed_at")));
                }
                customer.setCustomerType(ctype);
                List<Address> addresses = new ArrayList<>();
                Address address = new Address();
                address.setId(rs.getLong("a1_id"));
                address.setCustomerId(rs.getLong("a1_customer_id"));
                address.setAddressName(rs.getString("a1_address_name"));
                address.setCityId(rs.getLong("a1_city_id"));
                address.setDistrictId(rs.getLong("a1_district_id"));
                address.setNeighborhoodId(rs.getLong("a1_neighborhood_id"));
                address.setEmail(rs.getString("a1_email"));
                address.setFax(rs.getString("a1_fax"));
                address.setFullAddress(rs.getString("a1_full_address"));

                City city = new City();
                city.setId(rs.getLong("ct1_id"));
                city.setName(rs.getString("ct1_name"));
                address.setCity(city);

                District district = new District();
                district.setId(rs.getLong("dt1_id"));
                district.setName(rs.getString("dt1_name"));
                address.setDistrict(district);

                Neighborhood neigh = new Neighborhood();
                neigh.setId(rs.getLong("nb1_id"));
                neigh.setName(rs.getString("nb1_name"));
                neigh.setPostCode(rs.getLong("nb1_PK"));
                address.setNeighborhood(neigh);
                addresses.add(address);
                customer.setAddress(addresses);

                customers.add(customer);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return customers;
    }

    public List<Address> getAddress(long customerId, long addressId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        String whereParameter, temp = "";
        ArrayList paramList = new ArrayList();
        if (customerId != 0) {
            temp += " and customer_id=?";
            paramList.add(customerId);
        }
        if (addressId != 0) {
            temp += " and ca.id=?";
            paramList.add(addressId);
        }
        whereParameter = temp;
        List<Address> addresses = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select ca.id,ca.customer_id,ca.address_name,ca.city_id,ca.district_id,ca.neighborhood_id,ca.phone_number,ca.email,ca.fax,ca.full_address,\n"
                    + "		c.id as c_id,c.name as c_name,\n"
                    + "         d.id as d_id,d.name as d_name,\n"
                    + "         n.id as n_id,n.name as n_name,n.PK as n_PK,\n"
                    + "         if(ca.id=(select default_address_id from customers where id=ca.customer_id),true,false) as is_default_address\n"
                    + "                         from address ca\n"
                    + "                		join cities c on c.id=ca.city_id\n"
                    + "                         join districts d on d.id=ca.district_id\n"
                    + "                         left join neighborhoods n on n.id=ca.neighborhood_id\n "
                    + "                         where 1=1 " + whereParameter);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Address address = new Address();
                City city = new City();
                District district = new District();
                Neighborhood neighbor = new Neighborhood();

                city.setId(rs.getLong("c_id"));
                city.setName(rs.getString("c_name"));
                district.setId(rs.getLong("d_id"));
                district.setName(rs.getString("d_name"));
                neighbor.setId(rs.getLong("n_id"));
                neighbor.setName(rs.getString("n_name"));
                neighbor.setPostCode(rs.getLong("n_PK"));

                address.setCity(city);
                address.setDistrict(district);
                address.setNeighborhood(neighbor);
                address.setId(rs.getLong("id"));
                address.setCustomerId(rs.getLong("customer_id"));
                address.setAddressName(rs.getString("address_name"));
                address.setPhoneNumber(rs.getString("phone_number"));
                address.setEmail(rs.getString("email"));
                address.setFax(rs.getString("fax"));
                address.setFullAddress(rs.getString("full_address"));
                address.setIsDefaultAddress(rs.getBoolean("is_default_address"));

                addresses.add(address);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return addresses;
    }

    public void updateCustomer(Customer customer) throws CustomException {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (customer.getName() != null) {
            whereParameters += ",name=?";
            paramList.add(customer.getName());
        }
        if (customer.getSurname() != null) {
            whereParameters += ",surname=?";
            paramList.add(customer.getSurname());
        }
        if (customer.getAppellation() != null) {
            whereParameters += ",appellation=?";
            paramList.add(customer.getAppellation());
        }
        if (customer.getFullAppellation() != null) {
            whereParameters += ",full_appellation=?";
            paramList.add(customer.getFullAppellation());
        }
        if (customer.getCustomerType().getId() != 0) {
            whereParameters += ",customer_type_id=?";
            paramList.add(customer.getCustomerType().getId());
        }
        if (customer.getTaxAdministration() != null && !customer.getTaxAdministration().equals("")) {
            whereParameters += ",tax_administration=?";
            paramList.add(customer.getTaxAdministration());
        }
        if (customer.getTc() != null && !customer.getTc().equals("")) {
            whereParameters += ",tc=?";
            paramList.add(customer.getTc());
        }
        if (customer.getTradeRegisterNumber() != null) {
            whereParameters += ",trade_register_number=?";
            paramList.add(customer.getTradeRegisterNumber());
        }
        if (customer.getMersisNumber() != null) {
            whereParameters += ",mersis_number=?";
            paramList.add(customer.getMersisNumber());
        }
        if (customer.getBasicDiscount() != -1) {
            whereParameters += ",basic_discount=?";
            paramList.add(customer.getBasicDiscount());
        }
        if (customer.getCreditLimit() != -1) {
            whereParameters += ",credit_limit=?";
            paramList.add(customer.getCreditLimit());
        }
        if (customer.isIsEfaturaUser()) {
            whereParameters += ",is_efatura_user=true";
        }
        if (customer.getSenderIdentifier() != null) {
            whereParameters += ",sender_identifier=?";
            paramList.add(customer.getSenderIdentifier());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from customers where id=? and tenant_id=?");
            ps.setLong(1, customer.getId());
            ps.setString(2, customer.getTenantId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CustomException("Bu müşteriyi güncellemek için yetkiniz yoktur.");
            }

            ps = conn.prepareStatement("update customers set id=id" + whereParameters + " where id=?");
            paramList.add(customer.getId());
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

    public void updateAddress(Address address) {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (address.getAddressName() != null && !address.getAddressName().equals("")) {
            whereParameters += ",address_name=?";
            paramList.add(address.getAddressName());
        }
        if (address.getCity().getId() != 0) {
            whereParameters += ",city_id=?";
            paramList.add(address.getCity().getId());
        }
        if (address.getDistrict().getId() != 0) {
            whereParameters += ",district_id=?";
            paramList.add(address.getDistrict().getId());
        }
        if (address.getNeighborhood().getId() != 0) {
            whereParameters += ",neighborhood_id=?";
            paramList.add(address.getNeighborhood().getId());
        }
        if (address.getPhoneNumber() != null && !address.getPhoneNumber().equals("")) {
            whereParameters += ",phone_number=?";
            paramList.add(address.getPhoneNumber());
        }
        if (address.getEmail() != null && !address.getEmail().equals("")) {
            whereParameters += ",email=?";
            paramList.add(address.getEmail());
        }
        if (address.getFax() != null && !address.getFax().equals("")) {
            whereParameters += ",fax=?";
            paramList.add(address.getFax());
        }
        if (address.getFullAddress() != null && !address.getFullAddress().equals("")) {
            whereParameters += ",full_address=?";
            paramList.add(address.getFullAddress());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update address set id=id" + whereParameters + " where id=?");
            paramList.add(address.getId());
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ps.executeUpdate();
            if (address.isIsDefaultAddress() && address.getCustomerId() != 0) {
                ps = conn.prepareStatement("update customers set default_address_id="
                        + "(select id from address where customer_id=? and id=?) "
                        + "where id=?");
                ps.setLong(1, address.getCustomerId());
                ps.setLong(2, address.getId());
                ps.setLong(3, address.getCustomerId());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void addAddress(Address address) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = dbhelper.getConnection();
            int psIndex = 1;
            ps = conn.prepareStatement("insert into address (customer_id,address_name,city_id,district_id,neighborhood_id,phone_number,email,fax,full_address)\n"
                    + "values (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setLong(psIndex++, address.getCustomerId());
            ps.setString(psIndex++, address.getAddressName());
            ps.setLong(psIndex++, address.getCity().getId());
            ps.setLong(psIndex++, address.getDistrict().getId());
            ps.setLong(psIndex++, address.getNeighborhood().getId());
            ps.setString(psIndex++, address.getPhoneNumber());
            ps.setString(psIndex++, address.getEmail());
            ps.setString(psIndex++, address.getFax());
            ps.setString(psIndex++, address.getFullAddress());
            ps.executeUpdate();

            ResultSet keys1 = ps.getGeneratedKeys();
            keys1.next();
            address.setId(keys1.getInt(1));

            if (address.isIsDefaultAddress() && address.getCustomerId() != 0) {
                ps = conn.prepareStatement("update customers set default_address_id="
                        + "(select id from address where customer_id=? and id=?) "
                        + "where id=?");
                ps.setLong(1, address.getCustomerId());
                ps.setLong(2, address.getId());
                ps.setLong(3, address.getCustomerId());
                ps.executeUpdate();
            }

            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<CustomerType> getCustomerType() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<CustomerType> types = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from customers_type");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CustomerType type = new CustomerType();
                type.setId(rs.getLong("id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                types.add(type);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return types;
    }

    public void deleteCustomer(List<Customer> customers) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            StringBuilder arrayStatement = new StringBuilder();
            arrayStatement.append("(");
            for (int i = 0; i < customers.size() - 1; i++) {
                arrayStatement.append("?,");
            }
            arrayStatement.append("?)");
            ps = conn.prepareStatement("delete c,a from customers c inner join address a \n"
                    + "                       where (c.default_address_id=a.id or a.customer_id=c.id) and c.id in " + arrayStatement.toString() + " \n"
                    + "                         and tenant_id=?");

            for (int i = 0; i < customers.size(); i++) {
                ps.setLong(i + 1, customers.get(i).getId());
            }
            ps.setString(customers.size() + 1, customers.get(0).getTenantId());
            ps.executeUpdate();

            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void addRemainder(Customer customer, Double amount) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update customers set remainder=remainder+? where id=?");
            ps.setDouble(1, amount);
            ps.setLong(2, customer.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }
}
