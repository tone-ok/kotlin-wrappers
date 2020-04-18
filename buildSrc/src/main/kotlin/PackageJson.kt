import org.json.JSONObject
import org.gradle.api.Task

private const val REPO_URL = "https://github.com/JetBrains/kotlin-wrappers"
private const val LICENSE = "Apache-2.0"

private fun Task.prop(propertyName: String): String =
    project.property(propertyName) as String

internal fun Task.generatePackageJson(): String =
    JSONObject().apply {
        put("name", "@jetbrains/${project.name}")
        put("description", prop("description"))
        put("version", project.npmVersion())
        put("main", jsOutputFileName)
        put("repository", REPO_URL)
        put("peerDependencies", peerDependencies())
        put("author", prop("author"))
        put("license", LICENSE)
    }.toString(2)

private fun Task.peerDependencies(): JSONObject {
    val map = versionMap()
        .mapKeys { "${'$'}${it.key}" }

    val source = project.projectDir
        .resolve("package.json").readText()

    val content = map.entries.fold(source) { data, (key, value) ->
        data.replace(key, value)
    }

    return JSONObject(content)
        .getJSONObject("peerDependencies")
}

private fun Task.versionMap(): Map<String, String> =
    sequenceOf(
        "css",
        "kotlin",
        "kotlinext",
        "mocha",
        "react",
        "react-dom",
        "react-redux",
        "react-router-dom",
        "redux",
        "styled"
    ).associate(project::versionPair)