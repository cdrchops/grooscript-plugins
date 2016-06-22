package org.grooscript.grails

import grails.plugins.Plugin

import org.grooscript.grails.bean.JavascriptConverter
import org.grooscript.grails.event.DefaultWebsocketConfig
import org.grooscript.grails.event.GrooscriptEventsInterceptor
import org.grooscript.grails.util.GrooscriptGrailsHelpers
import org.grooscript.grails.util.JavascriptTemplate

class GrooscriptGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.0.0 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/assets/javascripts/app/**",
        "grails-app/controllers/**",
        "grails-app/domain/**",
        "grails-app/views/**",
        "src/main/web-app/**",
    ]

    def title = "Grooscript" // Headline display name of the plugin
    def author = "Jorge Franco"
    def authorEmail = 'grooscript@gmail.com'
    def description = '''\
Use grooscript to work in the client side with your groovy code.
It converts the code to javascript and your groovy code will run in the browser.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grooscript.org/grails3-plugin/"

    def license = "APACHE"

    def developers = [ [ name: "Christian Meyer", email: "" ]]

    def issueManagement = [ system: "Github", url: "https://github.com/chiquitinxx/grooscript-grails3-plugin/issues" ]

    def scm = [ url: "https://github.com/chiquitinxx/grooscript-grails3-plugin" ]

    def loadAfter = ['grails-spring-websocket']

    @Override
    Closure doWithSpring() {
        { ->
            javascriptConverter(JavascriptConverter)
            javascriptTemplate(JavascriptTemplate)
            grooscriptGrailsHelpers(GrooscriptGrailsHelpers)
            if (GrooscriptGrailsHelpers.isSpringWebsocketsActive(applicationContext)) {
                grooscriptEventsInterceptor(GrooscriptEventsInterceptor)
                webSocketConfig(DefaultWebsocketConfig)
            }
        } 
    }
}
