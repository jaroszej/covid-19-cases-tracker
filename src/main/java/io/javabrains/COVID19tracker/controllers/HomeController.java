package io.javabrains.COVID19tracker.controllers;

import io.javabrains.COVID19tracker.services.COVID19DataService;
import io.javabrains.COVID19tracker.services.models.LocationStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {

    @Autowired
    COVID19DataService dataService;

    @GetMapping("/")
    public String home(Model model) {
        List<LocationStats> allStats = dataService.getAllStats();
        int totalCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPreviousDay()).sum();

        String formattedTotalCases = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalCases);
        String formattedNewCases = NumberFormat.getNumberInstance(Locale.getDefault()).format(totalNewCases);

        model.addAttribute("allStats", allStats);
        model.addAttribute("totalReportedCases", "Total cases: " + formattedTotalCases);
        model.addAttribute("totalNewCases", "Total new cases: " + formattedNewCases);
        return "home";
    }
}