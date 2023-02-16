package org.jocke.plugin

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.RawText
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.childrenOfType
import com.intellij.psi.xml.XmlFile
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.*
import org.jetbrains.plugins.groovy.GroovyLanguage

class EverythingToGradle : CopyPastePreProcessor {
  override fun preprocessOnCopy(
    file: PsiFile?,
    startOffsets: IntArray?,
    endOffsets: IntArray?,
    text: String?
  ): String? = null

  override fun preprocessOnPaste(
    project: Project?,
    file: PsiFile?,
    editor: Editor?,
    text: String,
    rawText: RawText?
  ): String {
    if (editor == null || project == null) {
      return text
    }

    if (file?.language != KotlinLanguage.INSTANCE) {
      return text
    }

    if (!file.name.endsWith(".gradle.kts")) {
      return text
    }

    val offset = editor.selectionModel.selectionStart

    val findElementAt = file.findElementAt(offset) ?: return text

    val parentOfType = PsiTreeUtil.getParentOfType(findElementAt, KtCallExpression::class.java)
    parentOfType?.let {
      val childrenOfType = it.childrenOfType<KtNameReferenceExpression>().firstOrNull()
      return if (childrenOfType?.text != "dependencies") { // Not in the dependency block
        text
      } else {
        var deps = tryParseGradle(project, text).toKotlinDsl()

        return deps.ifBlank {
          deps = tryParseMaven(project, text).toKotlinDsl()

          deps.ifBlank {
            text
          }
        }
      }
    }

    return text
  }
}

fun tryParseMaven(project: Project, text: String): List<Dependency> {
  val wrapInRoot = """<root>${text}</root>"""
  val xmlFile = PsiFileFactory.getInstance(project).createFileFromText(XMLLanguage.INSTANCE, wrapInRoot) as XmlFile

  // TODO: Look into making it nicer with DomElement https://plugins.jetbrains.com/docs/intellij/xml-dom-api.html#introduction
  return xmlFile.document?.rootTag?.findSubTags("dependency")?.map { tag ->
    val groupId = tag.findFirstSubTag("groupId")?.value?.trimmedText ?: ""
    val artifactId = tag.findFirstSubTag("artifactId")?.value?.trimmedText ?: ""
    val version = tag.findFirstSubTag("version")?.value?.trimmedText ?: ""
    val scope = tag.findFirstSubTag("scope")?.value?.trimmedText ?: ""

    MavenDependency(groupId, artifactId, version, scope)
  } ?: emptyList()
}

fun tryParseGradle(project: Project, text: String): List<Dependency> {
  val f = PsiFileFactory.getInstance(project).createFileFromText(GroovyLanguage, text)
  return parseGroovyCalls(f.childrenOfType())
}
