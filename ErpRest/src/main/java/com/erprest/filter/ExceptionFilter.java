/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.filter;

import com.erprest.model.ResponseData;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;

/**
 *
 * @author msi_ge72
 */
@Provider
public class ExceptionFilter implements ExtendedExceptionMapper<Exception> {

    private final static Logger logger = Logger.getLogger(ExceptionFilter.class.getName());

    @Override
    public boolean isMappable(Exception t) {
        //return (t instanceof WebApplicationException);
        return !(t instanceof NotFoundException
                || t instanceof NotAllowedException
                || t instanceof NotSupportedException
                || t instanceof IllegalStateException);
    }

    @Override
    public Response toResponse(Exception exception) {
        logger.log(Level.SEVERE, null, exception);
        ResponseData<Response> response = new ResponseData<>();
        if (exception instanceof CustomException) {
            response.setError(406, exception.getMessage());
        } else {
            response.setError(500, "JAVA ERP: Beklenmedik sunucu hatası oluştu");
        }
        /*if (exception != null && exception.getMessage() != null && exception.getMessage().contains("Communications link failure")) {
            response.setError(500, "databaseConnectionError");
            DBConnectionHelper.getInstance().connectNextDatabase();
        }*/
        return response.finalResponse();
    }

}
