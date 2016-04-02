package org.grooscript.grails.util

import org.grooscript.grails.tag.GrooscriptTagLib

import static org.grooscript.util.Util.SEP

import javax.annotation.ParametersAreNonnullByDefault
import java.util.regex.Matcher

/**
 * @author Jorge Franco <jorge.franco@osoco.es>
 */
@ParametersAreNonnullByDefault
final class Util {

    private Util() {
        // don't init me
    }

    private static final String DOMAIN_DIR = "grails-app${SEP}domain"
    private static final String PLUGIN_MESSAGE = '[Grooscript Plugin]'

    static final String GROOVY_SRC_DIR = "src${SEP}main${SEP}groovy"

    static void consoleMessage(message) {
        println "${PLUGIN_MESSAGE} [INFO] $message"
    }

    static void consoleError(message) {
        println "\u001B[91m${PLUGIN_MESSAGE} [ERROR] $message\u001B[0m"
    }

    static void consoleWarning(message) {
        println "\u001B[93m${PLUGIN_MESSAGE} [WARNING] $message\u001B[0m"
    }

    static String getNewTemplateName() {
        'fTemplate' + String.valueOf(new Date().time)
    }

    static String getDomainFileText(String domainClassCanonicalName) {
        getFileContentsIfExists("${DOMAIN_DIR}", domainClassCanonicalName)
    }

    static Map customizationAstOption(Class clazz) {
        [customization: {
            ast(clazz)
        }]
    }

    static String getResourceText(String shortClassName) {
        GrooscriptTagLib.classLoader.getResource(shortClassName).text
    }

    static String getClassSource(String fullClassName) {
        getFileContentsIfExists("${GROOVY_SRC_DIR}", fullClassName)
    }

    static String removeLastSemicolon(String code) {
        return code.lastIndexOf(';') >= 0 ? code.substring(0, code.lastIndexOf(';')) : code
    }

    private static String getFileContentsIfExists(String baseDir, String fileClassName) {
        def filePath = "${baseDir}${SEP}${getFileNameFromDomainClassCanonicalName(fileClassName)}"
        try {
            new File(filePath).text
        } catch (IOException ioe) {
            consoleError("Couldn't get class file: ${filePath}: ${ioe.message}")
            ''
        }
    }

    private static String getFileNameFromDomainClassCanonicalName(String domainClassCanonicalName) {
        "${domainClassCanonicalName.replaceAll("\\.", Matcher.quoteReplacement(SEP))}.groovy"
    }

}
