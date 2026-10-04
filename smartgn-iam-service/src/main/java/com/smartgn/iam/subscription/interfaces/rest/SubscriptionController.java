package com.smartgn.iam.subscription.interfaces.rest;

import com.smartgn.iam.auth.application.port.out.TokenClaims;
import com.smartgn.iam.subscription.application.port.in.GetSubscriptionUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions", description = "Account plan and enabled features")
public class SubscriptionController {

    private final GetSubscriptionUseCase subscriptions;

    public SubscriptionController(GetSubscriptionUseCase subscriptions) {
        this.subscriptions = subscriptions;
    }

    @GetMapping("/me")
    @Operation(summary = "Get my subscription",
            description = "Returns the current plan (FREE or PRO) and the features it enables. "
                    + "Core metrics and alerts are available on both plans.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token"),
            @ApiResponse(responseCode = "404", description = "The account no longer exists")
    })
    public ResponseEntity<SubscriptionResource> getMine(@Parameter(hidden = true) @AuthenticationPrincipal TokenClaims me) {
        return ResponseEntity.ok(SubscriptionResource.from(subscriptions.getByAccountId(me.accountId())));
    }
}
