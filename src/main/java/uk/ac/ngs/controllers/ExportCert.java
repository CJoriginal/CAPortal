/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.ngs.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ngs.dao.JdbcCrrDao;
import uk.ac.ngs.dao.JdbcRequestDao;
import uk.ac.ngs.domain.CrrRow;
import uk.ac.ngs.domain.RequestRow;
import uk.ac.ngs.security.CaUser;
import uk.ac.ngs.security.SecurityContextService;
import uk.ac.ngs.service.CertUtil;

/**
 *
 * @author jza23618
 */
@Controller
@RequestMapping("/caop/exportcert")
public class ExportCert {
    private static final Log log = LogFactory.getLog(ExportCert.class);
    private JdbcRequestDao jdbcRequestDao;
    private SecurityContextService securityContextService;
    private JdbcCrrDao jdbcCrrDao;
    
    public ExportCert() {
    }
    
    @ModelAttribute
    public void populateModel(Model model) {
        log.debug("caop export populate model");
        
        CaUser caUser = securityContextService.getCaUserDetails();
        // Extract the RA value from the user's certificate DN
        String dn = caUser.getCertificateRow().getDn();
        String OU = CertUtil.extractDnAttribute(dn, CertUtil.DNAttributeType.OU);
        String L = CertUtil.extractDnAttribute(dn, CertUtil.DNAttributeType.L);
        String CN = CertUtil.extractDnAttribute(dn, CertUtil.DNAttributeType.CN);
        String ra = OU + " " + L;
        model.addAttribute("ra", ra);
        log.debug("ra is:[" + ra + "]");
        
        //fetch list of approved certificates
        Map<JdbcRequestDao.WHERE_PARAMS, String> whereParams = new HashMap<JdbcRequestDao.WHERE_PARAMS, String>();
        whereParams.put(JdbcRequestDao.WHERE_PARAMS.RA_EQ, ra);
        whereParams.put(JdbcRequestDao.WHERE_PARAMS.STATUS_EQ, "APPROVED");
        List<RequestRow> approvedRequestRows = jdbcRequestDao.findBy(whereParams, null, null);
        approvedRequestRows = jdbcRequestDao.setDataNotBefore(approvedRequestRows);
        model.addAttribute("approved_reqrows", approvedRequestRows);
        
        // Fetch a list of approved CRRs 
        Map<JdbcCrrDao.WHERE_PARAMS, String> crrWhereParams = new HashMap<JdbcCrrDao.WHERE_PARAMS, String>();
        crrWhereParams.put(JdbcCrrDao.WHERE_PARAMS.STATUS_EQ, "APPROVED"); 
        crrWhereParams.put(JdbcCrrDao.WHERE_PARAMS.DN_LIKE, "%L="+L+",OU="+OU+"%"); 
        List<CrrRow> crrRows = jdbcCrrDao.findBy(crrWhereParams, null, null);
        log.debug("crrRows size: ["+crrRows.size()+"]"); 
        crrRows = jdbcCrrDao.setSubmitDateFromData(crrRows); 
        model.addAttribute("crr_reqrows", crrRows);
        
        model.addAttribute("lastPageRefreshDate", new Date()); 
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String raAdminHome(Locale locale, Model model) {
        log.debug("Controller /caop/exportcert");
        return "caop/exportcert";
    }
    
    @Inject
    public void setSecurityContextService(SecurityContextService securityContextService) {
        this.securityContextService = securityContextService;
    }
    
    @Inject
    public void setJdbcRequestDao(JdbcRequestDao jdbcRequestDao) {
        this.jdbcRequestDao = jdbcRequestDao;
    }
    
    @Inject
    public void setJdbcCrrDao(JdbcCrrDao jdbcCrrDao) {
        this.jdbcCrrDao = jdbcCrrDao;
    }
}
