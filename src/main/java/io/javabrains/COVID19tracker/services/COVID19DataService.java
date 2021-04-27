package io.javabrains.COVID19tracker.services;

import io.javabrains.COVID19tracker.services.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class COVID19DataService {

    private static String VIRUS_CASES_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    private List<LocationStats> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "* * * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestCases = HttpRequest.newBuilder().uri(URI.create(VIRUS_CASES_URL)).build();

        HttpResponse<String> httpResponseCases = client.send(requestCases, HttpResponse.BodyHandlers.ofString());

        StringReader casesReader = new StringReader(httpResponseCases.body());
        Iterable<CSVRecord> cases = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(casesReader);

        List<LocationStats> newStats = new ArrayList<>();

        // parse csv's
        for (CSVRecord covid_case : cases) {
            LocationStats locationStat = new LocationStats();
            locationStat.setState(covid_case.get("Province/State"));
            locationStat.setCountry(covid_case.get("Country/Region"));
            int latestCases = Integer.parseInt(covid_case.get(covid_case.size() - 1));
            int previousDayCases = Integer.parseInt(covid_case.get(covid_case.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPreviousDay(latestCases - previousDayCases);
            newStats.add(locationStat);
        }
        this.allStats = newStats;

    }

}