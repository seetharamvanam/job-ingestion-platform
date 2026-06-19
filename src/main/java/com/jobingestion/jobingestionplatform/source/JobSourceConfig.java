package com.jobingestion.jobingestionplatform.source;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JobSourceConfig(
        @JsonProperty("companyName")
        String companyName,
        @JsonProperty("careerUrl")
        String careerUrl,
        @JsonProperty("active")
        Boolean active
) {
}
