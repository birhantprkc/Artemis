package de.tum.cit.aet.artemis.account.authentication;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import de.tum.cit.aet.artemis.account.security.OIDCExchangeCodeService;
import de.tum.cit.aet.artemis.shared.base.AbstractSpringIntegrationIndependentTest;

class PublicUserJwtResourceTest extends AbstractSpringIntegrationIndependentTest {

    @MockitoBean // Если сервиса нет в контексте как мока, ставим @MockitoBean вместо @Autowired
    private OIDCExchangeCodeService oidcExchangeCodeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testExchangeCodeToJwtToken_success() throws Exception {
        String exchangeCode = "valid-exchange-code-123";
        String expectedJwt = "mock.jwt.token.string";

        when(oidcExchangeCodeService.redeemCode(exchangeCode)).thenReturn(expectedJwt);

        mockMvc.perform(get("/api/core/public/exchange-code").param("code", exchangeCode)).andExpect(status().isOk()).andExpect(content().string(expectedJwt));
    }

    @Test
    void testExchangeCodeToJwtToken_notFoundForInvalidCode() throws Exception {
        String invalidCode = "invalid-or-expired-code";

        when(oidcExchangeCodeService.redeemCode(invalidCode)).thenReturn(null);

        mockMvc.perform(get("/api/core/public/exchange-code").param("code", invalidCode)).andExpect(status().isNotFound());
    }
}
