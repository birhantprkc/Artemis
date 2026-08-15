package de.tum.cit.aet.artemis.core.web.open;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import de.tum.cit.aet.artemis.account.service.OIDCExchangeCodeService;
import de.tum.cit.aet.artemis.shared.base.AbstractSpringIntegrationOidcTest;

class PublicUserJwtResourceOIDCIntegrationTest extends AbstractSpringIntegrationOidcTest {

    @Autowired
    private OIDCExchangeCodeService oidcExchangeCodeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testExchangeCodeToJwtToken_success() throws Exception {
        String expectedJwt = "mock.jwt.token.string";
        String exchangeCode = oidcExchangeCodeService.storeJwtAndGenerateCode(expectedJwt);

        mockMvc.perform(get("/api/core/public/exchange-code").param("code", exchangeCode)).andExpect(status().isOk()).andExpect(content().string(expectedJwt));
    }

    @Test
    void testExchangeCodeToJwtToken_notFoundForInvalidCode() throws Exception {
        String invalidCode = "invalid-or-expired-code";

        mockMvc.perform(get("/api/core/public/exchange-code").param("code", invalidCode)).andExpect(status().isNotFound());
    }

    @Test
    void testExchangeCodeToJwtToken_codeIsSingleUse() throws Exception {
        String expectedJwt = "single-use.jwt.token";
        String exchangeCode = oidcExchangeCodeService.storeJwtAndGenerateCode(expectedJwt);

        // Первый запрос должен вернуть 200 OK
        mockMvc.perform(get("/api/core/public/exchange-code").param("code", exchangeCode)).andExpect(status().isOk()).andExpect(content().string(expectedJwt));

        // Повторный запрос с тем же кодом должен вернуть 404 Not Found
        mockMvc.perform(get("/api/core/public/exchange-code").param("code", exchangeCode)).andExpect(status().isNotFound());
    }
}
