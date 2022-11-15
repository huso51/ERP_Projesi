/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.dao;

import com.erprest.connection.DBConnectionHelper;
import com.erprest.filter.CustomException;
import com.erprest.model.AccountActivity;
import com.erprest.model.Customer;
import com.erprest.model.CustomerPayment;
import com.erprest.model.PaymentBillInstallment;
import com.erprest.model.PaymentCheckbook;
import com.erprest.model.PaymentCheckbookItem;
import com.erprest.model.SpendingType;
import com.erprest.model.Tenant;
import com.erprest.model.TenantAccount;
import com.erprest.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author msi_ge72
 */
public class AccountDao {

    public void addAccount(TenantAccount account, User authUser) throws CustomException {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into tenant_account(tenant_id,no,name,description) "
                    + "         values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getTenant().getId());
            ps.setString(2, account.getNo());
            ps.setString(3, account.getName());
            ps.setString(4, account.getDescription());
            ps.executeUpdate();
            ResultSet keys1 = ps.getGeneratedKeys();
            keys1.next();
            account.setId(keys1.getInt(1));
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        AccountActivity activity = new AccountActivity();
        activity.setAccount(account);
        activity.setUser(authUser);
        activity.setProcess("Borç Fişi");
        activity.setDescription("Açılış bakiyesi");
        activity.setDebt(account.getAmount());
        addAccountActivity(activity);
    }

    public List<TenantAccount> getTenantAccounts(String tenantId, long accountId, long limit, long offset) {
        List<TenantAccount> accounts = new ArrayList<>();
        String temp = "";
        ArrayList paramList = new ArrayList();
        if (tenantId != null && !tenantId.equals("")) {
            temp += " and tenant_id=?";
            paramList.add(tenantId);
        }
        if (accountId != 0) {
            temp += " and id=?";
            paramList.add(accountId);
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from tenant_account where 1=1 " + temp + " limit " + limit + " offset " + offset);
            for (int i = 0; i < paramList.size(); i++) {
                ps.setObject(i + 1, paramList.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TenantAccount account = new TenantAccount();
                account.setId(rs.getLong("id"));
                account.setNo(rs.getString("no"));
                account.setName(rs.getString("name"));
                account.setDescription(rs.getString("description"));
                account.setAmount(rs.getDouble("amount"));
                account.setCreatedAt(rs.getString("created_at"));
                accounts.add(account);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return accounts;
    }

    public void updateAccount(TenantAccount account, User authUser) throws CustomException {
        List<TenantAccount> existingAccounts = getTenantAccounts(account.getTenant().getId(), account.getId(), 1, 0);
        if (existingAccounts.isEmpty()) {
            throw new CustomException(account.getTenant().getName() + " organizasyonuna ait " + account.getName() + " hesabı bulunamadı.");
        }
        TenantAccount existingAccount = existingAccounts.get(0);
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (account.getNo() != null && !account.getNo().equals("")) {
            whereParameters += ",no=?";
            paramList.add(account.getNo());
        }
        if (account.getName() != null && !account.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(account.getName());
        }
        if (account.getDescription() != null && !account.getDescription().equals("")) {
            whereParameters += ",description=?";
            paramList.add(account.getDescription());
        }
        if (account.getAmount() != 0 && account.getAmount() != existingAccount.getAmount()) {
            AccountActivity activity = new AccountActivity();
            if (account.getAmount() > existingAccount.getAmount()) {
                activity.setDebt(account.getAmount() - existingAccount.getAmount());
            } else {
                activity.setReceivable(existingAccount.getAmount() - account.getAmount());
            }
            activity.setAccount(account);
            activity.setUser(authUser);
            activity.setProcess("Borç Fişi");
            activity.setDescription("Bakiye düzeltme");
            addAccountActivity(activity);
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update tenant_account set id=id" + whereParameters + " where id=?");
            paramList.add(account.getId());
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

    public void deleteAccount(TenantAccount account) throws CustomException {
        List<TenantAccount> existingAccount = getTenantAccounts(account.getTenant().getId(), account.getId(), 1, 0);
        if (existingAccount.isEmpty()) {
            throw new CustomException(account.getTenant().getName() + " organizasyonuna ait " + account.getName() + " hesabı bulunamadı.");
        }
        if (existingAccount.get(0).getAmount() != 0) {
            throw new CustomException("Hesapta " + existingAccount.get(0).getAmount() + " tutarında para bulunduğundan silinemez.");
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("delete from tenant_account where id=?");
            ps.setLong(1, existingAccount.get(0).getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }

    }

    public void addAccountTransfer(User authUser, TenantAccount fromAccount, TenantAccount toAccount, double amount) throws CustomException {
        List<TenantAccount> existingFromAccount = getTenantAccounts(fromAccount.getTenant().getId(), fromAccount.getId(), 1, 0);
        if (existingFromAccount.isEmpty()) {
            throw new CustomException(fromAccount.getTenant().getName() + " organizasyonuna ait " + fromAccount.getName() + " hesabı bulunamadı.");
        }
        List<TenantAccount> existingRoAccount = getTenantAccounts(toAccount.getTenant().getId(), toAccount.getId(), 1, 0);
        if (existingRoAccount.isEmpty()) {
            throw new CustomException(toAccount.getTenant().getName() + " organizasyonuna ait " + toAccount.getName() + " hesabı bulunamadı.");
        }
        if (fromAccount.getAmount() < amount) {
            throw new CustomException("En fazla" + fromAccount.getAmount() + " kadar transfer edebilirsiniz.");
        }
        AccountActivity fromActivity = new AccountActivity();
        fromActivity.setAccount(fromAccount);
        fromActivity.setUser(authUser);
        fromActivity.setProcess("Para Çıkışı");
        fromActivity.setDescription("Hesaba Transfer:" + toAccount.getName());
        fromActivity.setReceivable(amount);
        addAccountActivity(fromActivity);

        AccountActivity toActivity = new AccountActivity();
        toActivity.setAccount(toAccount);
        toActivity.setUser(authUser);
        toActivity.setProcess("Para Girişi");
        toActivity.setDescription("Hesaptan Transfer:" + fromAccount.getName());
        toActivity.setDebt(amount);
        addAccountActivity(toActivity);
    }

    private void addAmountToAccount(TenantAccount account, double amount) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update tenant_account set amount=amount+? where id=?");
            ps.setDouble(1, amount);
            ps.setLong(2, account.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public void addAccountActivity(AccountActivity activity) throws CustomException {
        TenantAccount existingAccount = getTenantAccounts(activity.getAccount().getTenant().getId(), activity.getAccount().getId(), 1, 0).get(0);
        String query = "";
        if (activity.getDebt() != 0) {
            addAmountToAccount(existingAccount, activity.getDebt());
            query = "insert into account_activity (tenant_account_id,user_id,customer_id,process,description,debt,remaining)"
                    + "values (?,?,?,?,?,?,?)";
        } else if (activity.getReceivable() != 0) {
            addAmountToAccount(existingAccount, -activity.getReceivable());
            query = "insert into account_activity (tenant_account_id,user_id,customer_id,process,description,receivable,remaining)"
                    + "values (?,?,?,?,?,?,?)";
        } else {
            throw new CustomException("borç veya alacak alanlarından biri girilmeli.");
        }
        existingAccount = getTenantAccounts(activity.getAccount().getTenant().getId(), activity.getAccount().getId(), 1, 0).get(0);
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement(query);
            ps.setLong(1, existingAccount.getId());
            ps.setString(2, activity.getUser().getId());
            ps.setLong(3, activity.getCustomer() != null ? activity.getCustomer().getId() : 0);
            ps.setString(4, activity.getProcess());
            ps.setString(5, activity.getDescription());
            ps.setDouble(6, activity.getDebt() != 0 ? activity.getDebt() : activity.getReceivable());
            ps.setDouble(7, existingAccount.getAmount());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<AccountActivity> getActivities(long accountId, long limit, long offset) {
        List<AccountActivity> activities = new ArrayList<>();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from account_activity act"
                    + "                     join tenant_account acc on act.tenant_account_id=acc.id"
                    + "                     join users u on act.user_id=u.id"
                    + "                     left join customers c on act.customer_id=c.id"
                    + "                         where act.tenant_account_id=? order by act.created_at desc limit " + limit + " offset " + offset);
            ps.setLong(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AccountActivity activity = new AccountActivity();
                activity.setId(rs.getLong("act.id"));
                activity.setProcess(rs.getString("act.process"));
                activity.setDescription(rs.getString("act.description"));
                activity.setDebt(rs.getDouble("act.debt"));
                activity.setReceivable(rs.getDouble("act.receivable"));
                activity.setRemaining(rs.getDouble("act.remaining"));
                activity.setCreatedAt(rs.getString("act.created_at"));

                User user = new User();
                user.setId(rs.getString("u.id"));
                user.setName(rs.getString("u.name"));
                activity.setUser(user);

                if (rs.getLong("c.id") != 0) {
                    Customer customer = new Customer();
                    customer.setId(rs.getLong("c.id"));
                    customer.setFullAppellation(rs.getString("c.full_appellation"));
                    activity.setCustomer(customer);
                }
                activities.add(activity);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return activities;
    }

    public void addSpendingType(SpendingType spendingType) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("insert into spending_type (tenant_id,name,description)"
                    + "                     values (?,?,?)");
            ps.setString(1, spendingType.getTenant().getId());
            ps.setString(2, spendingType.getName());
            ps.setString(3, spendingType.getDescription());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<SpendingType> getSpendingTypes(String tenantId) {
        List<SpendingType> types = new ArrayList<>();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from spending_type where tenant_id=?");
            ps.setString(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SpendingType type = new SpendingType();
                type.setId(rs.getLong("id"));
                type.setName(rs.getString("name"));
                type.setDescription(rs.getString("description"));
                type.setCreatedAt(rs.getString("created_at"));
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

    public void updateSpendingType(SpendingType type) {
        String whereParameters = "";
        ArrayList paramList = new ArrayList();
        if (type.getName() != null && !type.getName().equals("")) {
            whereParameters += ",name=?";
            paramList.add(type.getName());
        }
        if (type.getDescription() != null) {
            whereParameters += ",description=?";
            paramList.add(type.getDescription());
        }
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update spending_type set id=id" + whereParameters + " where id=? and tenant_id=?");
            paramList.add(type.getId());
            paramList.add(type.getTenant().getId());
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

    public void deleteSpendingType(SpendingType type) {
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("delete from spending_type where tenant_id=? and id=?");
            ps.setString(1, type.getTenant().getId());
            ps.setLong(2, type.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    // ------------------Customer Accounts
    public void addCustomerPayment(CustomerPayment payment, User authUser) throws CustomException {
        CustomerDao customerDao = new CustomerDao();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            conn.setAutoCommit(false);
            if (payment.getType().equals("cash")) {
                ps = conn.prepareStatement("insert into payment_cash (description,is_paying_to_customer,tenant_account_id,amount)"
                        + "                     values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, payment.getPaymentCash().getDescription());
                ps.setBoolean(2, payment.getPaymentCash().isIsPayingToCustomer());
                ps.setLong(3, payment.getPaymentCash().getTenantAccount().getId());
                ps.setDouble(4, payment.getPaymentCash().getAmount());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();// Ekleyemezse hata versin
                payment.getPaymentCash().setId(rs.getInt(1));

                String debtOrReceivable;
                if (payment.getPaymentCash().isIsPayingToCustomer()) {
                    debtOrReceivable = "debt";
                    customerDao.addRemainder(payment.getCustomer(), payment.getPaymentCash().getAmount());
                } else {
                    debtOrReceivable = "receivable";
                    customerDao.addRemainder(payment.getCustomer(), -payment.getPaymentCash().getAmount());
                }
                payment.setCustomer(customerDao.getCustomers(payment.getTenant().getId(), payment.getCustomer().getId(), null, null, 0, null, null, null, null, 1, 0).get(0));
                ps = conn.prepareStatement("insert into customer_payment (tenant_id,customer_id,description,type,payment_cash_id," + debtOrReceivable + ",remaining,is_paid)"
                        + "                     values (?,?,?,?,?,?,?,?)");
                ps.setString(1, payment.getTenant().getId());
                ps.setLong(2, payment.getCustomer().getId());
                ps.setString(3, payment.getPaymentCash().getDescription());
                ps.setString(4, payment.getType());
                ps.setLong(5, payment.getPaymentCash().getId());
                ps.setDouble(6, payment.getPaymentCash().getAmount());
                ps.setDouble(7, payment.getCustomer().getRemainder());
                ps.setBoolean(8, true);
                ps.executeUpdate();

                AccountActivity activity = new AccountActivity();
                activity.setAccount(payment.getPaymentCash().getTenantAccount());
                activity.setUser(authUser);
                activity.getAccount().setTenant(payment.getTenant());
                activity.setCustomer(payment.getCustomer());
                activity.setDescription("Nakit ödeme");
                if (payment.getPaymentCash().isIsPayingToCustomer()) {
                    activity.setReceivable(payment.getPaymentCash().getAmount());
                    activity.setProcess("Ödeme");
                } else {
                    activity.setDebt(payment.getPaymentCash().getAmount());
                    activity.setProcess("Tahsilat");
                }
                addAccountActivity(activity);
            } else if (payment.getType().equals("checkbook")) {
                ps = conn.prepareStatement("insert into payment_checkbook (description)"
                        + "                     values (?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, payment.getPaymentCheckbook().getDescription());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();// Ekleyemezse hata versin
                payment.getPaymentCheckbook().setId(rs.getInt(1));
                String debtOrReceivable = null;
                for (PaymentCheckbookItem item : payment.getPaymentCheckbook().getCheckbookItems()) {
                    ps = conn.prepareStatement("insert into payment_checkbook_item(payment_checkbook_id,amount,is_given_to_customer,is_paid,checkbook_date,payment_date)"
                            + "                     values(?,?,?,?,?,?)");
                    ps.setLong(1, payment.getPaymentCheckbook().getId());
                    ps.setDouble(2, item.getAmount());
                    ps.setBoolean(3, item.isIsGivenToCustomer());
                    ps.setBoolean(4, false);
                    ps.setString(5, item.getCheckbookDate());
                    ps.setString(6, item.getPaymentDate());
                    ps.executeUpdate();

                    if (payment.getPaymentCheckbook().getCheckbookItems().get(0).isIsGivenToCustomer()) {
                        debtOrReceivable = "debt";
                        customerDao.addRemainder(payment.getCustomer(), item.getAmount());
                        payment.setDebt(payment.getDebt() + item.getAmount());
                    } else {
                        debtOrReceivable = "receivable";
                        customerDao.addRemainder(payment.getCustomer(), -item.getAmount());
                        payment.setReceivable(payment.getReceivable() + item.getAmount());
                    }
                }

                payment.setCustomer(customerDao.getCustomers(payment.getTenant().getId(), payment.getCustomer().getId(), null, null, 0, null, null, null, null, 1, 0).get(0));
                ps = conn.prepareStatement("insert into customer_payment (tenant_id,customer_id,description,type,payment_checkbook_id," + debtOrReceivable + ",remaining,is_paid)"
                        + "                     values (?,?,?,?,?,?,?,?)");
                ps.setString(1, payment.getTenant().getId());
                ps.setLong(2, payment.getCustomer().getId());
                ps.setString(3, payment.getDescription());
                ps.setString(4, payment.getType());
                ps.setLong(5, payment.getPaymentCheckbook().getId());
                ps.setDouble(6, payment.getDebt() != 0 ? payment.getDebt() : payment.getReceivable());
                ps.setDouble(7, payment.getCustomer().getRemainder());
                ps.setBoolean(8, false);
                ps.executeUpdate();
            } else if (payment.getType().equals("bill")) {
                ps = conn.prepareStatement("insert into payment_bill (name,description,amount,bill_date,remaining)"
                        + "                 values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, payment.getPaymentBill().getName());
                ps.setString(2, payment.getPaymentBill().getDescription());
                ps.setDouble(3, payment.getPaymentBill().getAmount());
                ps.setDate(4, payment.getPaymentBill().getBillDate());
                ps.setDouble(5, payment.getCustomer().getRemainder());
                ResultSet rs = ps.getGeneratedKeys();
                rs.next();// Ekleyemezse hata versin
                payment.getPaymentBill().setId(rs.getInt(1));

                for (PaymentBillInstallment installment : payment.getPaymentBill().getBillInstallments()) {
                    ps = conn.prepareStatement("insert into payment_bill_installment (payment_bill_id,amount,is_paid,expiry_date)"
                            + "                 values (?,?,?,?)");
                    ps.setLong(1, payment.getPaymentBill().getId());
                    ps.setDouble(2, installment.getAmount());
                    ps.setBoolean(3, false);
                    ps.setDate(4, installment.getExpiryDate());
                    ps.executeUpdate();
                }
                ps = conn.prepareStatement("insert into customer_payment (tenant_id,customer_id,description,type,payment_bill_id,receivable,is_paid)"
                        + "                     values (?,?,?,?,?,?,?)");
                ps.setString(1, payment.getTenant().getId());
                ps.setLong(2, payment.getCustomer().getId());
                ps.setString(3, payment.getDescription());
                ps.setString(4, payment.getType());
                ps.setLong(5, payment.getPaymentBill().getId());
                ps.setDouble(6, payment.getPaymentBill().getAmount());
                ps.setBoolean(7, false);
                ps.executeUpdate();
            } else {
                if (payment.getDebt()!=0) {
                    customerDao.addRemainder(payment.getCustomer(), payment.getDebt() );
                } else {
                    customerDao.addRemainder(payment.getCustomer(), -payment.getReceivable());
                }
                payment.setCustomer(customerDao.getCustomers(payment.getTenant().getId(), payment.getCustomer().getId(), null, null, 0, null, null, null, null, 1, 0).get(0));
                String debtOrReceivable = payment.getDebt() != 0 ? "debt" : "receivable";
                ps = conn.prepareStatement("insert into customer_payment (tenant_id,customer_id,description,type," + debtOrReceivable + ",is_paid, remaining)"
                        + "                     values (?,?,?,?,?,?,?)");
                ps.setString(1, payment.getTenant().getId());
                ps.setLong(2, payment.getCustomer().getId());
                ps.setString(3, payment.getDescription());
                ps.setString(4, payment.getType());
                ps.setDouble(5, payment.getDebt() != 0 ? payment.getDebt() : payment.getReceivable());
                ps.setBoolean(6, false);
                ps.setDouble(7, payment.getCustomer().getRemainder());
                ps.executeUpdate();
            }
            conn.commit();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<CustomerPayment> getCustomerPayments(Tenant authTenant, long customerId, long limit, long offset) {
        List<CustomerPayment> payments = new ArrayList<>();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select p.*,c.full_appellation from customer_payment p \n"
                    + "                     join customers c on c.id=p.customer_id"
                    + "                         where p.tenant_id=? and p.customer_id=? order by p.created_at desc limit "+ limit +" offset "+ offset);
            ps.setString(1, authTenant.getId());
            ps.setLong(2, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CustomerPayment payment = new CustomerPayment();
                payment.setId(rs.getLong("p.id"));
                payment.setCustomer(new Customer());
                payment.getCustomer().setFullAppellation("c.full_appellation");
                payment.setDescription(rs.getString("p.description"));
                payment.setType(rs.getString("p.type"));
                payment.setDebt(rs.getDouble("p.debt"));
                payment.setReceivable(rs.getDouble("p.receivable"));
                payment.setRemaining(rs.getDouble("p.remaining"));
                payment.setIsPaid(rs.getBoolean("p.is_paid"));
                payment.setCreatedAt(rs.getString("p.created_at"));
                payments.add(payment);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return payments;
    }

    public List<PaymentCheckbookItem> getCheckBooks(Tenant authTenant) {
        List<PaymentCheckbookItem> checkbooks = new ArrayList<>();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from payment_checkbook_item ci\n"
                    + "		left join customer_payment cp on cp.payment_checkbook_id=ci.payment_checkbook_id\n"
                    + "			where cp.tenant_id=? order by cp.created_at desc");
            ps.setString(1, authTenant.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PaymentCheckbookItem check = new PaymentCheckbookItem();
                check.setId(rs.getLong("ci.id"));
                check.setAmount(rs.getDouble("ci.amount"));
                check.setIsGivenToCustomer(rs.getBoolean("ci.is_given_to_customer"));
                check.setIsPaid(rs.getBoolean("ci.is_paid"));
                check.setCheckbookDate(rs.getString("ci.checkbook_date"));
                check.setPaymentDate(rs.getString("ci.payment_date"));
                checkbooks.add(check);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return checkbooks;
    }

    public void payCheckbook(User authUser, PaymentCheckbookItem checkbookItem) throws CustomException {
        AccountActivity activity = new AccountActivity();
        activity.setAccount(checkbookItem.getTenantAccount());
        activity.setUser(authUser);
        //accountun tenantı set edilebilir, customer ı set edilebilir.
        activity.setDescription("Çek");
        if (checkbookItem.isIsGivenToCustomer()) {
            activity.setReceivable(checkbookItem.getAmount());
            activity.setProcess("Ödeme");
        } else {
            activity.setDebt(checkbookItem.getAmount());
            activity.setProcess("Tahsilat");
        }
        addAccountActivity(activity);
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update payment_checkbook_item set is_paid=true,tenant_account_id=? where id=?");
            ps.setLong(1, checkbookItem.getTenantAccount().getId());
            ps.setLong(2, checkbookItem.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }

    public List<PaymentBillInstallment> getBillInstallments(Tenant authTenant) {
        List<PaymentBillInstallment> installments = new ArrayList<>();
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("select * from payment_bill_installment pi\n"
                    + "	left join customer_payment cp on cp.payment_bill_id=pi.payment_bill_id\n"
                    + "		where cp.tenant_id=? order by cp.created_at desc");
            ps.setString(1, authTenant.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PaymentBillInstallment installment = new PaymentBillInstallment();
                installment.setId(rs.getLong("pi.id"));
                installment.setAmount(rs.getDouble("pi.amount"));
                installment.setIsPaid(rs.getBoolean("pi.is_paid"));
                installment.setExpiryDate(rs.getDate("pi.expiry_date"));
                installments.add(installment);
            }
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
        return installments;
    }

    public void payInstallment(User authUser, PaymentBillInstallment installment) throws CustomException {
        AccountActivity activity = new AccountActivity();
        activity.setAccount(installment.getTenantAccount());
        activity.setUser(authUser);
        activity.setDescription("Senet");
        activity.setReceivable(installment.getAmount());
        activity.setProcess("Ödeme");
        addAccountActivity(activity);
        DBConnectionHelper dbhelper = DBConnectionHelper.getInstance();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbhelper.getConnection();
            ps = conn.prepareStatement("update payment_bill_installment set is_paid=true, tenant_account_id=? where id=?");
            ps.setLong(1, installment.getTenantAccount().getId());
            ps.setLong(2, installment.getId());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            dbhelper.closeConnection(conn);
        }
    }
}
