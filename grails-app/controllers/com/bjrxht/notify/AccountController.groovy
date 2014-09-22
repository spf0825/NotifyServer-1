package com.bjrxht.notify


import grails.converters.*;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import java.sql.*
import org.codehaus.groovy.grails.web.json.JSONElement
import com.nisc.SecurityEngine
import com.bjrxht.sm9.SecurityHelper
import com.bjrxht.xmpp.GetPostUtil
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;
class AccountController {
     def springSecurityService;
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
    def accountJs = {
        params.max = Math.min(params.max ? params.max.toLong() : 10, 100)
        if(!params.maxList) {
            params.maxList=(1..5).collect{it*params.max}
        }
        if(!params.offset) params.offset ='0'
        if(!params.sort) params.sort ='id'
        if(!params.order) params.order ='asc'
    }
    def createAccount={}
    def save = {
        def map=[:];
        def account = Account.findByNumber(params.number?:'*');
        if (account){
          map.result = false;
          map.message = '账户已经存在！';
        } else{
            account = new Account();
            def  phone = Phone.findByNumber(params.phoneNumber?:'*');
            if (!phone){
                phone = new Phone(number: params.phoneNumber).save(flush: true);
            }
            account.number = params.number;
            account.realName = params.realName;
            account.phone =phone;
            account.bank = Bank.findByName('鹤壁银行');
            account.baseUser = springSecurityService.currentUser;
            if (account.save(flush: true)){
                map.result = true;
                map.message = '账户保存成功！';
            } else{
                println account.errors;
                map.result = false;
                map.message = '账户保存失败！';
            }
        }
        render "${map.result}:${map.message}" ;
    }
    def edit = {
        def map=[:];
        def account = Account.get(params.id?.toLong());
        if (!account){
          account = new Account();
        }
        def  phone = Phone.findByNumber(params.phoneNumber?:'*');
        if (phone){
            account.phone=phone;
        } else{
            account.phone = new Phone(params.phoneNumber).save(flush: true);
        }
        account.number = params.number;
        account.realName = params.realName;
        if (account.save(flush: true)){
            map.result = true;
            map.message = '账户保存成功！';
        } else{
            map.result = false;
            map.message = '账户保存失败！';
        }
        render "${map.result}:${map.message}"
    }
    def ajaxListAccount={
        if(SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN')){
            redirect(action: 'ajaxList');
        }
        params.max = Math.min(params.max ? params.max.toLong() : 10, 100)
        if(!params.maxList) {
            params.maxList=(1..5).collect{it*params.max}
        }
        if(!params.offset) params.offset ='0'
        if(!params.sort) params.sort ='id'
        if(!params.order) params.order ='asc'
    }
	def ajaxList={
        if(SpringSecurityUtils.ifAnyGranted('ROLE_INCASE')){
            redirect(action: 'ajaxListAccount');
        }
		params.max = Math.min(params.max ? params.max.toLong() : 10, 100)
		if(!params.maxList) {
			params.maxList=(1..5).collect{it*params.max}
		}
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'

	}
	def json={
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
		def jsonMap=[:]
		def accountInstanceTotal=0;
		def accountInstances=[];		
		if(params.searchField && params.searchValue){
			accountInstances=Account.createCriteria().list{
				order(params.sort,params.order)
				maxResults(params.max.toInteger())
				firstResult(params.offset.toInteger())
				if(['java.lang.String','java.lang.Character'].contains(Account.getDeclaredField("${params.searchField}").type.name)){
					ilike(params.searchField, "%${params.searchValue}%")
				}else{
					if('java.lang.Integer'==Account.getDeclaredField("${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toInteger());
					if('java.lang.Long'==Account.getDeclaredField("${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toLong());					
				}				
			}
            accountInstanceTotal=Account.createCriteria().count{
                if(['java.lang.String','java.lang.Character'].contains(Account.getDeclaredField("${params.searchField}").type.name)){
                    ilike(params.searchField, "%${params.searchValue}%")
                }else{
                    if('java.lang.Integer'==Account.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toInteger());
                    if('java.lang.Long'==Account.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toLong());
                }
            }
		}else{
			accountInstanceTotal=Account.count();
			accountInstances=Account.list(params);
		}
		jsonMap.total=accountInstanceTotal;
		jsonMap.rows=accountInstances;
		render jsonMap as JSON
	}
	def inList={
		def listMap=[]
		if(params.field){
            ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().find{it.name==params.field}.each{p->
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
					(new Account()).constraints."${params.field}"?.inList.eachWithIndex{obj,i->
						def map=[:]
						map.id=obj;
						map.text=obj;
						listMap<<map
					}
				}			
			}
		}else{
			def accountInstances=Account.list([sort:'id',order:'asc'])
			accountInstances.each{
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
        ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().each{p->
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
		def accountInstance = new Account(params)
		
		dateMap.each{k,v->
			accountInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
		}
		sqldateMap.each{k,v->
			accountInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
		}
		calendarMap.each{k,v->
			def gc=new GregorianCalendar();
			gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
			accountInstance."${k}"=gc;
		}
		timeMap.each{k,v->
			accountInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		timestampMap.each{k,v->
			accountInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		if (accountInstance.save(flush: true)) {
			map.message = "${message(code: 'default.created.message', args: [message(code: 'account.label', default: 'Account'), accountInstance.id])}"
			map.result=true
		}
		else {
			def messageSource = grailsApplication.getMainContext().getBean("messageSource")
            def annotation=Account.getAnnotation(Title);
			accountInstance.errors.getAllErrors().each{error->
				if(error.class.name=='org.springframework.validation.FieldError'){					
					def args=error.getArguments()
                    if(args.size()>0){
                        //args[0]=message(code:"accountInstance.${error.field}.label", default: "${error.field}")
                        def fieldAnnotation=Account.getDeclaredField(error.field).getAnnotation(Title);
                        if (fieldAnnotation.zh_CN()){
                            args[0]= fieldAnnotation.zh_CN()
                        }
                    }
                    if(args.size()>1){
                        //args[1]=message(code:"accountInstance.label", default: 'Account')
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
		def accountInstance = Account.get(params.id)
		if (accountInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (accountInstance.version > version) {					
					accountInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'account.label', default: 'Account')] as Object[], "Another user has updated this Account while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().each{p->
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
			accountInstance.properties = params
			dateMap.each{k,v->
				accountInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				accountInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				accountInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				accountInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				accountInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						accountInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!accountInstance.hasErrors() && accountInstance.save(flush: true)) {
				
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'account.label', default: 'Account'), accountInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Account.getAnnotation(Title);
				accountInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"accountInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Account.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"accountInstance.label", default: 'Account')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'account.label', default: 'Account'), params.id])}"
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
		def accountInstance = Account.get(params.id)
		if (accountInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (accountInstance.version > version) {
					accountInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'account.label', default: 'Account')] as Object[], "Another user has updated this Account while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().each{p->
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
			
			accountInstance.properties = params
			dateMap.each{k,v->
				accountInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				accountInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				accountInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				accountInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				accountInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						accountInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!accountInstance.hasErrors() && accountInstance.save(flush: true)) {
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'account.label', default: 'Account'), accountInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Account.getAnnotation(Title);
				accountInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
						if(args.size()>0){
							   //args[0]=message(code:"accountInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Account.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
						}
						if(args.size()>1){
							//args[1]=message(code:"accountInstance.label", default: 'Account')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'account.label', default: 'Account'), params.id])}"
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
		Account.withTransaction { status ->
			ids.each{id->
				def accountInstance = Account.get(id)
				if (accountInstance) {
					try {
						boolean hasManyChildren=false;
                        ApplicationHolder.application.getDomainClass(Account.name).getPersistentProperties().toList().each{p->
							if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
								if(accountInstance."${p.name}".size()>0){
									hasManyChildren=true;
								}
							}
						}
						if(!hasManyChildren){
							accountInstance.delete(flush: true);
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
