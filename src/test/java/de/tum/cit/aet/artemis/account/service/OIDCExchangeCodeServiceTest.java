package de.tum.cit.aet.artemis.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

class OIDCExchangeCodeServiceTest {

    private HazelcastInstance hazelcastInstance;

    private IMap<String, String> codeToJwtMap;

    private OIDCExchangeCodeService oidcExchangeCodeService;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        hazelcastInstance = mock(HazelcastInstance.class);
        codeToJwtMap = mock(IMap.class);
        when(hazelcastInstance.<String, String>getMap("oidcExchangeCodes")).thenReturn(codeToJwtMap);

        oidcExchangeCodeService = new OIDCExchangeCodeService(hazelcastInstance);
    }

    @Test
    void testStoreJwtAndGenerateCode_storesTokenWithTTL() {
        String jwtToken = "mock_jwt_token_123";

        String code = oidcExchangeCodeService.storeJwtAndGenerateCode(jwtToken);

        assertThat(code).isNotNull().isNotBlank();
        verify(codeToJwtMap).put(eq(code), eq(jwtToken), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testRedeemCode_validCode_returnsJwtTokenAndRemovesKey() {
        String code = "valid_exchange_code";
        String jwtToken = "mock_jwt_token_123";
        when(codeToJwtMap.remove(code)).thenReturn(jwtToken);

        String redeemedToken = oidcExchangeCodeService.redeemCode(code);

        assertThat(redeemedToken).isEqualTo(jwtToken);
        verify(codeToJwtMap).remove(code);
    }

    @Test
    void testRedeemCode_invalidOrNullCode_returnsNull() {
        assertThat(oidcExchangeCodeService.redeemCode(null)).isNull();
        assertThat(oidcExchangeCodeService.redeemCode("   ")).isNull();

        when(codeToJwtMap.remove("invalid_code")).thenReturn(null);
        assertThat(oidcExchangeCodeService.redeemCode("invalid_code")).isNull();
    }
}
