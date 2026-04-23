plugins {
    java
    id("org.springframework.boot") version "4.0.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

// Configuração comum a todos os subprojetos
subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    group = "com"
    version = "0.0.1-SNAPSHOT"
    description = "dishdash"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
    }

    // Importa o BOM do Spring Boot para todos os subprojetos
    // Isso garante que todas as versões de dependências sejam compatíveis
    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.5")
        }
    }
}
