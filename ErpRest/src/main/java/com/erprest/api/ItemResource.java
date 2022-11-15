/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.api;

import com.erprest.dao.ItemDao;
import com.erprest.filter.CustomException;
import com.erprest.model.Category;
import com.erprest.model.Currency;
import com.erprest.model.Item;
import com.erprest.model.ItemTaxes;
import com.erprest.model.ResponseData;
import com.erprest.model.Storage;
import com.erprest.model.TaxesType;
import com.erprest.model.UomCode;
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
@Path("sessions/items")
public class ItemResource {

    @Context
    private transient HttpServletRequest servletRequest;

    Gson gson = new Gson();

    @POST
    @Path("/addCategory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(
            @FormParam("name") String name,
            @FormParam("description") String description) {
        ResponseData<Response> response = new ResponseData<>();
        if (name == null || name.equals("")) {
            response.setError(406, "name boş bırakılamaz");
            return response.finalResponse();
        }
        User authUser = (User) this.servletRequest.getAttribute("authUser");

        ItemDao itemdao = new ItemDao();
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setTenantId(authUser.getTenantUsers().getTenant().getId());
        itemdao.addCategory(category);
        response.succeed("Kategori ekleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/getCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories(
            @FormParam("categoryId") long categoryId,
            @FormParam("storageId") long storageId) {
        ResponseData<List<Category>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");

        Category category = new Category();
        category.setId(categoryId);
        category.setTenantId(authUser.getTenantUsers().getTenant().getId());

        Storage storage = new Storage();
        storage.setId(storageId);
        ItemDao itemdao = new ItemDao();
        List<Category> categories = itemdao.getCategories(category, storage);
        response.setData(categories);
        return response.finalResponse();
    }

    @POST
    @Path("/deleteCategory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategories(
            @FormParam("categoryId") long categoryId) {
        ResponseData<Response> response = new ResponseData<>();
        if (categoryId == 0) {
            response.setError(406, "categoryId boş bırakılamaz");
            return response.finalResponse();
        }
        User authUser = (User) this.servletRequest.getAttribute("authUser");

        Category category = new Category();
        category.setId(categoryId);
        category.setTenantId(authUser.getTenantUsers().getTenant().getId());

        ItemDao itemdao = new ItemDao();
        itemdao.deleteCategory(category);
        response.succeed("Kategori silindi.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateCategory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategories(
            @FormParam("categoryId") long categoryId,
            @FormParam("name") String name,
            @FormParam("description") String description) {
        ResponseData<Response> response = new ResponseData<>();
        if (categoryId == 0) {
            response.setError(406, "categoryId boş bırakılamaz");
            return response.finalResponse();
        }
        User authUser = (User) this.servletRequest.getAttribute("authUser");

        Category category = new Category();
        category.setId(categoryId);
        category.setTenantId(authUser.getTenantUsers().getTenant().getId());
        category.setName(name);
        category.setDescription(description);

        ItemDao itemdao = new ItemDao();
        itemdao.updateCategory(category);
        response.succeed("Kategori güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/getItems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(
            @FormParam("whereId") long whereId,
            @FormParam("whereName") String whereName,
            @FormParam("whereCategoryId") long whereCategoryId,
            @DefaultValue("-1") @FormParam("whereStorageId") long whereStorageId,
            @FormParam("whereDateAfter") String whereDateAfter,
            @FormParam("whereDateBefore") String whereDateBefore,
            @FormParam("orderBy") String order_by,
            @DefaultValue("20") @FormParam("limit") long limit,
            @DefaultValue("0") @FormParam("offset") long offset) {
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        ResponseData<List<Item>> response = new ResponseData<>();
        Item item = new Item();
        item.setId(whereId);
        item.setTenantId(authUser.getTenantUsers().getTenant().getId());
        item.setName(whereName);
        item.setCategoryId(whereCategoryId);
        Storage storage = new Storage();
        storage.setId(whereStorageId);
        item.setStorage(storage);

        ItemDao itemdao = new ItemDao();
        List<Item> items = itemdao.getItems(item, whereDateAfter, whereDateBefore, order_by, limit, offset);
        response.setData(items);
        return response.finalResponse();
    }

    @POST
    @Path("/getUomCodes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUomCodes() {
        ResponseData<List<UomCode>> response = new ResponseData<>();
        ItemDao itemdao = new ItemDao();
        List<UomCode> uomcodes = itemdao.getUomCodes();
        response.setData(uomcodes);
        return response.finalResponse();
    }

    @POST
    @Path("/getCurrency")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrencies() {
        ResponseData<List<Currency>> response = new ResponseData<>();
        ItemDao itemdao = new ItemDao();
        List<Currency> currencies = itemdao.getCurrencies();
        response.setData(currencies);
        return response.finalResponse();
    }

    @POST
    @Path("/getTaxesType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTaxesType() {
        ResponseData<List<TaxesType>> response = new ResponseData<>();
        ItemDao itemdao = new ItemDao();
        List<TaxesType> types = itemdao.getTaxesType();
        response.setData(types);
        return response.finalResponse();
    }

    @POST
    @Path("/addItem")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItem(@FormParam("item") String itemsJson) {
        ResponseData<Response> response = new ResponseData<>();
        if (itemsJson == null || itemsJson.equals("")) {
            response.setError(406, "item boş bırakılamaz");
            return response.finalResponse();
        }
        //itemsJson="{\"itemTaxes\":[{\"id\":1,\"code\":\"kdv\",\"description\":\"katma değer vergisi\",\"value\":18},{\"id\":2,\"code\":\"ötv\",\"description\":\"özel tüketim vergisi\",\"value\":15}],\"category\":{\"id\":36},\"currency\":{\"id\":1},\"uomCode\":{\"id\":1},\"name\":\"Bilgisayar\",\"stock\":10,\"price\":3000}";
        Item item = gson.fromJson(itemsJson, Item.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");

        ItemDao itemdao = new ItemDao();
        item.setTenantId(authUser.getTenantUsers().getTenant().getId());
        itemdao.addItem(item);
        response.succeed("Ürün ekleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateItem")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItem(
            @FormParam("item") String json) throws CustomException {
        ResponseData<Response> response = new ResponseData<>();
        Item item = gson.fromJson(json, Item.class);
        if (item.getId() == 0) {
            response.setError(406, "item.id boş bırakılamaz");
            return response.finalResponse();
        }
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        item.setTenantId(authUser.getTenantUsers().getTenant().getId());
        ItemDao itemdao = new ItemDao();
        itemdao.updateItem(item);
        response.succeed("Ürün güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/deleteItemTaxes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteItemTaxes(
            @FormParam("itemTaxes") String json) {
        ResponseData<Response> response = new ResponseData<>();
        ItemTaxes itemtaxes = gson.fromJson(json, ItemTaxes.class);
        if (itemtaxes.getId() == 0) {
            response.setError(406, "itemTaxes.id boş bırakılamaz");
            return response.finalResponse();
        }
        ItemDao itemdao = new ItemDao();
        itemdao.deleteItemTaxes(itemtaxes.getId());
        response.succeed("Ürünün vergisi silindi.");
        return response.finalResponse();
    }

    @POST
    @Path("/deleteItems")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteItem(
            @FormParam("items") String itemsJson) {
        ResponseData<Response> response=new ResponseData<>();
        List<Item> items = gson.fromJson(itemsJson, new TypeToken<List<Item>>() {
        }.getType());
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        items.get(0).setTenantId(authUser.getTenantUsers().getTenant().getId());
        ItemDao itemdao = new ItemDao();
        itemdao.deleteItem(items);
        response.succeed("Ürün silindi.");
        return response.finalResponse();
    }

    @POST
    @Path("/getStorage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStorage(
            @FormParam("storageId") long storageId) {
        ResponseData<List<Storage>> response = new ResponseData<>();
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        Storage storage = new Storage();
        storage.setId(storageId);
        storage.setTenant(authUser.getTenantUsers().getTenant());
        ItemDao itemdao = new ItemDao();
        List<Storage> storages = itemdao.getStorage(storage);
        response.setData(storages);
        return response.finalResponse();
    }

    @POST
    @Path("/addStorage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStorage(@FormParam("storage") String storageJson) {
        ResponseData<Response> response=new ResponseData<>();
        Storage storage = gson.fromJson(storageJson, Storage.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        storage.setTenant(authUser.getTenantUsers().getTenant());
        ItemDao itemdao = new ItemDao();
        itemdao.addStorage(storage);
        response.succeed("Depo ekleme tamamlandı.");
        return response.finalResponse();
    }

    @POST
    @Path("/updateStorage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStorage(@FormParam("storage") String storageJson) {
        ResponseData<Response> response=new ResponseData<>();
        Storage storage = gson.fromJson(storageJson, Storage.class);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        storage.setTenant(authUser.getTenantUsers().getTenant());
        ItemDao itemdao = new ItemDao();
        itemdao.updateStorage(storage);
        response.succeed("Depo güncellendi.");
        return response.finalResponse();
    }

    @POST
    @Path("/deleteStorage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteStorage(@FormParam("storageId") long storageId) throws CustomException {
        ResponseData<Response> response=new ResponseData<>();
        Storage storage = new Storage();
        storage.setId(storageId);
        User authUser = (User) this.servletRequest.getAttribute("authUser");
        storage.setTenant(authUser.getTenantUsers().getTenant());
        ItemDao itemdao = new ItemDao();
        itemdao.deleteStorage(storage);
        response.succeed("Depo silindi.");
        return response.finalResponse();
    }
}
