package com.jobingestion.jobingestionplatform.provider;

import com.jobingestion.jobingestionplatform.source.JobBoardProviderType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JobBoardProviderFactory {

    /*JobBoardProviderFactory is responsible for resolving the correct
    JobBoardProvider implementation for a given JobBoardProviderType.*/

    private final Map<JobBoardProviderType, JobBoardProvider> providers;

    public JobBoardProviderFactory(List<JobBoardProvider> jobBoardProvidersList) {
        providers = new HashMap<>();
        for (JobBoardProvider provider : jobBoardProvidersList) {
            if (providers.containsKey(provider.getProviderType())) {
                throw new IllegalStateException("Duplicate job board provider type " + provider.getProviderType());
            }
            providers.put(provider.getProviderType(), provider);
        }
    }

    public JobBoardProvider getProvider(JobBoardProviderType providerType) {
        JobBoardProvider provider = providers.get(providerType);
        if (provider == null) {
            throw new IllegalStateException("Provider " + providerType + " not found");
        }
        return provider;
    }

}
