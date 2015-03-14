package grails3grooscript

import grails.plugins.*
import org.grooscript.grails.bean.GrooscriptConverter
import org.grooscript.grails.util.GrooscriptTemplate
import org.grooscript.grails.websocket.SpringWebsocketPlugin

class Grails3GrooscriptGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0.RC1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/assets/javascripts/app/**",
        "grails-app/controllers/**",
        "grails-app/domain/**",
        "grails-app/views/**",
        "src/main/groovy/MyScript.groovy",
        "src/main/web-app/**",
    ]

    // TODO Fill in these fields
    def title = "Grooscript" // Headline display name of the plugin
    def author = "Jorge Franco"
    def authorEmail = 'grooscript@gmail.com'
    def description = '''\
Use grooscript to work in the client side with your groovy code.
It converts the code to javascript and your groovy code will run in the browser.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/grails3-grooscript"

    def license = "APACHE"

    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() { {->
            grooscriptConverter(GrooscriptConverter)
            grooscriptTemplate(GrooscriptTemplate)

            /*if (application.config.grooscript?.websockets == 'springWebsocketPlugin') {
                websocketSender(SpringWebsocketPlugin) {
                    brokerMessagingTemplate = ref('brokerMessagingTemplate')
                }
            }*/
        } 
    }
}
