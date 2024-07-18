import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000") // İzin verilen origin (istemci)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // İzin verilen HTTP metodları
                .allowedHeaders("*") // Tüm başlıklara izin ver
                .allowCredentials(true) // Credentials izin ver (cookies, auth header vs.)

    }
}

