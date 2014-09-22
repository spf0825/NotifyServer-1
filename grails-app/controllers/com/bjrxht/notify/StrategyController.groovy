package com.bjrxht.notify


import grails.converters.*;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import java.sql.*
import com.bjrxht.grails.annotation.Title;
class StrategyController {

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
	}
	def json={
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		if(!params.offset) params.offset ='0'
		if(!params.sort) params.sort ='id'
		if(!params.order) params.order ='asc'
		def jsonMap=[:]
		def strategyInstanceTotal=0;
		def strategyInstances=[];		
		if(params.searchField && params.searchValue){
			strategyInstances=Strategy.createCriteria().list{
				order(params.sort,params.order)
				maxResults(params.max.toInteger())
				firstResult(params.offset.toInteger())
				if(['java.lang.String','java.lang.Character'].contains(Strategy.getDeclaredField("${params.searchField}").type.name)){
					ilike(params.searchField, "%${params.searchValue}%")
				}else{
					if('java.lang.Integer'==Strategy.getDeclaredField("${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toInteger());
					if('java.lang.Long'==Strategy.getDeclaredField("${params.searchField}").type.name) 
					eq(params.searchField,params.searchValue.toLong());					
				}				
			}
            strategyInstanceTotal=Strategy.createCriteria().count{
                if(['java.lang.String','java.lang.Character'].contains(Strategy.getDeclaredField("${params.searchField}").type.name)){
                    ilike(params.searchField, "%${params.searchValue}%")
                }else{
                    if('java.lang.Integer'==Strategy.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toInteger());
                    if('java.lang.Long'==Strategy.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toLong());
                }
            }

		}else{
			strategyInstanceTotal=Strategy.count();
			strategyInstances=Strategy.list(params);
		}
		jsonMap.total=strategyInstanceTotal;
		jsonMap.rows=strategyInstances;
		render jsonMap as JSON
	}
	def inList={
		def listMap=[]
		if(params.field){
            ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().find{it.name==params.field}.each{p->
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
					(new Strategy()).constraints."${params.field}"?.inList.eachWithIndex{obj,i->
						def map=[:]
						map.id=obj;
						map.text=obj;
						listMap<<map
					}
				}			
			}
		}else{
			def strategyInstances=Strategy.list([sort:'id',order:'asc'])
			strategyInstances.each{
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
        ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().each{p->
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
        if(params.min&&params.min!=""){
            params.min=params.min.toString().toFloat();
        } else{
            params.min=0.0f
        }
        if(params.min1&&params.min1!=""){
            params.min1=params.min1.toString().toFloat();
        } else{
            params.min1=0.0f
        }
        if(params.min2&&params.min2!=""){
            params.min2=params.min2.toString().toFloat();
        } else{
            params.min2=0.0f
        }
        if(params.min3&&params.min3!=""){
            params.min3=params.min3.toString().toFloat();
        } else{
            params.min3=0.0f
        }
        if(params.min4&&params.min4!=""){
            params.min4=params.min4.toString().toFloat();
        }else{
            params.min4=0.0f
        }
        if(params.max&&params.max!=""){
            params.max=params.max.toString().toFloat();
        }else{
            params.max=9999999.0f
        }
		def strategyInstance = new Strategy(params)

        dateMap.each{k,v->
			strategyInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
		}
		sqldateMap.each{k,v->
			strategyInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
		}
		calendarMap.each{k,v->
			def gc=new GregorianCalendar();
			gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
			strategyInstance."${k}"=gc;
		}
		timeMap.each{k,v->
			strategyInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		timestampMap.each{k,v->
			strategyInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
		}
		if (strategyInstance.save(flush: true)) {

			map.message = "${message(code: 'default.created.message', args: [message(code: 'strategy.label', default: 'Strategy'), strategyInstance.id])}"
			map.result=true
		}
		else {
            log.error strategyInstance.errors
             /*
			def messageSource = grailsApplication.getMainContext().getBean("messageSource")
            def annotation=Strategy.getAnnotation(Title);
			strategyInstance.errors.getAllErrors().each{error->
				if(error.class.name=='org.springframework.validation.FieldError'){					
					def args=error.getArguments()
                    if(args.size()>0){
                        //args[0]=message(code:"strategyInstance.${error.field}.label", default: "${error.field}")
                        //def fieldAnnotation=Strategy.getDeclaredField(error.field).getAnnotation(Title);
                        //if (fieldAnnotation.zh_CN()){
                        //    args[0]= fieldAnnotation.zh_CN()
                       // }
                    }
                    if(args.size()>1){
                        //args[1]=message(code:"strategyInstance.label", default: 'Strategy')
                       // if(annotation?.zh_CN()){
                        //    def title= annotation?.zh_CN()
                       // }
                    }
					//def newerror=new org.springframework.validation.FieldError(error.objectName,error.field,error.rejectedValue,error.isBindingFailure(),error.codes,args,error.defaultMessage)
					map.message = map.message + messageSource.getMessage(newerror,org.springframework.web.servlet.support.RequestContextUtils.getLocale(request))+'<p>'
				}
			}
            */
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
		def strategyInstance = Strategy.get(params.id)
		if (strategyInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (strategyInstance.version > version) {					
					strategyInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'strategy.label', default: 'Strategy')] as Object[], "Another user has updated this Strategy while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().each{p->
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
            if (!params.max||params.max==""){
                params.max=9999999.0f;
            }else{
                params.max=params.max.toString().toFloat();
            }
            if (!params.min ||params.min==""){
                params.min=0.0f;
            }else{
                params.min=params.min.toString().toFloat();
            }
            if(params.min1&&params.min1!=""){
                params.min1=params.min1.toString().toFloat();
            }else{
                params.min1=0.0f;
            }
            if(params.min2&&params.min2!=""){
                params.min2=params.min2.toString().toFloat();
            }else{
                params.min2=0.0f;
            }
            if(params.min3&&params.min3!=""){
                params.min3=params.min3.toString().toFloat();
            }else{
                params.min3=0.0f;
            }
            if(params.min4&&params.min4!=""){
                params.min4=params.min4.toString().toFloat();
            }else{
                params.min4=0.0f;
            }
			strategyInstance.properties = params
            if (params.type=='complex'){
              if (strategyInstance.isAuthorize1&&strategyInstance.isAuthorize2){
                  strategyInstance.min1=0.0f;
                  strategyInstance.min2=0.0f;
                  strategyInstance.min3=0.0f;
                  strategyInstance.min4=0.0f;
                  strategyInstance.phone1='';
                  strategyInstance.phone2='';
                  strategyInstance.phone3='';
                  strategyInstance.phone4='';
              }else{
                  map.result = false;
                  map.message = '未经确认手机授权不能改变策略';
                  render "${map.result}:${map.message}:${map.id}"
                  return;
              }
            }
			dateMap.each{k,v->
				strategyInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				strategyInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				strategyInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				strategyInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				strategyInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						strategyInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!strategyInstance.hasErrors() && strategyInstance.save(flush: true)) {
				
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'strategy.label', default: 'Strategy'), strategyInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Strategy.getAnnotation(Title);
				strategyInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"strategyInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Strategy.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"strategyInstance.label", default: 'Strategy')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'strategy.label', default: 'Strategy'), params.id])}"
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
		def strategyInstance = Strategy.get(params.id)
		if (strategyInstance) {
			if (params.version) {
				def version = params.version.toLong()
				if (strategyInstance.version > version) {
					strategyInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'strategy.label', default: 'Strategy')] as Object[], "Another user has updated this Strategy while you were editing")
					map.result=false
				}
			}
			def dateMap=[:];
			def sqldateMap=[:];
			def calendarMap=[:];
			def timeMap=[:];
			def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().each{p->
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
			strategyInstance.properties = params
			dateMap.each{k,v->
				strategyInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
			}
			sqldateMap.each{k,v->
				strategyInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
			}
			calendarMap.each{k,v->
				def gc=new GregorianCalendar();
				gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
				strategyInstance."${k}"=gc;
			}
			timeMap.each{k,v->
				strategyInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
			timestampMap.each{k,v->
				strategyInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
			}
            ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().each{p->
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
					if(params."${p}"){
						strategyInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
					}
				}
			}
			if (!strategyInstance.hasErrors() && strategyInstance.save(flush: true)) {
				map.message = "${message(code: 'default.updated.message', args: [message(code: 'strategy.label', default: 'Strategy'), strategyInstance.id])}"
				map.result=true
			}
			else {
				def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Strategy.getAnnotation(Title);
				strategyInstance.errors.getAllErrors().each{error->
					if(error.class.name=='org.springframework.validation.FieldError'){
						def args=error.getArguments()
						if(args.size()>0){
							   //args[0]=message(code:"strategyInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Strategy.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
						}
						if(args.size()>1){
							//args[1]=message(code:"strategyInstance.label", default: 'Strategy')
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
			map.message = "${message(code: 'default.not.found.message', args: [message(code: 'strategy.label', default: 'Strategy'), params.id])}"
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
		Strategy.withTransaction { status ->
			ids.each{id->
				def strategyInstance = Strategy.get(id)
				if (strategyInstance) {
					try {
						boolean hasManyChildren=false;
                        ApplicationHolder.application.getDomainClass(Strategy.name).getPersistentProperties().toList().each{p->
							if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
								if(strategyInstance."${p.name}".size()>0){
									hasManyChildren=true;
								}
							}
						}
						if(!hasManyChildren){
							strategyInstance.delete(flush: true);
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
