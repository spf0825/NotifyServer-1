import grails.converters.*;
class EasyuiController {
	def index = {
	}
    def blank={

    }
	def notejson={
		def props=System.getProperties()
		def notes=[
			["java.version":"${message(code:'app.system.props.javaversion')}"],
			["java.vendor":"${message(code:'app.system.props.javavendor')}"],
			["os.name":"${message(code:'app.system.props.osname')}"],
			["os.arch":"${message(code:'app.system.props.osarch')}"],
			["os.version":"${message(code:'app.system.props.osversion')}"]
			];
		def jsonMap=[:]
		jsonMap.total=5
		jsonMap.rows=[]
		notes.each{note->
			note.each{k,v->
				def map=[:];
				map.name=v;
				map.value=props.getProperty(k);
				map.group="system";
				jsonMap.rows<<map;
			}
		}
		render jsonMap as JSON;
	}
	def controllerTree={
		def excludedController=[""];
		def jsonList=[];
		def amap=[:];
		amap.id="all";
		amap.text="${message(code:'app.system.controllertree.top.label')}";
		amap.state="open";
		amap.attributes=["type":"top"];
		def cList=[];
		grailsApplication.controllerClasses.sort { it.fullName }.each{c->
			if(!excludedController.contains(c.logicalPropertyName)){
				def cmap=[:];
				cmap.id=c.logicalPropertyName;
				cmap.text=c.fullName.split('\\.')[-1];
				//cmap.state="closed"; //"open"
				cmap.attributes=["type":"controller"];
				//cmap.children=[];
				cList << cmap;
			}
		}
		amap.children=cList;
		jsonList << amap;
		render jsonList as JSON;
	}
	
	def systemi18njson={
		/*		def i18ns=[['en':"${message(code:'app.system.i18n.en.label')}"],
				['zh_CN':"${message(code:'app.system.i18n.zh_CN.label')}"],
				['da':"${message(code:'app.system.i18n.da.label')}"],['de':"${message(code:'app.system.i18n.de.label')}"],
				['es':"${message(code:'app.system.i18n.es.label')}"],['fr':"${message(code:'app.system.i18n.fr.label')}"],
				['it':"${message(code:'app.system.i18n.it.label')}"],['ja':"${message(code:'app.system.i18n.ja.label')}"],
				['nl':"${message(code:'app.system.i18n.nl.label')}"],['pt_BR':"${message(code:'app.system.i18n.pt_BR.label')}"],
				['pt_PT':"${message(code:'app.system.i18n.pt_PT.label')}"],['ru':"${message(code:'app.system.i18n.ru.label')}"],
				['th':"${message(code:'app.system.i18n.th.label')}"]
				];*/
		/*		def i18ns=[['en':"<img src='${request.contextPath}/images/flag_en.gif'/>${message(code:'app.system.i18n.en.label')}"],
				['zh_CN':"<img src='${request.contextPath}/images/flag_zh_CN.gif'/>${message(code:'app.system.i18n.zh_CN.label')}"]
				];*/
				def i18ns=[['en':"${message(code:'app.system.i18n.en.label')}"],['zh_CN':"${message(code:'app.system.i18n.zh_CN.label')}"]];
				def listMap=[];
				i18ns.each{i18n->
					i18n.each{k,v->
						def newMap=[:];
						newMap.id=k;
						newMap.text=v;
						listMap<<newMap;
					}
				}
				render listMap as JSON;
			}
	def changeLang={
		def jsonMap=[:]
		jsonMap.result='ok'
		render jsonMap as JSON;
	}
	
	def systemskinjson={
        def skins=[['default':"${message(code:'app.system.skin.default.label')}"],['gray':"${message(code:'app.system.skin.gray.label')}"],['cupertino':'银灰'],['pepper-grinder':'浅蓝'],['sunny':'橙黄'],
                ['metro-blue':'Metro-Blue'],['metro-gray':'Metro-Gray'],['metro-green':'Metro-Green'],['metro-orange':'Metro-Grange'],['metro-red':'Metro-Red']];
        def listMap=[];
        skins.each{skin->
            skin.each{k,v->
                def newMap=[:];
                newMap.id=k;
                newMap.text=v;
                listMap<<newMap;
            }
        }
        render listMap as JSON;
	}
	//更换系统皮肤
	def changeSkin={
	   session.skin=params.skin
	   def jsonMap=[:]
	   jsonMap.result='ok'
	   render jsonMap as JSON;
	 }
	
}
