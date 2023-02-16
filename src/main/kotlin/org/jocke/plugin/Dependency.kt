package org.jocke.plugin

interface Dependency {
  fun toKotlinDsl(): String
}

fun List<Dependency>.toKotlinDsl(): String = this.joinToString(separator = "\n") { it.toKotlinDsl() }