package com.prism.shield.entities;

import java.util.List;

public record PrivacyResponse(List<String> foundPII, String status, String redactedText) {

        public PrivacyResponse {
            // 1. Force Immutability: Ensure the list cannot be changed later
            foundPII = (foundPII == null) ? List.of() : List.copyOf(foundPII);

            // 2. Default Status: Handle cases where the AI output is messy
            if (status == null || status.isBlank()) {
                status = foundPII.isEmpty() ? "SAFE" : "FLAGGED";
            }
        }
    }