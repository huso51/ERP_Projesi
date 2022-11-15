/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erprest.service;

import com.erprest.connection.ProjectConfig;

/**
 *
 * @author msi_ge72
 */
public class InvoiceXslt {

    public String getInvoiceXslt(String scenarioCode, String companyLogo, String companySignature) {
        String xsltString;
        if (scenarioCode != null && scenarioCode.equals("TEMELIRSALIYE")) {
            xsltString = ProjectConfig.getInstance().getDespatchXsl();
        } else {
            xsltString = ProjectConfig.getInstance().getXslt();
        }
        xsltString = xsltString.replace("{{title}}", (scenarioCode != null && scenarioCode.equals("EARSIVFATURA")) ? "e-Arşiv" : "e-Fatura");
        if (companyLogo != null) {
            xsltString = xsltString.replace("{{companyLogo}}", "<img style=\"max-width:200px;max-height:100px;\" align=\"middle\" alt=\"Company Logo\"\n"
                    + "                                         src=\"data:image/jpeg;base64," + companyLogo + "\"/>");
        } else {
            xsltString = xsltString.replace("{{companyLogo}}", "");
        }

        if (companySignature != null && scenarioCode != null && scenarioCode.equals("EARSIVFATURA")) {
            xsltString = xsltString.replace("{{companySignature}}", "<img style=\"max-width:65px;max-height:50px;\" align=\"middle\" alt=\"Company Sign\"\n"
                    + "                                         src=\"data:image/jpeg;base64," + companySignature + "\"/>");
        } else {
            xsltString = xsltString.replace("{{companySignature}}", "");
        }
        return xsltString;
    }

}
