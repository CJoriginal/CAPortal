package uk.ac.ngs.controllers;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

    // private static final Logger log =
    // LoggerFactory.getLogger(HomeController.class);
    private static final Log log = LogFactory.getLog(HomeController.class);

    /**
     * Selects the home view to render by returning its name.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Locale locale, Model model) {
        log.debug("Welcome home info! the client locale is "+ locale.toString());
        
        Date date = new Date();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, locale);
        String formattedDate = dateFormat.format(date);
        model.addAttribute("serverTime", formattedDate);
        return "home";
    }

    /**
     * Simply selects the /cert_owner/home view to render by returning its name.
     */
    // @RequestMapping(value = "/cert_owner/", method = RequestMethod.GET)
    // public String certOwnerHome(Locale locale, Model model) {
    // log.info("Controller /cert_owner/");
    // return "/cert_owner/home";
    // }

}
