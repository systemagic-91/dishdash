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

    // Adiciona o Mockito como Java Agent explicitamente durante a execução dos testes.
    //
    // Nas versões mais novas do JDK, o Mockito não pode mais se auto-anexar
    // dinamicamente (self-attaching) para habilitar o inline mock maker.
    // Isso gera warnings e será bloqueado no futuro.
    //
    // Ao configurar o -javaagent manualmente, garantimos que:
    // - os mocks (inclusive de classes finais) continuem funcionando
    // - eliminamos os warnings do ByteBuddy/JDK
    // - o build fique compatível com futuras versões do Java
    //
    // Aqui buscamos o jar do Mockito no classpath de teste e passamos como agent.
    jvmArgs(
        "-javaagent:${configurations.testRuntimeClasspath.get()
            .find { it.name.contains("mockito-core") }?.absolutePath}"
    )
}
