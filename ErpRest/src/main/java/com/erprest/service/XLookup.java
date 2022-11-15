/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

/**
 *
 * @author msi_ge72
 */
import org.w3c.dom.*;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class XLookup {

    private final Document document;
    private Map<String, String> nsMap = new HashMap<String, String>();
    private XPath xPath;

    public XLookup(Document document) {
        this.document = document;
    }

    public void setNamespace(String prefix, String url) {
        nsMap.put(prefix, url);
    }

    private void initXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        xPath = factory.newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return nsMap.get(prefix);
            }

            @Override
            public String getPrefix(String namespaceURI) {
                for (Map.Entry<String, String> entry : nsMap.entrySet()) {
                    if (entry.getValue().equals(namespaceURI)) {
                        return entry.getKey();
                    }
                }
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return nsMap.keySet().iterator();
            }
        });
    }

    public Node getNode(String nodeEvaluationString) {
        if (xPath == null) {
            initXPath();
        }
        try {
            Node node = (Node) xPath.evaluate(nodeEvaluationString, document.getDocumentElement(), XPathConstants.NODE);
            return node;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XLookup.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public NodeList getNodeList(String nodeEvaluationString) {
        if (xPath == null) {
            initXPath();
        }
        try {
            NodeList nodeList = (NodeList) xPath.evaluate(nodeEvaluationString, document.getDocumentElement(), XPathConstants.NODESET);
            return nodeList;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(XLookup.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
