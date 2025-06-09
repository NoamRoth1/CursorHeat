package org.cursorheat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardRedirectController {
    @GetMapping("/api/v1/dashboard.html")
    public String redirectDashboard() {
        return "redirect:/dashboard.html";
    }
} 