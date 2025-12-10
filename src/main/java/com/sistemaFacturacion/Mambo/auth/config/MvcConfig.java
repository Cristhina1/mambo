package com.sistemaFacturacion.Mambo.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto expone la carpeta "uploads" para que sea accesible vía URL
        // Ejemplo: http://localhost:8080/images/mi-foto.jpg
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/"); // O la ruta absoluta donde guardes: "file:C:/imagenes/"
    }
}
