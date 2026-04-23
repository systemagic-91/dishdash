plugins{
    id("org.springframework.boot")
}

dependencies {

    // Web reativo — Spring Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // MongoDB reativo — R2DBC não existe pro Mongo, usamos o driver reativo nativo
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    // Validação de DTOs
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Actuator — health check, métricas
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Lombok — reduz boilerplate (getters, builders, etc.)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testes
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test") // para testar Mono/Flux

}

tasks.withType<Test> {
    useJUnitPlatform()
}
