package org.jocke.plugin

data class MavenDependency(
  var groupId: String = "",
  var artifactId: String = "",
  var version: String? = null,
  var scope: String = ""
) : Dependency {
  override fun toKotlinDsl(): String {
    val s = """$groupId:$artifactId${version?.let { ":$version" } ?: ""}"""
    return when (scope) {
      "test" -> """testImplementation("$s")"""
      "compile" -> """compileOnly("$s")"""
      "runtime" -> """runtimeOnly("$s")"""
      else -> """implementation("$s")"""
    }
  }
}
