package com.bjrxht.notify


import grails.converters.*;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import java.sql.*;
class PhoneController {

    static allowedMethods = [ajaxInsert: "POST", ajaxUpdate: "POST", ajaxDelete: "POST"]

    def index = {
        redirect(action: "ajaxList", params: params)
    }

	/*
	 *     integrate easyui action                     
	 */
    
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
/*		def phoneInstanceTotal=0;
		if(params.searchField && params.searchValue){
			if(['java.lang.String','java.lang.Character'].contains(Phone.getDeclaredField("${params.searchField}").type.name)){
				phoneInstanceTotal=Phone.invokeMethod("countBy${params.searchField.capitalize()}Ilike","%${params.searchValue}%")
			}else{
				if('java.lang.Integer'==Phone.getDeclaredField("${params.searchField}").type.name) 
				phoneInstanceTotal=Phone.invokeMethod("countBy${params.searchField.capitalize()}InList",[params.searchValue.toInteger()]);
				if('java.lang.Long'==Phone.getDeclaredField("${params.searchField}").type.name) 
				phoneInstanceTotal=Phone.invokeMethod("countBy${params.searchField.capitalize()}InList",[params.searchValue.toLong()]);
			}
			
		}else{
			phoneInstanceTotal=Phone.count();
		}
		[phoneInstanceTotal: phoneInstanceTotal]*/
	}
	def json={
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
		def jsonMap=[:]
		def phoneInstanceTotal=0;
		def phoneInstances=[];		
		if(params.searchField && params.searchValue){
			phoneInstances=Phone.createCriteria().list{
				order(params.sort,params.order)
				maxResults(params.max.toInteger())
				firstResult(params.offset.toInteger())
				if(['java.lang.String','java.lang.Character'].contains(Phone.getDeclaredField("${params.searchField}").type.name)){
					ilike(params.searchField, "%${params.searchValue}%")
				}else{
					if('java.lang.Integer'==Phone.getDeclaredField("${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toInteger());
					if('java.lang.Long'==Phone.getDeclaredField("${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toLong());					
				}				
			}
            phoneInstanceTotal=Phone.createCriteria().count{
                if(['java.lang.String','java.lang.Character'].contains(Phone.getDeclaredField("${params.searchField}").type.name)){
                    ilike(params.searchField, "%${params.searchValue}%")
                }else{
                    if('java.lang.Integer'==Phone.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toInteger());
                    if('java.lang.Long'==Phone.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toLong());
                }
            }

        }else{
			phoneInstanceTotal=Phone.count();
			phoneInstances=Phone.list(params);
		}
		jsonMap.total=phoneInstanceTotal;
		jsonMap.rows=phoneInstances;
		render jsonMap as JSON
	}
	def inList={
		def listMap=[]
		if(params.field){
            ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().find{it.name==params.field}.each{p->
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
					(new Phone()).constraints."${params.field}"?.inList.eachWithIndex{obj,i->
						def map=[:]
						map.id=obj;
						map.text=obj;
						listMap<<map
					}
				}			
			}
		}else{
			def phoneInstances=Phone.list([sort:'id',order:'asc'])
			phoneInstances.each{
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
        ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().each{p->
			if(p.type == java.util.Date.class){
				if(params."${p.name}"){
					dateMap."${p.name}"=params."${p.name}";
					params."${p.name}"='';
				}
			}
			if(p.type == java.sql.Date.class){
				if(params."${p.name}"){
					sqldateMap."${p.name}"=params."${p.name}";
					params."${p.name}"='';
				}
			}
			if(p.type == java.util.Calendar.class){
				if(params."${p.name}"){
					calendarMap."${p.name}"=params."${p.name}";
					params."${p.name}"='';
				}
			}
			if(p.type == java.sql.Time.class){
				if(params."${p.name}"){
					timeMap."${p.name}"=params."${p.name}";
					params."${p.name}"='';
				}				
			}
			if(p.type == java.sql.Timestamp.class){
				if(params."${p.name}"){
					timestampMap."${p.name}"=params."${p.name}";
					params."${p.name}"='';
				}
			}
		}
		def phoneInstance = new Phone(params)
		
				if(params."checkbox_accounts"){
                    if (params."checkbox_accounts" instanceof String){
                        phoneInstance.addToAccounts(Account.get(params."checkbox_accounts".toLong()));
                    }else{
                        params."checkbox_accounts".each{checkValue->
                            phoneInstance.addToAccounts(Account.get(checkValue.toLong()));
                        }
                    }
				}
			
		dateMap.each{k,v->
			phoneInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
		}
		sqldateMap.each{k,v->
			phoneInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
		}
		calendarMap.each{k,v->
			def gc=new GregorianCalendar();
			gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
			phoneInstance."${k}"=gc;
		}
		timeMap.each{k,v->
			phoneInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		timestampMap.each{k,v->
			phoneInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		if (phoneInstance.save(flush: true)) {
			map.message = "${message(code: 'default.created.message', args: [message(code: 'phone.label', default: 'Phone'), phoneInstance.id])}"
			map.result=true
		}
		else {
			def messageSource = grailsApplication.getMainContext().getBean("messageSource")
            def annotation=Phone.getAnnotation(Title);
			phoneInstance.errors.getAllErrors().each{error->
				if(error.class.name=='org.springframework.validation.FieldError'){					
					def args=error.getArguments()
                    if(args.size()>0){
                        //args[0]=message(code:"phoneInstance.${error.field}.label", default: "${error.field}")
                        def fieldAnnotation=Phone.getDeclaredField(error.field).getAnnotation(Title);
                        if (fieldAnnotation.zh_CN()){
                            args[0]= fieldAnnotation.zh_CN()
                        }
                    }
                    if(args.size()>1){
                        //args[1]=message(code:"phoneInstance.label", default: 'Phone')
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
		render "${map.result}:${map.message}"
	}
	//form submit 
	def ajaxUpdate={
		def map=[:];
		map.message='';
		map.id=params.id;
		def phoneInstance = Phone.get(params.id)
		if (phoneInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (phoneInstance.version > version) {					
					phoneInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'phone.label', default: 'Phone')] as Object[], "Another user has updated this Phone while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().each{p->
				if(p.type == java.util.Date.class){
					if(params."${p.name}"){
						dateMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.sql.Date.class){
					if(params."${p.name}"){
						sqldateMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.util.Calendar.class){
					if(params."${p.name}"){
						calendarMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.sql.Time.class){
					if(params."${p.name}"){
						timeMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.sql.Timestamp.class){
					if(params."${p.name}"){
						timestampMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
			}
			
			phoneInstance.properties = params
			dateMap.each{k,v->
				phoneInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				phoneInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				phoneInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				phoneInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				phoneInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						phoneInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!phoneInstance.hasErrors() && phoneInstance.save(flush: true)) {
				
                        if(params."checkbox_accounts"){
                            if (params."checkbox_accounts" instanceof String){
                                phoneInstance.addToAccounts(Account.get(params."checkbox_accounts".toLong()));
                            }else{
                                params."checkbox_accounts".each{checkValue->
                                    phoneInstance.addToAccounts(Account.get(checkValue.toLong()));
                                }
                            }
                        }
					
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'phone.label', default: 'Phone'), phoneInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Phone.getAnnotation(Title);
				phoneInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"phoneInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Phone.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"phoneInstance.label", default: 'Phone')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'phone.label', default: 'Phone'), params.id])}"
			map.result=false
		}
		
		//render map as JSON
		render "${map.result}:${map.message}:${map.id}"
	}
	//one Row Update
	def ajaxRowUpdate={
		def map=[:];
		map.message='';
		map.id=params.id;
		def phoneInstance = Phone.get(params.id)
		if (phoneInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (phoneInstance.version > version) {
					phoneInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'phone.label', default: 'Phone')] as Object[], "Another user has updated this Phone while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().each{p->
				if(p.type == java.util.Date.class){
					if(params."${p.name}"){
						dateMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.sql.Date.class){
					if(params."${p.name}"){
						sqldateMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.util.Calendar.class){
					if(params."${p.name}"){
						calendarMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.sql.Time.class){
					if(params."${p.name}"){
						timeMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
				if(p.type == java.sql.Timestamp.class){
					if(params."${p.name}"){
						timestampMap."${p.name}"=params."${p.name}";
						params."${p.name}"='';
					}
				}
			}
			
			phoneInstance.properties = params
			dateMap.each{k,v->
				phoneInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				phoneInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				phoneInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				phoneInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				phoneInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						phoneInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!phoneInstance.hasErrors() && phoneInstance.save(flush: true)) {
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'phone.label', default: 'Phone'), phoneInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Phone.getAnnotation(Title);
				phoneInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
						if(args.size()>0){
							   //args[0]=message(code:"phoneInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Phone.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
						}
						if(args.size()>1){
							//args[1]=message(code:"phoneInstance.label", default: 'Phone')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'phone.label', default: 'Phone'), params.id])}"
			map.result=false
		}
		
		render map as JSON
		//render "${map.result}:${map.message}"
	}
	//批量删除
	def ajaxDeletes={
		def ids=params.fileIds?.split(',').toList().collect{it.toLong()};
		boolean allSucess=true;
		String wrongIds='';
		Phone.withTransaction { status ->
			ids.each{id->
				def phoneInstance = Phone.get(id)
				if (phoneInstance) {
					try {
						boolean hasManyChildren=false;
                        ApplicationHolder.application.getDomainClass(Phone.name).getPersistentProperties().toList().each{p->
							if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
								if(phoneInstance."${p.name}".size()>0){
									hasManyChildren=true;
								}
							}
						}
						if(!hasManyChildren){
							phoneInstance.delete(flush: true);
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
		map.message = "${message(code: 'easyui.controller.delete.fault.message')} ${wrongIds}"
		if(allSucess)map.message = "${message(code: 'easyui.controller.delete.sucess.message')}"
		map.result=allSucess
		render map as JSON
	}



}
