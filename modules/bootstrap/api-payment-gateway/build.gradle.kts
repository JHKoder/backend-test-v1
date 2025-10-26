plugins {
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

tasks.jar {
    enabled = false
}

val asciidoctorExt by configurations.creating

dependencies {
    implementation(projects.modules.domain)
    implementation(projects.modules.application)
    implementation(projects.modules.common)
    implementation(projects.modules.infrastructure.persistence)
    implementation(projects.modules.external.pgClient)
    implementation(libs.spring.boot.starter.jpa)
    implementation(libs.bundles.bootstrap)
    testImplementation(libs.bundles.test)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(module = "mockito-core")
    }

    testImplementation(libs.database.h2)

    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")

    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

// REST Docs 설정
val snippetsDir = file("build/generated-snippets")

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.asciidoctor {
    dependsOn(tasks.test)
    configurations(asciidoctorExt.name)

    setSourceDir(file("docs/asciidoc"))
    setBaseDir(file("docs/asciidoc"))

    sources {
        include("**/index.adoc")
    }

    attributes(
        mapOf("snippets" to snippetsDir)
    )

    doLast {
        println("Asciidoctor output directory: ${outputDir.absolutePath}")
    }
}

tasks.register("copyHTML", Copy::class) {
    dependsOn(tasks.asciidoctor)
    from(file("build/docs/asciidoc"))
    into(file("src/main/resources/static/docs"))

    doLast {
        println("Copied HTML files to src/main/resources/static/docs")
    }
}

tasks.bootJar {
    enabled = true
    dependsOn(tasks.getByName("copyHTML"))
}

tasks.build {
    dependsOn(tasks.getByName("copyHTML"))
}
