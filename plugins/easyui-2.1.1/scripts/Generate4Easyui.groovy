/*
 * Copyright 2004-2005 the original author or authors.
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

/**
 * Gant script that generates a CRUD controller and matching views for a given domain class
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */
import org.codehaus.groovy.grails.scaffolding.EasyuiGrailsTemplateGenerator
import grails.util.GrailsNameUtils


includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsGenerate")

generateViews = true
generateController = true

target ('default': "Generates a CRUD interface (controller + views) for a domain class") {
    depends(checkVersion, parseArguments, packageApp)
    promptForName(type: "Domain Class")
	//pluginContextPath ${easyuiPluginDir}
    try {
        def name = argsMap["params"][0]
        if (!name || name == "*") {
            //uberGenerate()			
        }
        else {
            generateForName = name
            generateForOne()
        }
    }
    catch (Exception e) {
        logError("Error running generate-all", e)
        exit(1)
    }
}
target(generateForOne: "Generates controllers and views for only one domain class.") {
	depends(loadApp)
	
	def name = generateForName
	name = name.indexOf('.') > -1 ? name : GrailsNameUtils.getClassNameRepresentation(name)
	def domainClass = grailsApp.getDomainClass(name)
	
	if (!domainClass) {
		println "Domain class not found in grails-app/domain, trying hibernate mapped classes..."
		bootstrap()
		domainClass = grailsApp.getDomainClass(name)
	}
	
	if (domainClass) {
		generateForDomainClass(domainClass)
		event("StatusFinal", ["Finished generation for domain class ${domainClass.fullName}"])
	}
	else {
		event("StatusFinal", ["No domain class found for name ${name}. Please try again and enter a valid domain class name"])
		exit(1)
	}
}
def generateForDomainClass(domainClass) {
	def templateGenerator = new EasyuiGrailsTemplateGenerator(classLoader)
	templateGenerator.pluginContextPath=easyuiPluginDir
	if (generateViews) {
		event("StatusUpdate", ["Generating easyui views for domain class ${domainClass.fullName}"])
		//templateGenerator.generateViews(domainClass, basedir)
        templateGenerator.generateViews(domainClass, basedir,['ajaxList','js'])
		event("GenerateViewsEnd", [domainClass.fullName])
	}
	
	if (generateController) {
		event("StatusUpdate", ["Generating easyui controller for domain class ${domainClass.fullName}"])
		//templateGenerator.generateController(domainClass, basedir)
        templateGenerator.generateController(domainClass, basedir,'Controller')
		//createUnitTest(name: domainClass.fullName, suffix: "Controller",superClass: "ControllerUnitTestCase")
		event("GenerateControllerEnd", [domainClass.fullName])
	}
}
