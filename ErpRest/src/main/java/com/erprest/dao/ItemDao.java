/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.filter.CustomException;
import com.erprest.model.Category;
import com.erprest.model.Currency;
import com.erprest.model.Item;
import com.erprest.model.ItemTaxes;
import com.erprest.model.Storage;
import com.erprest.model.TaxesType;
import com.erprest.model.UomCode;
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
public class ItemDao {

    public void addCategory(Category category) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into categories (tenant_id,name,description) \n"
                    + "                 	values(?,?,?)");
            ps.setString(1, category.getTenantId());
            ps.setString(2, category.getName());
            ps.setString(3, category.getDescription());
            ps.executeUpdate();

            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<Category> getCategories(Category categoryUser, Storage storage) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        String whereParameter = "", temp = "";
        ArrayList paramList = new ArrayList();

        temp += " and c.tenant_id=?";
        paramList.add(categoryUser.getTenantId());
        if (categoryUser.getId() != 0) {
            temp += " and c.id=?";
            paramList.add(categoryUser.getId());
        }
        if (storage.getId() != 0) {
            temp += " and s.id=?";
            paramList.add(storage.getId());
        }
        whereParameter = temp;
        List<Category> categories = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select distinct c.* from items i\n"
                    + "		right join categories c on i.category_id=c.id\n"
                    + "        left join storage s on s.id=i.storage_id\n"
                    + "where 1=1 " + whereParameter);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getLong("id"));
                category.setTenantId(rs.getString("tenant_id"));
                category.setCode(rs.getString("code"));
                category.setName(rs.getString("name"));
                category.setDescription(rs.getString("description"));

                categories.add(category);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return categories;

    }

    public void deleteCategory(Category category) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("delete from categories where tenant_id=? and id=?");
            ps.setString(1, category.getTenantId());
            ps.setLong(2, category.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void updateCategory(Category category) {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (category.getName() != null && !category.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(category.getName());
        }
        if (category.getDescription() != null) {
            whereParameters += ",description=?";
            paramList.add(category.getDescription());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update categories set id=id" + whereParameters + " where id=? and tenant_id=?");
            paramList.add(category.getId());
            paramList.add(category.getTenantId());
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

    public List<Item> getItems(Item itemparam, String whereDateAfter, String whereDateBefore, String order_by, long limit, long offset) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<Item> items = new ArrayList<>();
        String whereParameter = "", temp = "";
        ArrayList paramList = new ArrayList();

        temp += " and i.tenant_id=?";
        paramList.add(itemparam.getTenantId());
        if (itemparam.getName() != null && !itemparam.getName().equals("")) {
            temp += " and i.name like ?";
            paramList.add("%" + itemparam.getName() + "%");
        }
        if (itemparam.getId() != 0) {
            temp += " and i.id=?";
            paramList.add(itemparam.getId());
        }
        if (itemparam.getCategoryId() != 0) {
            temp += " and i.category_id=?";
            paramList.add(itemparam.getCategoryId());
        }
        if (whereDateAfter != null && !whereDateAfter.equals("")) {
            temp += " and i.created_at > ?";
            paramList.add(whereDateAfter);
        }
        if (whereDateBefore != null && !whereDateBefore.equals("")) {
            temp += " and i.created_at < ?";
            paramList.add(whereDateBefore);
        }
        if (itemparam.getStorage() != null && itemparam.getStorage().getId() != -1) {
            temp += " and i.storage_id=?";
            paramList.add(itemparam.getStorage().getId());
        }
        if (order_by == null) {
            order_by = "";
        } else {
            order_by = " order by " + order_by;
        }
        whereParameter = temp;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select i.id,i.tenant_id,i.name,i.description,i.category_id,i.storage_id,i.uom_id,i.stock,i.price,i.currency_id,i.code,i.created_at,\n"
                    + "	c.id as c_id,c.tenant_id as c_tenant_id,c.code as c_code,c.name as c_name,c.description as c_description,\n"
                    + " s.id as s_id,s.tenant_id as s_tenant_id,s.name as s_name,s.description as s_description,s.address as s_address,s.created_at as s_created_at,\n"
                    + "	u.id as u_id,u.code as u_code,u.description as u_description,\n"
                    + " cu.id as cu_id,cu.code as cu_code,cu.description as cu_description\n"
                    + "		from items i\n"
                    + "			left join categories c on c.id=i.category_id\n"
                    + "                 left join storage s on s.id=i.storage_id\n"
                    + "			join uom_codes u on u.id=i.uom_id\n"
                    + "			join currency cu on cu.id=i.currency_id\n"
                    + "                     where 1=1  \n"
                    + whereParameter + order_by + " limit " + limit + " offset " + offset);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Item item = new Item();
                item.setId(rs.getLong("id"));
                item.setTenantId(rs.getString("tenant_id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setCategoryId(rs.getLong("category_id"));
                item.setPrice(rs.getDouble("price"));
                item.setStock(rs.getLong("stock"));
                item.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("created_at")));

                Category category = new Category();
                category.setId(rs.getLong("c_id"));
                category.setTenantId(rs.getString("c_tenant_id"));
                category.setCode(rs.getString("c_code"));
                category.setName(rs.getString("c_name"));
                category.setDescription(rs.getString("c_description"));
                item.setCategory(category);

                Storage storage = new Storage();
                storage.setId(rs.getLong("s_id"));
                storage.setName(rs.getString("s_name"));
                storage.setDescription(rs.getString("s_description"));
                storage.setAddress(rs.getString("s_address"));
                storage.setCreatedAt(rs.getTimestamp("s_created_at"));
                item.setStorage(storage);

                UomCode uom = new UomCode();
                uom.setId(rs.getLong("u_id"));
                uom.setCode(rs.getString("u_code"));
                uom.setDescription(rs.getString("u_description"));
                item.setUomCode(uom);

                Currency currency = new Currency();
                currency.setId(rs.getLong("cu_id"));
                currency.setCode(rs.getString("cu_code"));
                currency.setDescription(rs.getString("cu_description"));
                item.setCurrency(currency);

                PreparedStatement ps2 = conn.prepareStatement("select it.id as it_id,it.item_id as it_item_id,it.value as it_value,it.taxes_type_id as it_taxes_type_id,\n"
                        + "                     ty.id as ty_id,ty.code as ty_code,ty.description as ty_description from item_taxes it\n"
                        + "join taxes_type ty on ty.id=it.taxes_type_id\n"
                        + "where item_id=?");
                ps2.setLong(1, item.getId());
                ResultSet rs2 = ps2.executeQuery();
                List<ItemTaxes> itemTaxess = new ArrayList<>();
                while (rs2.next()) {
                    ItemTaxes itemtexas = new ItemTaxes();
                    itemtexas.setId(rs2.getLong("it_id"));
                    itemtexas.setItemId(rs2.getLong("it_item_id"));
                    itemtexas.setValue(rs2.getLong("it_value"));
                    itemtexas.setTaxesTypeId(rs2.getLong("it_taxes_type_id"));

                    TaxesType ty = new TaxesType();
                    ty.setId(rs2.getLong("ty_id"));
                    ty.setCode(rs2.getString("ty_code"));
                    ty.setDescription(rs2.getString("ty_description"));
                    itemtexas.setTaxesType(ty);
                    itemTaxess.add(itemtexas);
                }
                item.setItemTaxes(itemTaxess);
                items.add(item);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return items;
    }

    public List<UomCode> getUomCodes() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<UomCode> uomCodes = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from uom_codes");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UomCode uomcode = new UomCode();
                uomcode.setId(rs.getLong("id"));
                uomcode.setCode(rs.getString("code"));
                uomcode.setDescription(rs.getString("description"));
                uomCodes.add(uomcode);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return uomCodes;
    }

    public List<Currency> getCurrencies() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<Currency> currencies = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from currency");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Currency currency = new Currency();
                currency.setId(rs.getLong("id"));
                currency.setCode(rs.getString("code"));
                currency.setDescription(rs.getString("description"));
                currencies.add(currency);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return currencies;
    }

    public List<TaxesType> getTaxesType() {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        List<TaxesType> types = new ArrayList<>();
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from taxes_type");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TaxesType type = new TaxesType();
                type.setId(rs.getLong("id"));
                type.setCode(rs.getString("code"));
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

    public void addItem(Item item) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into items (tenant_id,category_id,storage_id,name,description,uom_id,stock,price,currency_id,code)\n"
                    + "	values(?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, item.getTenantId());
            ps.setLong(2, item.getCategory().getId());
            ps.setLong(3, item.getStorage().getId());
            ps.setString(4, item.getName());
            ps.setString(5, item.getDescription());
            ps.setLong(6, item.getUomCode().getId());
            ps.setDouble(7, item.getStock());
            ps.setDouble(8, item.getPrice());
            ps.setLong(9, item.getCurrency().getId());
            ps.setString(10, item.getCode());
            ps.executeUpdate();

            ResultSet keys1 = ps.getGeneratedKeys();
            keys1.next();
            item.setId(keys1.getInt(1));

            for (int i = 0; i < item.getItemTaxes().size(); i++) {
                ps = conn.prepareStatement("insert into item_taxes (item_id,value,taxes_type_id)"
                        + "                 values(?,?,?) ");
                ps.setLong(1, item.getId());
                ps.setLong(2, item.getItemTaxes().get(i).getValue());
                ps.setLong(3, item.getItemTaxes().get(i).getTaxesType().getId());
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void updateItem(Item item) throws CustomException {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (item.getName() != null && !item.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(item.getName());
        }
        if (item.getCategoryId() != 0) {
            whereParameters += ",category_id=?";
            paramList.add(item.getCategory().getId());
        }
        if (item.getDescription() != null) {
            whereParameters += ",description=?";
            paramList.add(item.getDescription());
        }
        if (item.getUomCode().getId() != 0) {
            whereParameters += ",uom_id=?";
            paramList.add(item.getUomCode().getId());
        }
        if (item.getStock() != -1) {
            whereParameters += ",stock=?";
            paramList.add(item.getStock());
        }
        if (item.getPrice() != 0) {
            whereParameters += ",price=?";
            paramList.add(item.getPrice());
        }
        if (item.getCurrencyId() != 0) {
            whereParameters += ",currency_id=?";
            paramList.add(item.getCategoryId());
        }
        if (item.getCode() != null) {
            whereParameters += ",code=?";
            paramList.add(item.getCode());
        }
        if (item.getStorage() != null) {
            whereParameters += ",storage_id=?";
            paramList.add(item.getStorage().getId());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from items where id=? and tenant_id=?");
            ps.setLong(1, item.getId());
            ps.setString(2, item.getTenantId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CustomException("Organizasyonunuzun bu ürünü güncellemek için yetkiniz yoktur.");
            }
            ps = conn.prepareStatement("update items set id=id" + whereParameters + " where id=?");
            paramList.add(item.getId());
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ps.executeUpdate();
            if (item.getItemTaxes().size() > 0) {
                for (int i = 0; i < item.getItemTaxes().size(); i++) {
                    ps = conn.prepareStatement("INSERT INTO item_taxes (item_id,value,taxes_type_id) VALUES (?,?,?)\n"
                            + "  ON DUPLICATE KEY UPDATE value=Values(value);");
                    ps.setLong(1, item.getId());
                    ps.setLong(2, item.getItemTaxes().get(i).getValue());
                    ps.setLong(3, item.getItemTaxes().get(i).getTaxesType().getId());
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

    public void deleteItemTaxes(long itemTaxesId) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("delete from item_taxes where id=?");
            ps.setLong(1, itemTaxesId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void deleteItem(List<Item> items) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            String psParams = "";
            for (int i = 0; i < items.size(); i++) {
                psParams += "?,";
            }
            psParams = psParams.substring(0, psParams.length() - 1);//delete last ","
            ps = conn.prepareStatement("delete from items where id in (" + psParams + ") and tenant_id=?");
            for (int i = 0; i < items.size(); i++) {
                ps.setLong(i + 1, items.get(i).getId());
            }
            ps.setString(items.size() + 1, items.get(0).getTenantId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<Storage> getStorage(Storage storage) {
        List<Storage> storages = new ArrayList<>();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        String storageIdParam = "";
        if (storage.getId() != 0) {
            storageIdParam += " and id=?";
        }
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from storage where tenant_id=? " + storageIdParam);
            ps.setString(1, storage.getTenant().getId());
            if (!storageIdParam.equals("")) {
                ps.setLong(2, storage.getId());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Storage storageResult = new Storage();
                storageResult.setId(rs.getLong("id"));
                storageResult.setName(rs.getString("name"));
                storageResult.setDescription(rs.getString("description"));
                storageResult.setAddress(rs.getString("address"));
                storageResult.setCreatedAt(rs.getTimestamp("created_at"));
                storages.add(storageResult);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return storages;
    }

    public void addStorage(Storage storage) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into storage(tenant_id,name,description,address) \n"
                    + "		values (?,?,?,?)");
            ps.setString(1, storage.getTenant().getId());
            ps.setString(2, storage.getName());
            ps.setString(3, storage.getDescription());
            ps.setString(4, storage.getAddress());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void updateStorage(Storage storage) {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (storage.getName() != null && !storage.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(storage.getName());
        }
        if (storage.getDescription() != null) {
            whereParameters += ",description=?";
            paramList.add(storage.getDescription());
        }
        if (storage.getAddress() != null) {
            whereParameters += ",address=?";
            paramList.add(storage.getAddress());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update storage set id=id" + whereParameters + " where id=? and tenant_id=?");
            paramList.add(storage.getId());
            paramList.add(storage.getTenant().getId());
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

    public void deleteStorage(Storage storage) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        Item item = new Item();
        item.setStorage(storage);
        item.setTenantId(storage.getTenant().getId());
        List<Item> items = getItems(item, null, null, null, 1, 0);
        if (!items.isEmpty()) {
            throw new CustomException("önce depodaki ürünlerin silinmesi gerekir.");
        }
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("delete from storage where id=? and tenant_id=?");
            ps.setLong(1, storage.getId());
            ps.setString(2, storage.getTenant().getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void addStock(long itemId, double amount) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn=dbhelper.getConnection();
            ps=conn.prepareStatement("update items set stock=stock+? where id=?");
            ps.setDouble(1, amount);
            ps.setLong(2, itemId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }
}
