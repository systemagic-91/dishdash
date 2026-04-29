plugins {
    id("org.springframework.boot")
}

dependencies {

    // WebClient — para o order-service chamar o inventory via HTTP
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    // Kafka — gerenciado pelo BOM do Spring Boot 4
    implementation("org.springframework.boot:spring-boot-starter-kafka")

    // Testes
    testImplementation("org.springframework.boot:spring-boot-starter-kafka-test")
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