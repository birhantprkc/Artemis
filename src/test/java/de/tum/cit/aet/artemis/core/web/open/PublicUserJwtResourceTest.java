package de.tum.cit.aet.artemis.core.web.open;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import de.tum.cit.aet.artemis.shared.base.AbstractSpringIntegrationIndependentTest;

class PublicUserJwtResourceTest extends AbstractSpringIntegrationIndependentTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testExchangeCodeToJwtToken_whenOidcDisabled_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/core/public/exchange-code").param("code", "any-code")).andExpect(status().isNotFound());
    }
}
