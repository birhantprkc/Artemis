package de.tum.cit.aet.artemis.account.service;

import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import de.tum.cit.aet.artemis.account.config.OIDCEnabled;
import de.tum.cit.aet.artemis.account.security.RandomUtil;

/**
 * Service to store and exchange single-use OIDC codes for JWT tokens.
 */
@Service
@Lazy
@Conditional(OIDCEnabled.class)
public class OIDCExchangeCodeService {

    private final HazelcastInstance hazelcastInstance;

    private IMap<String, String> codeToJwtMap;

    public OIDCExchangeCodeService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @PostConstruct
    public void init() {
        this.codeToJwtMap = hazelcastInstance.getMap("oidcExchangeCodes");
    }

    /**
     * Generate exchange code and store the code-jwt relationship to cache.
     *
     * @param jwtToken The jwtToken which should be obtained in the end.
     * @return The code which is exchanged by client to receive jwt Token.
     */
    public String storeJwtAndGenerateCode(String jwtToken) {
        String code = RandomUtil.generateResetKey();
        codeToJwtMap.put(code, jwtToken, 5, TimeUnit.MINUTES);
        return code;
    }

    /**
     * Exchanges the single-use code to obtain the JWT token.
     *
     * @param code The single-use token which is exchanged to receive the JWT token.
     * @return The JWT token associated with the code, or null if the code is invalid or expired.
     */
    public String redeemCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return codeToJwtMap.remove(code);
    }
}
