package org.jocke.plugin

import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrApplicationStatementImpl

data class GradleDependency(
  val function: Function,
  var name: String = "",
  var group: String = "",
  var version: String = "",
  // TODO: Be able to activate this maybe?
  val withoutIdentifiers: Boolean = true,
): Dependency {
  override fun toKotlinDsl(): String {
    if (withoutIdentifiers) {
      return """${function.type}("$group:$name:$version")"""
    }
    return """${function.type}(group = "$group", name = "$name", version = "$version")"""
  }
}

fun parseGroovyCalls(appStatements: List<GrApplicationStatementImpl>): List<GradleDependency> {
  return appStatements.map {
    val function = Function.fromString(it.callReference!!.methodName) ?: Function.UNKNOWN

    val d = GradleDependency(function)
    it.argumentList.namedArguments.forEach { n ->
      when (n.labelName) {
        "name" -> d.name = n.expression?.text?.replace("\'", "") ?: ""
        "group" -> d.group = n.expression?.text?.replace("\'", "") ?: ""
        "version" -> d.version = n.expression?.text?.replace("\'", "") ?: ""
      }
    }

    d
  }.filter {
    it.function != Function.UNKNOWN || it.name.isNotBlank() || it.group.isNotBlank() || it.group.isNotBlank()
  }
}

enum class Function(val type: String) {
  UNKNOWN("unknown"),

  ANNOTATION_PROCESSOR("annotationProcessor"),
  API("api"),
  API_DEPENDENCIES_METADATA("apiDependenciesMetadata"),
  IMPLEMENTATION("implementation"),
  COMPILE("compile"),
  IMPLEMENTATION_DEPENDENCIES_METADATA("implementationDependenciesMetadata"),
  COMPILE_ONLY("compileOnly"),
  COMPILE_ONLY_DEPENDENCIES_METADATA("compileOnlyDependenciesMetadata"),
  RUNTIME_ONLY("runtimeOnly"),
  RUNTIME_ONLY_DEPENDENCIES_METADATA("runtimeOnlyDependenciesMetadata"),

  TEST_ANNOTATION_PROCESSOR("testAnnotationProcessor"),
  TEST_API("testApi"),
  TEST_API_DEPENDENCIES_METADATA("testApiDependenciesMetadata"),
  TEST_IMPLEMENTATION("testImplementation"),
  TEST_IMPLEMENTATION_DEPENDENCIES_METADATA("testImplementationDependenciesMetadata"),
  TEST_COMPILE_ONLY("testCompileOnly"),
  TEST_COMPILE_ONLY_DEPENDENCIES_METADATA("testCompileOnlyDependenciesMetadata"),
  TEST_RUNTIME_ONLY("testRuntimeOnly"),
  TEST_RUNTIME_ONLY_DEPENDENCIES_METADATA("testRuntimeOnlyDependenciesMetadata");

  companion object {
    fun fromString(type: String?): Function? {
      if (type == "compile") {
        return IMPLEMENTATION
      }
      for (value in values()) {
        if (value.type.equals(type, false)) {
          return value
        }
      }
      return null
    }
  }
}
