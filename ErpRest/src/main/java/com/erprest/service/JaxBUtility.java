/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.connection.ProjectConfig;
import com.erprest.filter.NamespaceMapper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import tr.gov.efatura.xjc.DespatchAdviceType;

/**
 *
 * @author msi_ge72
 */
public class JaxBUtility<T> {

    private static JaxBUtility despatchUtility;
    //private static JaxBUtility invoiceUtility;
    private static final Logger logger = Logger.getLogger(JaxBUtility.class.getName());
    JAXBContext jContent;
    private T instanceType;
    private QName qName;
    File sampleFile;

    private JaxBUtility() {
    }

    public static JaxBUtility getInstance(Class type) {
        try {
            if (type.newInstance() instanceof DespatchAdviceType) {
                if (despatchUtility == null) {
                    despatchUtility = new JaxBUtility(DespatchAdviceType.class);
                }
                return despatchUtility;
            }
        } catch (IllegalAccessException | InstantiationException e) {
            logger.log(Level.SEVERE,null,e);
        }
        return null;
    }

    private JaxBUtility(T instanceType) {
        this.instanceType = instanceType;
        try {
            if (instanceType instanceof DespatchAdviceType) {
                jContent = JAXBContext.newInstance((Class<T>) instanceType);
                sampleFile = ProjectConfig.getInstance().getDespatchExample();
                qName = new QName("urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2", "DespatchAdvice");
            } else {
                jContent = JAXBContext.newInstance((Class<T>) instanceType);
                sampleFile = ProjectConfig.getInstance().getDespatchExample();
                qName = new QName("urn:oasis:names:specification:ubl:schema:xsd:DespatchAdvice-2", "DespatchAdvice");
            }
        } catch (JAXBException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public Object unmarshallSample() {
        Object jaxBObject;
        try {
            Unmarshaller unmarshaller = jContent.createUnmarshaller();
            JAXBElement<Object> jxbElem = (JAXBElement<Object>) unmarshaller.unmarshal(sampleFile);
            jaxBObject = JAXBIntrospector.getValue(jxbElem);
        } catch (JAXBException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        return jaxBObject;
    }

    public String marshallSample(Object jaxBObject) {
        String JaxBXml;
        try {
            JAXBElement<Object> root = new JAXBElement<>(qName, Object.class, jaxBObject);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Marshaller marshaller = jContent.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespaceMapper<>(instanceType));
            marshaller.marshal(root, bos);

            JaxBXml = new String(bos.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException | JAXBException e) {
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
        return JaxBXml;
    }
}
