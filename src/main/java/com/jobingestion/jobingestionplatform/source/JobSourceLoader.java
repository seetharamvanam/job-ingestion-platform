package com.jobingestion.jobingestionplatform.source;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JobSourceLoader {

    private final ObjectMapper objectMapper;
    private final JobSourceRepository jobSourceRepository;
    private final Resource jsonResource;

    public JobSourceLoader(
            ObjectMapper objectMapper,
            JobSourceRepository jobSourceRepository,
            @Value("classpath:job-sources.json") Resource jsonResource) {
        this.objectMapper = objectMapper;
        this.jobSourceRepository = jobSourceRepository;
        this.jsonResource = jsonResource;
    }

    @Transactional
    public List<JobSource> loadAndSave() {
        try (InputStream inputStream = jsonResource.getInputStream()) {
            List<JobSourceConfig> dtos = objectMapper.readValue(inputStream, new TypeReference<List<JobSourceConfig>>() {});

            List<JobSource> entities = dtos.stream()
                    .filter(dto -> jobSourceRepository.findByCareerUrl(dto.careerUrl()).isEmpty())
                    .map(
                            dto -> JobSource.builder()
                                    .companyName(dto.companyName())
                                    .careerUrl(dto.careerUrl())
                                    .activeStatus(dto.active())
                                    .build()).collect(Collectors.toList());

            return jobSourceRepository.saveAll(entities);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load and save JSON data", e);
        }
    }
}
