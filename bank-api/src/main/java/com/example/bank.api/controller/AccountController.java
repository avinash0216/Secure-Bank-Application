package com.example.bankapi.controller;

import com.example.bankapi.model.Account;
import com.example.bankapi.service.AuditService;
import com.example.bankapi.service.DownstreamAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.HashMap;
import java.util.Map;
import com.example.bankapi.service.TransferService;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private AuditService auditService;
    private DownstreamAccountService downstreamAccountService;
    private TransferService transferService;

    public AccountController (AuditService auditService, DownstreamAccountService downstreamAccountService, TransferService transferService){
        this.auditService = auditService;
        this.downstreamAccountService = downstreamAccountService;
        this.transferService = transferService;
    }

    @GetMapping
    public List<Account> getAll() {
        return transferService.listAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getById(@PathVariable String id) {
        auditService.logEvent("READ_ACCOUNT",id);
        return transferService.listAccounts().stream()
                .filter(a -> a.id().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // TODO 1: Add a POST endpoint that accepts an Account in the request body
    // and returns 201 Created with the account in the response body.
    // The endpoint does not need to persist the account -- this is a stub.
    // Annotate the parameter with @RequestBody.
    // Use ResponseEntity.status(HttpStatus.CREATED).body(account) as the return value.

    @PostMapping
    public ResponseEntity<Account> accounts(Account account){
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    // TODO 6: Complete this endpoint.
// @AuthenticationPrincipal instructs Spring Security to inject the validated Jwt
// from the SecurityContext directly as a method parameter.
// This is cleaner than calling SecurityContextHolder.getContext() manually.
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        // TODO 7: Return a Map containing:
        //   "subject"            -- jwt.getSubject()
        //   "issuer"             -- jwt.getIssuer().toString()
        //   "scopes"             -- jwt.getClaimAsString("scope")
        //   "tokenExpiry"        -- jwt.getExpiresAt().toString()
        //   "roles"              -- jwt.getClaimAsStringList("roles"), or an empty list if null
        //   "preferredUsername"  -- jwt.getClaimAsString("preferred_username"), or "not present"
        //   "fullName"           -- jwt.getClaimAsString("name"), or "not present"
        //
        // The service token will NOT have "roles", "preferred_username", or "name"
        // because the token customizer in the Authorization Server only adds those
        // for user-context tokens. This difference is the main thing to observe.

        Map<String, Object> info = new HashMap<>();
        info.put("subject", jwt.getSubject());
        info.put("issuer", jwt.getIssuer().toString());
        info.put("scopes", jwt.getClaimAsString("scope"));
        info.put("tokenExpiry", jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null);
        List<String> roles = jwt.getClaimAsStringList("roles");
        info.put("roles", roles != null ? roles : List.of());
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        info.put("preferredUsername", preferredUsername != null ? preferredUsername : "not present");
        String fullName = jwt.getClaimAsString("name");
        info.put("fullName", fullName != null ? fullName : "not present");

        return info; // Replace with your implementation
    }

    // TODO 10: Add this endpoint to AccountController.
// For a regular account holder it returns only accounts whose customerId
// matches their sub. For a teller or auditor it returns all accounts.
//
// Read jwt.getSubject() and jwt.getClaimAsStringList("roles").
// Filter ACCOUNTS by customerId for account holders.
// Return the full list for tellers and auditors.
    @GetMapping("/mine")
    public List<Account> getMyAccounts(@AuthenticationPrincipal Jwt jwt) {
        String subject = jwt.getSubject();
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) roles = List.of();

        boolean isStaff = roles.contains("teller") || roles.contains("auditor");
        if (isStaff) {
            return transferService.listAccounts();
        }
        return transferService.listAccounts().stream()
                .filter(a -> a.customerId().equals(subject))
                .toList();
    }

    // TODO 24: Add this endpoint to AccountController.
// It is protected and requires an authenticated caller.
// The inbound request uses the caller's token.
// The outbound call to the downstream service uses the service's own token.
    @GetMapping("/downstream")
    public List<Account> getFromDownstream() {
        // TODO: call downstreamAccountService.fetchAllFromDownstream() and return the result
        return downstreamAccountService.fetchAllFromDownstream();
    }

}