val skywalkingAgentVersion = "8.12.0"
val hutoolVersion = "5.8.18"
val fastjsonVersion = "1.2.83"

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.apache.skywalking:apm-agent-core:$skywalkingAgentVersion")
    implementation("cn.hutool:hutool-http:$hutoolVersion")
    implementation("com.alibaba:fastjson:$fastjsonVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks{
    shadowJar{
        mergeServiceFiles()
        dependencies{
           exclude(dependency(":gson"))
           exclude(dependency("io.grpc:"))
           exclude(dependency("io.netty:"))
           exclude(dependency("io.opencensus:"))
           exclude(dependency("com.google.*:"))
           exclude(dependency("com.google.guava:guava"))
           exclude(dependency("org.checkerframework:checker-compat-qual"))
           exclude(dependency("org.codehaus.mojo:animal-sniffer-annotations"))
           exclude(dependency("io.perfmark:"))
           exclude(dependency("org.slf4j:"))
        }
        manifest {
            attributes["Created-By"] = GradleVersion.current()
            attributes["Built-By"] = System.getProperty("user.name")
            attributes["Build-Jdk"] = System.getProperty("java.version")
            attributes["Specification-Title"] = "apm-agent"
            attributes["Specification-Version"] = skywalkingAgentVersion
            /*
            Specification-Vendor: The Apache Software Foundation
            Implementation-Title: apm-agent
            Implementation-Version: 8.12.0-SNAPSHOT
            Implementation-Vendor-Id: org.apache.skywalking
            Implementation-Vendor: The Apache Software Foundation
            Implementation-URL: http://maven.apache.org*/

            attributes["Can-Redefine-Classes"] = true
            attributes["Can-Retransform-Classes"] = true
            attributes["Premain-Class"] = "org.apache.skywalking.apm.agent.SkyWalkingAgent"

        }



        relocate("net.bytebuddy","org.apache.skywalking.apm.dependencies.net.bytebuddy")
        relocate("cn.hutool","cloud.pandas.shaded.cn.hutool")
        relocate("com.alibaba.fastjson","cloud.pandas.shaded.com.alibaba.fastjson")
    }

}