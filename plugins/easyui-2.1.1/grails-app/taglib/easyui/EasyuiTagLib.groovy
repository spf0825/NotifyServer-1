package easyui
/**
 * @author xiao peng
 */
class EasyuiTagLib {
	static namespace = 'easyui'
	def path = { attrs, body ->
		out << "${createLinkTo(dir:pluginContextPath)}"
	}
}
