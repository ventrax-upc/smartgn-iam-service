package com.smartgn.iam.subscription.domain.model;

import com.smartgn.iam.auth.domain.model.Plan;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Suscripcion(UUID accountId, Plan plan) {

    public boolean permite(Feature feature) {
        return feature.habilitadaPara(plan);
    }

    public Set<Feature> funcionalidades() {
        return Stream.of(Feature.values())
                .filter(this::permite)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Feature.class)));
    }
}
