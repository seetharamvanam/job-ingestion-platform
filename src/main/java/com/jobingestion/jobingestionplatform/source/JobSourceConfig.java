package com.jobingestion.jobingestionplatform.source;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.antlr.v4.runtime.misc.NotNull;

public record JobSourceConfig(
        @NotNull
        String companyName,
        @NotNull
        String careerUrl,
        @NotNull
        Boolean active
) {
}
