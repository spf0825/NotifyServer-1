package com.bjrxht.notify


import grails.converters.*;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import java.sql.*;
class BankController {

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
/*		def bankInstanceTotal=0;
		if(params.searchField && params.searchValue){
			if(['java.lang.String','java.lang.Character'].contains(Bank.getDeclaredField("${params.searchField}").type.name)){
				bankInstanceTotal=Bank.invokeMethod("countBy${params.searchField.capitalize()}Ilike","%${params.searchValue}%")
			}else{
				if('java.lang.Integer'==Bank.getDeclaredField("${params.searchField}").type.name) 
				bankInstanceTotal=Bank.invokeMethod("countBy${params.searchField.capitalize()}InList",[params.searchValue.toInteger()]);
				if('java.lang.Long'==Bank.getDeclaredField("${params.searchField}").type.name) 
				bankInstanceTotal=Bank.invokeMethod("countBy${params.searchField.capitalize()}InList",[params.searchValue.toLong()]);
			}
			
		}else{
			bankInstanceTotal=Bank.count();
		}
		[bankInstanceTotal: bankInstanceTotal]*/
	}
	def json={
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
		def jsonMap=[:]
		def bankInstanceTotal=0;
		def bankInstances=[];
        println Bank.list().size();
		if(params.searchField && params.searchValue){
			bankInstances=Bank.createCriteria().list{
				order(params.sort,params.order)
				maxResults(params.max.toInteger())
				firstResult(params.offset.toInteger())
				if(['java.lang.String','java.lang.Character'].contains(Bank.getDeclaredField("${params.searchField}").type.name)){
					ilike(params.searchField, "%${params.searchValue}%")
				}else{
					if('java.lang.Integer'==Bank.getDeclaredField("${params.searchField}").type.name)
					eq(params.searchField,params.searchValue.toInteger());
					if('java.lang.Long'==Bank.getDeclaredField("${params.searchField}").type.name)
					eq(params.searchField,params.searchValue.toLong());
				}
			}
            bankInstanceTotal=Bank.createCriteria().count{
                if(['java.lang.String','java.lang.Character'].contains(Bank.getDeclaredField("${params.searchField}").type.name)){
                    ilike(params.searchField, "%${params.searchValue}%")
                }else{
                    if('java.lang.Integer'==Bank.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toInteger());
                    if('java.lang.Long'==Bank.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toLong());
                }
            }
		}else{
			bankInstanceTotal=Bank.count();
			bankInstances=Bank.list(params);
		}
		jsonMap.total=bankInstanceTotal;
		jsonMap.rows=bankInstances;
		render jsonMap as JSON
	}
	def inList={
		def listMap=[]
		if(params.field){
            ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().find{it.name==params.field}.each{p->
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
					(new Bank()).constraints."${params.field}"?.inList.eachWithIndex{obj,i->
						def map=[:]
						map.id=obj;
						map.text=obj;
						listMap<<map
					}
				}			
			}
		}else{
			def bankInstances=Bank.list([sort:'id',order:'asc'])
			bankInstances.each{
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
        ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().each{p->
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
		def bankInstance = new Bank(params)
		
		dateMap.each{k,v->
			bankInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
		}
		sqldateMap.each{k,v->
			bankInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
		}
		calendarMap.each{k,v->
			def gc=new GregorianCalendar();
			gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
			bankInstance."${k}"=gc;
		}
		timeMap.each{k,v->
			bankInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		timestampMap.each{k,v->
			bankInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		if (bankInstance.save(flush: true)) {
			map.message = "${message(code: 'default.created.message', args: [message(code: 'bank.label', default: 'Bank'), bankInstance.id])}"
			map.result=true
		}
		else {
			def messageSource = grailsApplication.getMainContext().getBean("messageSource")
            def annotation=Bank.getAnnotation(Title);
			bankInstance.errors.getAllErrors().each{error->
				if(error.class.name=='org.springframework.validation.FieldError'){					
					def args=error.getArguments()
                    if(args.size()>0){
                        //args[0]=message(code:"bankInstance.${error.field}.label", default: "${error.field}")
                        def fieldAnnotation=Bank.getDeclaredField(error.field).getAnnotation(Title);
                        if (fieldAnnotation.zh_CN()){
                            args[0]= fieldAnnotation.zh_CN()
                        }
                    }
                    if(args.size()>1){
                        //args[1]=message(code:"bankInstance.label", default: 'Bank')
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
		def bankInstance = Bank.get(params.id)
		if (bankInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (bankInstance.version > version) {					
					bankInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'bank.label', default: 'Bank')] as Object[], "Another user has updated this Bank while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().each{p->
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
			
			bankInstance.properties = params
			dateMap.each{k,v->
				bankInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				bankInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				bankInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				bankInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				bankInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						bankInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!bankInstance.hasErrors() && bankInstance.save(flush: true)) {
				
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'bank.label', default: 'Bank'), bankInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Bank.getAnnotation(Title);
				bankInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"bankInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Bank.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"bankInstance.label", default: 'Bank')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'bank.label', default: 'Bank'), params.id])}"
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
		def bankInstance = Bank.get(params.id)
		if (bankInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (bankInstance.version > version) {
					bankInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'bank.label', default: 'Bank')] as Object[], "Another user has updated this Bank while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().each{p->
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
			
			bankInstance.properties = params
			dateMap.each{k,v->
				bankInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				bankInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				bankInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				bankInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				bankInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						bankInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!bankInstance.hasErrors() && bankInstance.save(flush: true)) {
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'bank.label', default: 'Bank'), bankInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Bank.getAnnotation(Title);
				bankInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
						if(args.size()>0){
							   //args[0]=message(code:"bankInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Bank.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
						}
						if(args.size()>1){
							//args[1]=message(code:"bankInstance.label", default: 'Bank')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'bank.label', default: 'Bank'), params.id])}"
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
		Bank.withTransaction { status ->
			ids.each{id->
				def bankInstance = Bank.get(id)
				if (bankInstance) {
					try {
						boolean hasManyChildren=false;
                        ApplicationHolder.application.getDomainClass(Bank.name).getPersistentProperties().toList().each{p->
							if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
								if(bankInstance."${p.name}".size()>0){
									hasManyChildren=true;
								}
							}
						}
						if(!hasManyChildren){
							bankInstance.delete(flush: true);
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
