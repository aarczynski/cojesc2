package pl.arczynskiadam.cojesc.http.client.facebook.config;

import feign.RequestInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:/facebook-credentials.yaml")
@EnableConfigurationProperties(FacebookApiAuthProperties.class)
public class FeignFacebookApiClientConfig {

    private static final String ACCESS_TOKEN = "access_token";

    @Bean
    public RequestInterceptor requestInterceptor(FacebookApiAuthProperties auth) {
        return requestTemplate -> requestTemplate.query(ACCESS_TOKEN, String.join("|", auth.getAppId(), auth.getSecret()));
    }
}
