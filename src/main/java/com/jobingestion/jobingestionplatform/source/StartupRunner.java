package com.jobingestion.jobingestionplatform.source;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final JobSourceLoader jobSourceLoader;
    public StartupRunner(JobSourceLoader jobSourceLoader) {
        this.jobSourceLoader = jobSourceLoader;
    }

    @Override
    public void run(String... args){
        jobSourceLoader.loadAndSave();
    }
}
