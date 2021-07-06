package no.dnb.awsintro.infrastructure

internal data class Label(val label: String?) {
    fun prefix(name: String, separator: String = "-"): String = if (label.isNullOrBlank()) name else "$label$separator$name"
    fun postfix(name: String, separator: String = "-"): String = if (label.isNullOrBlank()) name else "$name$separator$label"
}

data class Namespace(val environment: String, val label: String?) {
    private val namespaceString = Label(label).postfix(environment)

    /**
     * Adds the namespace to the start of baseName
     */
    fun prefixWithNamespace(baseName: String, separator: String = "-"): String = Label(namespaceString).prefix(baseName, separator)

    /**
     * Adds the namespace to the end of baseName
     */
    fun postfixWithNamespace(baseName: String, separator: String = "-"): String = Label(namespaceString).postfix(baseName, separator)

    /**
     * Adds the label to the end of baseName
     */
    fun postfixWithLabel(baseName: String, separator: String = "-"): String = Label(label).postfix(baseName, separator)

    /**
     * Adds the environment to the start of baseName
     */
    fun prefixWithEnvironment(baseName: String, separator: String = "-"): String = Label(environment).prefix(baseName, separator)

    /**
     * Adds the label to the end of baseName
     */
    fun postfixWithEnvironment(baseName: String, separator: String = "-"): String = Label(environment).postfix(baseName, separator)
}