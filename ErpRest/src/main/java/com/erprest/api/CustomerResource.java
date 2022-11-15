/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.CustomerDao;
import com.erprest.filter.CustomException;
import com.erprest.model.City;
import com.erprest.model.Customer;
import com.erprest.model.Address;
import com.erprest.model.CustomerType;
import com.erprest.model.District;
import com.erprest.model.Neighborhood;
import com.erprest.model.ResponseData;
import com.erprest.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
@Path("sessions/customers")
public class CustomerResource {

    @Context
    private transient HttpServletRequest servletRequest;
    Gson gson = new Gson();

    @POST
    @Path("/getCities")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCities() {
        ResponseData<List<City>> response = new ResponseData<>();
        CustomerDao customerdao = new CustomerDao();
        List<City> cities = customerdao.getCities();
        response.setData(cities);
        return response.finalResponse();
    }

    @POST
    @Path("/getDistricts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDistricts(@FormParam("cityId") long city_id) {
        ResponseData<List<District>> response = new ResponseData<>();
        CustomerDao customerdao = new CustomerDao();
        List<District> districts = customerdao.getDistricts(city_id);
        response.setData(districts);
        return response.finalResponse();
    }

    @POST
    @Path("/getNeighborhoods")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNeighborhoods(@FormParam("districtId") long districtId) {
        ResponseData<List<Neighborhood>> response = new ResponseData<>();
        CustomerDao customerdao = new CustomerDao();
        List<Neighborhood> neighborhoods = customerdao.getNeighborhoods(districtId);
        response.setData(neighborhoods);
        return response.finalResponse();
    }

    @POST
    @Path("/addCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomer(
            @FormParam("customer") String customerJson) {
        ResponseData<Response> response = new ResponseData();
        Customer customer = gson.fromJson(customerJson, Customer.class);
        if (customerJson == null || customerJson.equals("")) {
            response.setError(406, "customer parametresi boş geçilemez");
        } else {
            User authUser = (User) this.servletRequest.getAttribute("authUser");
            customer.setTenantId(authUser.getTenantUsers().getTenant().getId());
            CustomerDao customerdao = new CustomerDao();
            customerdao.addCustomer(customer);
            response.succeed("Müşteri ekleme tamamlandı.");
        }
        return response.finalResponse();
    }

    @POST
    @Path("/getCustomers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(
            @FormParam("whereId") long whereId,
            @FormParam("whereName") String whereName,
            @FormParam("whereAppellation") String whereAppellation,
            @FormParam("whereCustomerTypeId") long whereCustomerTypeId,
            @FormParam("whereTaxAdministration") String whereTaxAdministration,
            @FormParam("whereDateAfter") String whereDateAfter,
            @FormParam("whereDateBefore") String whereDateBefore,
            @FormParam("orderBy") String order_by,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {

        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<Customer>> response = new ResponseData<>();
        CustomerDao customerdao = new CustomerDao();
        List<Customer> customers = customerdao.getCustomers(authUser.getTenantUsers().getTenant().getId(),
                whereId, whereName, whereAppellation, whereCustomerTypeId, whereTaxAdministration,
                whereDateAfter, whereDateBefore, order_by, limit, offset);
        response.setData(customers);
        return response.finalResponse();
    }

    @POST
    @Path("/getAddress")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddress(@FormParam("customerId") long customerId,
            @FormParam("addressId") long addressId) {
        CustomerDao customerdao = new CustomerDao();
        ResponseData<List<Address>> response = new ResponseData<>();
        List<Address> addresses = customerdao.getAddress(customerId, addressId);
        response.setData(addresses);
        return response.finalResponse();
    }

    @POST
    @Path("/updateCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(
            @FormParam("customer") String customerJson) throws CustomException {
        ResponseData<Response> response = new ResponseData();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        Customer customer = gson.fromJson(customerJson, Customer.class);
        customer.setTenantId(authUser.getTenantUsers().getTenant().getId());
        if (authUser.getTenantUsers().getTenant().getTenantInfo().isIsEfaturaUser()) {
            customer.setTc(null);
        }
        CustomerDao customerdao = new CustomerDao();
        customerdao.updateCustomer(customer);
        response.succeed("Müşteri bilgileri güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateAddress")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAddress(
            @FormParam("address") String addressJson) {
        ResponseData<Response> response = new ResponseData();
        Address address = gson.fromJson(addressJson, Address.class);
        CustomerDao customerdao = new CustomerDao();
        customerdao.updateAddress(address);
        response.succeed("Adres bilgileri güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/addAddress")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAddress(
            @FormParam("address") String addressJson) {
        ResponseData<Response> response = new ResponseData();
        Address address = gson.fromJson(addressJson, Address.class);
        CustomerDao customerdao = new CustomerDao();
        customerdao.addAddress(address);
        response.succeed("Adres ekleme tamamlandı.");
        return response.finalResponse();

    }

    @POST
    @Path("/getCustomerTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerType() {
        ResponseData<List<CustomerType>> response = new ResponseData<>();
        CustomerDao customerdao = new CustomerDao();
        List<CustomerType> types = customerdao.getCustomerType();
        response.setData(types);
        return response.finalResponse();
    }

    @POST
    @Path("/deleteCustomers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomers(
            @FormParam("customer") String customerJson) {
        ResponseData<Response> response = new ResponseData();
        List<Customer> customers = gson.fromJson(customerJson, new TypeToken<List<Customer>>() {
        }.getType());
        CustomerDao customerdao = new CustomerDao();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        customers.get(0).setTenantId(authUser.getTenantUsers().getTenant().getId());
        customerdao.deleteCustomer(customers);
        response.succeed("Müşteri silme tamamlandı.");
        return response.finalResponse();
    }
}
