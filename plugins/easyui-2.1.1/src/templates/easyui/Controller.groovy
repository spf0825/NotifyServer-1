<%=packageName ? "package ${packageName}\n\n" : ''%>
import grails.converters.*;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import java.sql.*;
class ${className}Controller {

    static allowedMethods = [ajaxInsert: "POST", ajaxUpdate: "POST", ajaxDelete: "POST"]

    def index = {
        redirect(action: "ajaxList", params: params)
    }

	/*
	 *     integrate easyui action                     
	 */
    <%
	
	private String captitalStr(String str){
		return str[0].toUpperCase()+str[1..-1]
		//log.error org.codehaus.groovy.runtime.InvokerHelper.getVersion()
/*		def versionList=groovy.lang.GroovySystem.getVersion().split("\\\\.").toList();
		if(versionList[2].toInteger()<7 || (versionList[2].toInteger()==7 && versionList[3].toInteger()<3)){
			return str[0].toUpperCase()+str[1..-1]
		}else{
			return str.capitalize()
		}*/
	}
	
	%>
	def js={
		params.max = Math.min(params.max ? params.max.toLong() : 10, 100)
		if(!params.maxList) {
			params.maxList=(1..5).collect{it*params.max}
		}
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
	}
	def ajaxList={
		params.max = Math.min(params.max ? params.max.toLong() : 10, 100)
		if(!params.maxList) {
			params.maxList=(1..5).collect{it*params.max}
		}
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
/*		def ${propertyName}Total=0;
		if(params.searchField && params.searchValue){
			if(['java.lang.String','java.lang.Character'].contains(${className}.getDeclaredField("\${params.searchField}").type.name)){
				${propertyName}Total=${className}.invokeMethod("countBy\${params.searchField.capitalize()}Ilike","%\${params.searchValue}%")
			}else{
				if('java.lang.Integer'==${className}.getDeclaredField("\${params.searchField}").type.name) 
				${propertyName}Total=${className}.invokeMethod("countBy\${params.searchField.capitalize()}InList",[params.searchValue.toInteger()]);
				if('java.lang.Long'==${className}.getDeclaredField("\${params.searchField}").type.name) 
				${propertyName}Total=${className}.invokeMethod("countBy\${params.searchField.capitalize()}InList",[params.searchValue.toLong()]);
			}
			
		}else{
			${propertyName}Total=${className}.count();
		}
		[${propertyName}Total: ${propertyName}Total]*/
	}
	def json={
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
		def jsonMap=[:]
		def ${propertyName}Total=0;
		def ${propertyName}s=[];		
		if(params.searchField && params.searchValue){
			${propertyName}s=${className}.createCriteria().list{
				order(params.sort,params.order)
				maxResults(params.max.toInteger())
				firstResult(params.offset.toInteger())
				if(['java.lang.String','java.lang.Character'].contains(${className}.getDeclaredField("\${params.searchField}").type.name)){
					ilike(params.searchField, "%\${params.searchValue}%")
				}else{
					if('java.lang.Integer'==${className}.getDeclaredField("\${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toInteger());
					if('java.lang.Long'==${className}.getDeclaredField("\${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toLong());					
				}				
			}
			if(['java.lang.String','java.lang.Character'].contains(${className}.getDeclaredField("\${params.searchField}").type.name)){
				${propertyName}Total=${className}.invokeMethod("countBy\${captitalStr(params.searchField)}Ilike","%\${params.searchValue}%")
			}else{
				if('java.lang.Integer'==${className}.getDeclaredField("\${params.searchField}").type.name) 
				${propertyName}Total=${className}.invokeMethod("countBy\${captitalStr(params.searchField)}InList",[params.searchValue.toInteger()]);
				if('java.lang.Long'==${className}.getDeclaredField("\${params.searchField}").type.name) 
				${propertyName}Total=${className}.invokeMethod("countBy\${captitalStr(params.searchField)}InList",[params.searchValue.toLong()]);
			}			
		}else{
			${propertyName}Total=${className}.count();
			${propertyName}s=${className}.list(params);
		}
		jsonMap.total=${propertyName}Total;
		jsonMap.rows=${propertyName}s;
		render jsonMap as JSON
	}
	def inList={
		def listMap=[]
		if(params.field){
            ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().find{it.name==params.field}.each{p->
				if(p.isAssociation()){
					if(p.oneToMany || p.manyToMany){
						
					}
					if (p.manyToOne || p.oneToOne){
							def objs=p.type.list([sort:'id',order:'asc'])
							objs.each{
								def map=[:]
								map.id=it.id
								map.text=it.toString()
								listMap<<map
							}
					}
					
				}else{
					(new ${className}()).constraints."\${params.field}"?.inList.eachWithIndex{obj,i->
						def map=[:]
						map.id=obj;
						map.text=obj;
						listMap<<map
					}
				}			
			}
		}else{
			def ${propertyName}s=${className}.list([sort:'id',order:'asc'])
			${propertyName}s.each{
				def map=[:]
				map.id=it.id
				map.text=it.toString()
				listMap<<map
			}
		}
		render listMap as JSON
	}
	def ajaxInsert={
		def map=[:];
		map.message='';
		def dateMap=[:];
		def sqldateMap=[:];
		def calendarMap=[:];
		def timeMap=[:];
		def timestampMap=[:];
        ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().each{p->
			if(p.type == java.util.Date.class){
				if(params."\${p.name}"){
					dateMap."\${p.name}"=params."\${p.name}";
					params."\${p.name}"='';
				}
			}
			if(p.type == java.sql.Date.class){
				if(params."\${p.name}"){
					sqldateMap."\${p.name}"=params."\${p.name}";
					params."\${p.name}"='';
				}
			}
			if(p.type == java.util.Calendar.class){
				if(params."\${p.name}"){
					calendarMap."\${p.name}"=params."\${p.name}";
					params."\${p.name}"='';
				}
			}
			if(p.type == java.sql.Time.class){
				if(params."\${p.name}"){
					timeMap."\${p.name}"=params."\${p.name}";
					params."\${p.name}"='';
				}				
			}
			if(p.type == java.sql.Timestamp.class){
				if(params."\${p.name}"){
					timestampMap."\${p.name}"=params."\${p.name}";
					params."\${p.name}"='';
				}
			}
		}
		def ${propertyName} = new ${className}(params)
		<%
		domainClass.getPersistentProperties().toList().each{p->
			if(p.isAssociation() && (p.oneToMany || p.manyToMany)){%>
				if(params."checkbox_${p.name}"){
                    if (params."checkbox_${p.name}" instanceof String){
                        ${propertyName}.addTo${captitalStr(p.name)}(${p.referencedDomainClass?.name}.get(params."checkbox_${p.name}".toLong()));
                    }else{
                        params."checkbox_${p.name}".each{checkValue->
                            ${propertyName}.addTo${captitalStr(p.name)}(${p.referencedDomainClass?.name}.get(checkValue.toLong()));
                        }
                    }
				}
			<%}
			if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){%>
				def ${p.name}Obj=request.getFile('${p.name}');
				if(${p.name}Obj && !${p.name}Obj.empty){
					${propertyName}.${p.name}=${p.name}Obj.bytes;
					${propertyName}.${p.name}FileName=${p.name}Obj.originalFilename;
				}else{
					${propertyName}.${p.name}=null;
				}
			<%}
		}
		%>
		dateMap.each{k,v->
			${propertyName}."\${k}"=java.util.Date.parse('yyyy-MM-dd',v);
		}
		sqldateMap.each{k,v->
			${propertyName}."\${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
		}
		calendarMap.each{k,v->
			def gc=new GregorianCalendar();
			gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
			${propertyName}."\${k}"=gc;
		}
		timeMap.each{k,v->
			${propertyName}."\${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		timestampMap.each{k,v->
			${propertyName}."\${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		if (${propertyName}.save(flush: true)) {
			map.message = "\${message(code: 'default.created.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])}"
			map.result=true
		}
		else {
			def messageSource = grailsApplication.getMainContext().getBean("messageSource")
            def annotation=${className}.getAnnotation(Title);
			${propertyName}.errors.getAllErrors().each{error->
				if(error.class.name=='org.springframework.validation.FieldError'){					
					def args=error.getArguments()
                    if(args.size()>0){
                        //args[0]=message(code:"${propertyName}.\${error.field}.label", default: "\${error.field}")
                        def fieldAnnotation=${className}.getDeclaredField(error.field).getAnnotation(Title);
                        if (fieldAnnotation.zh_CN()){
                            args[0]= fieldAnnotation.zh_CN()
                        }
                    }
                    if(args.size()>1){
                        //args[1]=message(code:"${propertyName}.label", default: '${className}')
                        if(annotation?.zh_CN()){
                            def title= annotation?.zh_CN()
                        }
                    }
					def newerror=new org.springframework.validation.FieldError(error.objectName,error.field,error.rejectedValue,error.isBindingFailure(),error.codes,args,error.defaultMessage)
					map.message = map.message + messageSource.getMessage(newerror,org.springframework.web.servlet.support.RequestContextUtils.getLocale(request))+'<p>'
				}
			}
			map.result=false
		}
		
		//render map as JSON
		render "\${map.result}:\${map.message}"
	}
	//form submit 
	def ajaxUpdate={
		def map=[:];
		map.message='';
		map.id=params.id;
		def ${propertyName} = ${className}.get(params.id)
		if (${propertyName}) {
			if (params.version) {
				def version = params.version.toLong()
				if (${propertyName}.version > version) {					
					${propertyName}.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: '${domainClass.propertyName}.label', default: '${className}')] as Object[], "Another user has updated this ${className} while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().each{p->
				if(p.type == java.util.Date.class){
					if(params."\${p.name}"){
						dateMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.sql.Date.class){
					if(params."\${p.name}"){
						sqldateMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.util.Calendar.class){
					if(params."\${p.name}"){
						calendarMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.sql.Time.class){
					if(params."\${p.name}"){
						timeMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.sql.Timestamp.class){
					if(params."\${p.name}"){
						timestampMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
			}
			<%
			domainClass.getPersistentProperties().toList().each{p->
				if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){%>
					def ${p.name}OldObj=${propertyName}.${p.name};
				<%}
			}
			%>
			${propertyName}.properties = params
			dateMap.each{k,v->
				${propertyName}."\${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				${propertyName}."\${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				${propertyName}."\${k}"=gc;
			}
			timeMap.each{k,v->
				${propertyName}."\${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				${propertyName}."\${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."\${p}"){
						${propertyName}."\${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."\${p}");
					}
				}
			}
			if (!${propertyName}.hasErrors() && ${propertyName}.save(flush: true)) {
				<%
				domainClass.getPersistentProperties().toList().each{p->
					if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
						if(p.manyToMany){%>
							${propertyName}.${p.name}.collect(it.id).each{newId->
								${propertyName}.removeFrom${captitalStr(p.name)}(${p.referencedDomainClass.name}.get(newId));
							}
						<%}%>
                        if(params."checkbox_${p.name}"){
                            if (params."checkbox_${p.name}" instanceof String){
                                ${propertyName}.addTo${captitalStr(p.name)}(${p.referencedDomainClass?.name}.get(params."checkbox_${p.name}".toLong()));
                            }else{
                                params."checkbox_${p.name}".each{checkValue->
                                    ${propertyName}.addTo${captitalStr(p.name)}(${p.referencedDomainClass?.name}.get(checkValue.toLong()));
                                }
                            }
                        }
					<%}
					if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){%>
					if(request.class.name=='org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest'){
						def ${p.name}Obj=request.getFile('${p.name}');
						if(${p.name}Obj && !${p.name}Obj.empty){
							${propertyName}.${p.name}=${p.name}Obj.bytes;
							${propertyName}.${p.name}FileName=${p.name}Obj.originalFilename;
						}else{
							${propertyName}.${p.name}=${p.name}OldObj;
						}
					}
				<%}
				}
				%>
				map.message = "\${message(code: 'default.updated.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=${className}.getAnnotation(Title);
				${propertyName}.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"${propertyName}.\${error.field}.label", default: "\${error.field}")
                            def fieldAnnotation=${className}.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"${propertyName}.label", default: '${className}')
                            if(annotation?.zh_CN()){
                                def title= annotation?.zh_CN()
                            }
                        }
						def newerror=new org.springframework.validation.FieldError(error.objectName,error.field,error.rejectedValue,error.isBindingFailure(),error.codes,args,error.defaultMessage)
						map.message = map.message + messageSource.getMessage(newerror,org.springframework.web.servlet.support.RequestContextUtils.getLocale(request))+'<p>'
					}
				}
				map.result=false
			}
		}
		else {
			map.message = "\${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
			map.result=false
		}
		
		//render map as JSON
		render "\${map.result}:\${map.message}:\${map.id}"
	}
	//one Row Update
	def ajaxRowUpdate={
		def map=[:];
		map.message='';
		map.id=params.id;
		def ${propertyName} = ${className}.get(params.id)
		if (${propertyName}) {
			if (params.version) {
				def version = params.version.toLong()
				if (${propertyName}.version > version) {
					${propertyName}.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: '${domainClass.propertyName}.label', default: '${className}')] as Object[], "Another user has updated this ${className} while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().each{p->
				if(p.type == java.util.Date.class){
					if(params."\${p.name}"){
						dateMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.sql.Date.class){
					if(params."\${p.name}"){
						sqldateMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.util.Calendar.class){
					if(params."\${p.name}"){
						calendarMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.sql.Time.class){
					if(params."\${p.name}"){
						timeMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
				if(p.type == java.sql.Timestamp.class){
					if(params."\${p.name}"){
						timestampMap."\${p.name}"=params."\${p.name}";
						params."\${p.name}"='';
					}
				}
			}
			<%
			domainClass.getPersistentProperties().toList().each{p->
				if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){%>
					def ${p.name}OldObj=${propertyName}.${p.name};
				<%}
			}
			%>
			${propertyName}.properties = params
			dateMap.each{k,v->
				${propertyName}."\${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				${propertyName}."\${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				${propertyName}."\${k}"=gc;
			}
			timeMap.each{k,v->
				${propertyName}."\${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				${propertyName}."\${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."\${p}"){
						${propertyName}."\${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."\${p}");
					}
				}
			}
			if (!${propertyName}.hasErrors() && ${propertyName}.save(flush: true)) {
				map.message = "\${message(code: 'default.updated.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), ${propertyName}.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=${className}.getAnnotation(Title);
				${propertyName}.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
						if(args.size()>0){
							   //args[0]=message(code:"${propertyName}.\${error.field}.label", default: "\${error.field}")
                            def fieldAnnotation=${className}.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
						}
						if(args.size()>1){
							//args[1]=message(code:"${propertyName}.label", default: '${className}')
                            if(annotation?.zh_CN()){
                                def title= annotation?.zh_CN()
                            }
						}
						def newerror=new org.springframework.validation.FieldError(error.objectName,error.field,error.rejectedValue,error.isBindingFailure(),error.codes,args,error.defaultMessage)
						map.message = map.message + messageSource.getMessage(newerror,org.springframework.web.servlet.support.RequestContextUtils.getLocale(request))+'<p>'
					}
				}
				map.result=false
			}
		}
		else {
			map.message = "\${message(code: 'default.not.found.message', args: [message(code: '${domainClass.propertyName}.label', default: '${className}'), params.id])}"
			map.result=false
		}
		
		render map as JSON
		//render "\${map.result}:\${map.message}"
	}
	//批量删除
	def ajaxDeletes={
		def ids=params.fileIds?.split(',').toList().collect{it.toLong()};
		boolean allSucess=true;
		String wrongIds='';
		${className}.withTransaction { status ->
			ids.each{id->
				def ${propertyName} = ${className}.get(id)
				if (${propertyName}) {
					try {
						boolean hasManyChildren=false;
                        ApplicationHolder.application.getDomainClass(${className}.name).getPersistentProperties().toList().each{p->
							if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
								if(${propertyName}."\${p.name}".size()>0){
									hasManyChildren=true;
								}
							}
						}
						if(!hasManyChildren){
							${propertyName}.delete(flush: true);
						}else{
							allSucess=false;
							wrongIds=wrongIds+id+',';
						}						
					}
					catch (org.springframework.dao.DataIntegrityViolationException e) {
						allSucess=false;
						wrongIds=wrongIds+id+',';
					}
				}
				else {
				}
			}
			//status.setRollbackOnly()
		}
		def map=[:];
		map.message = "\${message(code: 'easyui.controller.delete.fault.message')} \${wrongIds}"
		if(allSucess)map.message = "\${message(code: 'easyui.controller.delete.sucess.message')}"
		map.result=allSucess
		render map as JSON
	}



}
