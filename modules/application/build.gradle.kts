tasks.jar {
    enabled = true
}

tasks.bootJar {
    enabled = false
}

dependencies {
    implementation(projects.modules.domain)
    implementation(projects.modules.common)
    implementation(libs.spring.boot.starter.logging)

    implementation("org.springframework:spring-context")
}
