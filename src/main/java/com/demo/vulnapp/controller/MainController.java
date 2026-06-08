package com.demo.vulnapp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
public class MainController {

    // Log4Shell: user input logged directly with log4j 2.14.1
    private static final Logger log = LogManager.getLogger(MainController.class);

    @Autowired
    private DataSource dataSource;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "VulnApp - Demo");
        return "index";
    }

    // SQL Injection: raw string concatenation into query
    @GetMapping("/search")
    @ResponseBody
    public String search(@RequestParam String query, HttpServletResponse response) throws Exception {
        log.info("Search query received: {}", query); // Log4Shell trigger point

        StringBuilder result = new StringBuilder("<html><body><h2>Results for: " + query + "</h2><ul>"); // XSS
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // SQLI: unsanitised input directly in query
            ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + query + "'");
            while (rs.next()) {
                result.append("<li>").append(rs.getString("username")).append("</li>");
            }
        }
        result.append("</ul></body></html>");
        response.setContentType("text/html");
        return result.toString();
    }

    // Command Injection: user input passed to Runtime.exec
    @GetMapping("/ping")
    @ResponseBody
    public String ping(@RequestParam String host) throws IOException {
        log.info("Ping host: {}", host);
        // Command injection: no sanitisation
        Process proc = Runtime.getRuntime().exec("ping -c 1 " + host);
        return "Ping executed for: " + host;
    }

    // Text4Shell (CVE-2022-42889): StringSubstitutor with user input
    @GetMapping("/greet")
    @ResponseBody
    public String greet(@RequestParam String name) {
        log.info("Greet: {}", name); // Log4Shell trigger
        // Text4Shell: user-controlled interpolation via commons-text
        StringSubstitutor sub = StringSubstitutor.createInterpolator();
        return sub.replace("Hello, " + name + "!");
    }

    // SSRF: user-controlled URL fetch
    @GetMapping("/fetch")
    @ResponseBody
    public String fetch(@RequestParam String url) throws IOException {
        log.info("Fetching URL: {}", url);
        // SSRF: unvalidated user-supplied URL
        java.net.URL target = new java.net.URL(url);
        java.io.InputStream in = target.openStream();
        return new String(in.readAllBytes());
    }

    // Hardcoded credentials endpoint (for demo discovery)
    @GetMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(defaultValue = "") String password) {
        // Hardcoded secret
        if ("admin123".equals(password)) {
            return "Welcome admin! DB password: s3cr3t_db_pass";
        }
        return "Unauthorized";
    }
}
