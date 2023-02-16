package org.jocke.plugin

data class MavenDependency(
    var groupId: String = "",
    var artifactId: String = "",
    var version: String = "",
    var scope: String = ""
): Dependency {
    override fun toKotlinDsl(): String {
        return when (scope) {
          "test" -> """testImplementation("$groupId:$artifactId:$version")"""
          "compile" -> """compileOnly("$groupId:$artifactId:$version")"""
          "runtime" -> """runtimeOnly("$groupId:$artifactId:$version")"""
          else -> """implementation("$groupId:$artifactId:$version")"""
        }
    }
}
