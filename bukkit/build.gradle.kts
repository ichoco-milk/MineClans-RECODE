plugins {
    id("com.gradleup.shadow") version "9.0.0-beta12"
}

repositories {
    maven("https://repo.extendedclip.com/releases/")
    maven("https://jitpack.io")
    maven("https://repo.mikeprimm.com")
    maven("https://maven.enginehub.org/repo")
    maven("https://repo.dmulloy2.net/repository/public")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("redis.clients:jedis:6.1.0")
    implementation("com.github.MilkBowl:VaultAPI:1.7")
    implementation("us.dynmap:dynmap-api:3.6")
    implementation("com.comphenix.protocol:ProtocolLib:5.3.0")
}

tasks.shadowJar {}
