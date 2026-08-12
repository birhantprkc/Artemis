package de.tum.cit.aet.artemis.account.security;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import de.tum.cit.aet.artemis.account.config.OIDCEnabled;

@Service
@Lazy
@Conditional(OIDCEnabled.class)
public class OIDCExchangeCodeService {

    private final IMap<String, String> codeToJwtMap;

    public OIDCExchangeCodeService(HazelcastInstance hazelcastInstance) {
        this.codeToJwtMap = hazelcastInstance.getMap("oidcExchangeCodes");
    }

    /**
     * Generate exchange code and store the code-jwt relationship to cache
     *
     * @param jwtToken The jwtToken which should be obtained in the end
     * @return The code which is exchanged by client to receive jwt Token
     */
    public String storeJwtAndGenerateCode(String jwtToken) {
        String code = RandomUtil.generateResetKey();
        // TTL = 5 minutes to compensate some internet connection issues:
        codeToJwtMap.put(code, jwtToken, 5, TimeUnit.MINUTES);
        return code;
    }

    /**
     * Exchamge the code to obtain the jwtToken
     *
     * @param code The single use token which is exchanged to jwtToken
     */
    public String redeemCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        // Code may be used only once
        return codeToJwtMap.remove(code);
    }
}
