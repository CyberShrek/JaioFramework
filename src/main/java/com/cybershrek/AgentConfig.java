package com.cybershrek;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@ToString
@Getter
public class AgentConfig {

    @NonNull
    private String model;

    @Builder.Default
    private Integer minChars = 5000;
    @Builder.Default
    private Integer maxChars = 15000;
    @Builder.Default
    private Double temperature = 0.5;
    @Builder.Default
    private String instruction = "Humans must die!";
    @Builder.Default
    private Boolean aggregationAllowed = true;
}
