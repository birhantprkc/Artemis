package de.tum.cit.aet.artemis.core.web.open;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import de.tum.cit.aet.artemis.account.service.OIDCExchangeCodeService;
import de.tum.cit.aet.artemis.shared.base.AbstractSpringIntegrationIndependentTest;

class PublicUserJwtResourceTest extends AbstractSpringIntegrationIndependentTest {

    @Autowired(required = false)
    private OIDCExchangeCodeService oidcExchangeCodeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testExchangeCodeToJwtToken_success() throws Exception {
        if (oidcExchangeCodeService == null) {
            // If OIDC is disabled in test context, endpoint returns 404
            mockMvc.perform(get("/api/core/public/exchange-code").param("code", "any-code")).andExpect(status().isNotFound());
            return;
        }

        String expectedJwt = "mock.jwt.token.string";
        String exchangeCode = oidcExchangeCodeService.storeJwtAndGenerateCode(expectedJwt);

        mockMvc.perform(get("/api/core/public/exchange-code").param("code", exchangeCode)).andExpect(status().isOk()).andExpect(content().string(expectedJwt));
    }

    @Test
    void testExchangeCodeToJwtToken_notFoundForInvalidCode() throws Exception {
        String invalidCode = "invalid-or-expired-code";

        mockMvc.perform(get("/api/core/public/exchange-code").param("code", invalidCode)).andExpect(status().isNotFound());
    }
}
