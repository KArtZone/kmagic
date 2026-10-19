package pro.artkart.kmagic.functionsolutions

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jdom2.Element
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import pro.artkart.kmagic.common.List
import pro.artkart.kmagic.common.Result
import pro.artkart.kmagic.common.sequence
import java.io.File
import java.io.IOException
import java.io.StringReader

val log = KotlinLogging.logger { }
const val format = "\n\tFirst Name : %s\n" +
        "\tLast Name : %s\n" +
        "\tEmail : %s\n" +
        "\tSalary : %s"
val elementNames = List("firstName", "lastName", "email", "salary")

@ConsistentCopyVisibility
data class FilePath private constructor(val value: Result<String>) {
    companion object {
        operator fun invoke(path: String): FilePath = FilePath(
            Result.of({ isValidPath(path) }, path, "Invalid file path: $path")
        )

        private fun isValidPath(path: String): Boolean = true
    }
}

@ConsistentCopyVisibility
data class ElementName private constructor(val value: Result<String>) {
    companion object {
        operator fun invoke(name: String): ElementName = ElementName(
            Result.of({ isValidName(name) }, name, "Invalid name: $name")
        )

        fun isValidName(name: String): Boolean = true
    }
}

fun readFileToString(path: String): Result<String> = Result.of { File(path).readText() }

fun readDocument(rootElementName: String, stringDoc: String): Result<List<Element>> = try {
    SAXBuilder()
        .build(StringReader(stringDoc))
        .rootElement
        .let {
            Result(List(*it.getChildren(rootElementName).toTypedArray()))
        }
} catch (io: IOException) {
    Result.failure("Invalid root element name '$rootElementName'or XML data $stringDoc: ${io.message}")
} catch (jde: JDOMException) {
    Result.failure("Invalid root element name '$rootElementName' or XML data $stringDoc: ${jde.message}")
} catch (e: Exception) {
    Result.failure("Unexpected error while reading XML data $stringDoc: ${e.message}")
}

fun processElement(element: Element, format: Pair<String, List<String>>): String = String.format(
    format.first,
    *format.second.map { element.getChildText(it) }.toArrayList().toArray()
)

fun processElementV2(element: Element): String {
    return String.format(
        format,
        *elementNames.map { element.getChildText(it) }
            .toArrayList()
            .toTypedArray()
    )
}

fun processElementV3(element: Element): Result<String> {
    return try {
        Result(
            String.format(
                format,
                *elementNames.map { getChildText(element, it) }
                    .toArrayList()
                    .toTypedArray()
            ))
    } catch (e: Exception) {
        Result.failure(
            "Exception while formatting element. Probable cause is a missing element name in element list $elementNames"
        )
    }
}

val processElementV4: (List<String>) -> (String) -> (Element) -> Result<String> = { elementNames ->
    { format ->
        { element ->
            try {
                Result(
                    String.format(
                        format,
                        *elementNames.map { getChildText(element, it) }
                            .toArrayList()
                            .toTypedArray()
                    ))
            } catch (e: Exception) {
                Result.failure(
                    "Exception while formatting element. Probable cause is a missing element name in element list ${pro.artkart.kmagic.functionsolutions.elementNames}"
                )
            }
        }
    }
}

fun getChildText(element: Element, name: String): String =
    element.getChildText(name) ?: "Element $name is not a child of ${element.name}"

fun toStringList(list: List<Element>, format: Pair<String, List<String>>) =
    list.map { processElement(it, format) }

fun getRootElementName(): Result<String> = Result.of { "staff" }

fun getXmlFilePath(): Result<String> = Result.of { "staff.xml" }

fun processList(list: List<String>) = list.forEach { log.info { it } }

fun read(path: String) {
    try {
        SAXBuilder()
            .build(File(path))
            .rootElement
            .getChildren("staff")
            .forEach { element ->
                log.info {
                    "\nFirst name: ${element.getChildText("firstName")}\n" +
                            "Last name: ${element.getChildText("lastName")}\n" +
                            "Email: ${element.getChildText("email")}\n" +
                            "Salary: ${element.getChildText("salary")}"
                }
            }
    } catch (e: IOException) {
        log.error { e.message }
    } catch (e: JDOMException) {
        log.error { e.message }
    }
}

fun readV2() {
    getXmlFilePath()
        .flatMap(::readFileToString)
        .flatMap { doc ->
            getRootElementName()
                .flatMap { rootName ->
                    readDocument(rootName, doc)
                }.map { list ->
                    toStringList(
                        list, Pair(
                            format,
                            List("firstName", "lastName", "email", "salary")
                        )
                    )
                }
        }.forEach(::processList, { log.error { it.message } })
}

fun readV3(
    path: () -> Result<String>,
    rootName: () -> Result<String>,
    format: Pair<String, List<String>>,
    effect: (List<String>) -> Unit
): () -> Unit = {
    path()
        .flatMap(::readFileToString)
        .flatMap { doc ->
            rootName().flatMap { root ->
                readDocument(root, doc)
            }
        }
        .map { list -> toStringList(list, format) }
        .forEach(effect, { log.error { it.message } })
}

fun readV4(
    path: () -> FilePath,
    rootName: () -> ElementName,
    format: Pair<String, List<String>>,
    effect: (List<String>) -> Unit
): () -> Unit = {
    path().value
        .flatMap(::readFileToString)
        .flatMap { doc ->
            rootName().value.flatMap { root ->
                readDocument(root, doc)
            }
        }
        .map { list -> toStringList(list, format) }
        .forEach(effect, { log.error { it.message } })
}

fun <T> readV5(
    path: () -> FilePath,
    rootName: () -> ElementName,
    function: (Element) -> T,
    effect: (List<T>) -> Unit
): () -> Unit = {
    path().value
        .flatMap(::readFileToString)
        .flatMap { doc ->
            rootName().value.flatMap { root ->
                readDocument(root, doc)
            }
        }
        .map { list -> list.map(function) }
        .forEach(effect, { throw it })
}

fun <T> readV6(
    path: () -> FilePath,
    rootName: () -> ElementName,
    function: (Element) -> Result<T>,
    effect: (List<T>) -> Unit
): () -> Unit = {
    path().value
        .flatMap(::readFileToString)
        .flatMap { doc ->
            rootName().value.flatMap { root ->
                readDocument(root, doc)
            }
        }
        .flatMap { list -> sequence(list.map(function)) }
        .forEach(effect, { throw it })
}

fun main() {
    val format = "\n\tFirst Name : %s\n" +
            "\tLast Name : %s\n" +
            "\tEmail : %s\n" +
            "\tSalary : %s"
    val elementNames = List("firstName", "lastName", "email", "salary")
    fun getXmlFilePathV2(): FilePath = FilePath("staff.xml")
    fun getRootElementNameV2(): ElementName = ElementName("staff")
    fun processList(list: List<String>) = list.forEach { log.info { it } }
    readV6(
        ::getXmlFilePathV2,
        ::getRootElementNameV2,
        processElementV4(elementNames)(format),
        ::processList
    ).let { program ->
        try {
            program()
        } catch (e: Exception) {
            log.error { "An exception occurred: ${e.message}" }
        }
    }
}
