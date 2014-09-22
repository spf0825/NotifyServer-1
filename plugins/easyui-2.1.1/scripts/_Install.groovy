//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'ant' to access a global instance of AntBuilder
//
// For example you can create directory under project tree:
//
//    ant.mkdir(dir:"${basedir}/grails-app/jobs")
//

ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"
Ant.copy(
	file:"${easyuiPluginDir}/grails-app/i18n/easyui-messages.properties",
	tofile:"${basedir}/grails-app/i18n/easyui-messages.properties",
	overwrite: true
	);
Ant.copy(
	file:"${easyuiPluginDir}/grails-app/i18n/easyui-messages_zh_CN.properties",
	tofile:"${basedir}/grails-app/i18n/easyui-messages_zh_CN.properties",
	overwrite: true
	);
//overwrite = false
//copyGrailsResources("${basedir}/grails-app/i18n", "${easyuiPluginDir}/grails-app/i18n/*", overwrite)
/*Ant.copy(
		file:"${easyuiPluginDir}/grails-app/views/layouts/easyui.gsp",
		tofile:"${basedir}/grails-app/views/layouts/easyui.gsp",
		overwrite: true
		)*/
/*		
copyGrailsResources("${basedir}/web-app/js/easyui", "${easyuiPluginDir}/web-app/js/easyui/*", overwrite)

targetDir = "${basedir}/src/templates"
overwrite = false
if (new File(targetDir).exists()) {
}
else {
	ant.mkdir(dir: targetDir)
}
copyGrailsResources("$targetDir/easyui", "src/grails/templates/easyui/*", overwrite)
*/