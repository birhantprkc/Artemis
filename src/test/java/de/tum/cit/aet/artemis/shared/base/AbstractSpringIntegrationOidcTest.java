package de.tum.cit.aet.artemis.shared.base;

import org.springframework.test.context.TestPropertySource;

/**
 * Base integration test class for OIDC-enabled test scenarios.
 */
@TestPropertySource(properties = { "artemis.user-management.oidc.enabled=true", "spring.security.oauth2.client.registration.oidc.client-id=mock-client-id",
        "spring.security.oauth2.client.registration.oidc.client-secret=mock-client-secret", "spring.security.oauth2.client.provider.oidc.issuer-uri=http://mock-issuer",
        "spring.security.oauth2.client.provider.oidc.authorization-uri=http://mock-auth", "spring.security.oauth2.client.provider.oidc.token-uri=http://mock-token",
        "spring.security.oauth2.client.provider.oidc.user-info-uri=http://mock-user", "spring.security.oauth2.client.provider.oidc.jwk-set-uri=http://mock-jwk" })
public abstract class AbstractSpringIntegrationOidcTest extends AbstractSpringIntegrationIndependentTest {
}
