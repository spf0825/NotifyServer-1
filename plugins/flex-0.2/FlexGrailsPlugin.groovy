/**
 * Copyright 2007 Marcel Overdijk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import flex.messaging.MessageBroker
import flex.messaging.services.RemotingService
import flex.messaging.services.remoting.RemotingDestination
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.plugins.flex.FlexUtils
import org.codehaus.groovy.grails.plugins.support.GrailsPluginUtils

class FlexGrailsPlugin {
    def version = 0.2
    def dependsOn = [services: GrailsPluginUtils.grailsVersion]
    def observe = ["services"]
    def author = "Marcel Overdijk"
    def authorEmail = "marceloverdijk@gmail.com"
    def title = "Provides integration between Grails and Flex"
    def description = ""
    def documentation = "http://grails.org/Flex+Plugin"
    
    def doWithWebDescriptor = { xml ->
        def webtierCompilerEnabled = (application.config.flatten().containsKey("flex.webtier.compiler.enabled") ? application.config.flex.webtier.compiler.enabled : GrailsUtil.isDevelopmentEnv())
        def useContextClassLoader = (application.config.flatten().containsKey("flex.use.context.classloader") ? application.config.flex.use.context.classloader : true)        

        // context params
        def contextParams = xml.'context-param'
        contextParams[contextParams.size() - 1] + {
            'context-param' {
                'param-name'("flex.class.path")
                'param-value'("/WEB-INF/flex/hotfixes,/WEB-INF/flex/jars")
            }
        }
            
        // listeners
        def listeners = xml.listener
        listeners[listeners.size() - 1] + {
            listener {
                'listener-class'("flex.messaging.HttpFlexSession")
            }
        }     

        // servlets
        def servlets = xml.servlet
        servlets[servlets.size() - 1] + {
            servlet {
                'servlet-name'("MessageBrokerServlet")
                'display-name'("MessageBrokerServlet")
                'servlet-class'("flex.messaging.MessageBrokerServlet")
                'init-param' {
                    'param-name'("services.configuration.file")
                    'param-value'("/WEB-INF/flex/services-config.xml")
                }
                'init-param' {
                    'param-name'("flex.write.path")
                    'param-value'("/WEB-INF/flex")
                }
                'init-param' {
                    'param-name'("useContextClassLoader")
                    'param-value'(useContextClassLoader)
                }
                'load-on-startup'("1")
            }
            servlet {
                'servlet-name'("FlexForbiddenServlet")
                'display-name'("Prevents access to *.as/*.swc files")
                'servlet-class'("flex.bootstrap.BootstrapServlet")
                'init-param' {
                    'param-name'("servlet.class")
                    'param-value'("flex.webtier.server.j2ee.ForbiddenServlet")
                }
            }
            if (webtierCompilerEnabled) {
                servlet {
                    'servlet-name'("FlexMxmlServlet")
                    'display-name'("MXML Processor")
                    'description'("Servlet wrapper for the Mxml Compiler")
                    'servlet-class'("flex.bootstrap.BootstrapServlet")
                    'init-param' {
                        'param-name'("servlet.class")
                        'param-value'("flex.webtier.server.j2ee.MxmlServlet")
                    }
                    'init-param' {
                        'param-name'("webtier.configuration.file")
                        'param-value'("/WEB-INF/flex/flex-webtier-config.xml")
                    }
                    'load-on-startup'("1")
                }
                servlet {
                    'servlet-name'("FlexSwfServlet")
                    'display-name'("SWF Retriever")
                    'servlet-class'("flex.bootstrap.BootstrapServlet")
                    'init-param' {
                        'param-name'("servlet.class")
                        'param-value'("flex.webtier.server.j2ee.SwfServlet")
                    }
                    'load-on-startup'("2")
                }
                servlet {
                    'servlet-name'("FlexInternalServlet")
                    'servlet-class'("flex.bootstrap.BootstrapServlet")
                    'init-param' {
                        'param-name'("servlet.class")
                        'param-value'("flex.webtier.server.j2ee.filemanager.FileManagerServlet")
                    }
                    'load-on-startup'("10")
                }
            }
        }
    
        // servlet mappings
        def servletMappings = xml.'servlet-mapping'
        servletMappings[servletMappings.size() - 1] + {
            'servlet-mapping' {
                'servlet-name'("MessageBrokerServlet")
                'url-pattern'("/messagebroker/*")
            }
            'servlet-mapping' {
                'servlet-name'("FlexForbiddenServlet")
                'url-pattern'("*.as")
            }
            'servlet-mapping' {
                'servlet-name'("FlexForbiddenServlet")
                'url-pattern'("*.swc")
            }
            if (webtierCompilerEnabled) {
                'servlet-mapping' {
                    'servlet-name'("FlexMxmlServlet")
                    'url-pattern'("*.mxml")
                }
                'servlet-mapping' {
                    'servlet-name'("FlexSwfServlet")
                    'url-pattern'("*.swf")
                }
                'servlet-mapping' {
                    'servlet-name'("FlexInternalServlet")
                    'url-pattern'("/flex-internal/*")
                }
            }
            else {
                'servlet-mapping' {
                    'servlet-name'("FlexForbiddenServlet")
                    'url-pattern'("*.mxml")
                }
            }
        }
    }
    
    def onChange = { event ->
        if (event.source) { 
            def serviceClass = application.getServiceClass(event.source?.name)
            if (FlexUtils.hasFlexRemotingConvention(serviceClass)) {
                def messageBroker = MessageBroker.getMessageBroker(null)
                def remotingService = FlexUtils.getGrailsRemotingService(messageBroker)
                if (!remotingService.getDestination(serviceClass.propertyName)) {
                    FlexUtils.createRemotingDestination(messageBroker, serviceClass)
                }
            }
        }
    }                                                                                  
    
}
