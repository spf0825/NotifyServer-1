package com.bjrxht.notify

import com.bjrxht.sm9.SecurityHelper
import com.bjrxht.xmpp.GetPostUtil
import grails.converters.*;
import org.codehaus.groovy.grails.web.converters.ConverterUtil;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import java.sql.*
import org.codehaus.groovy.grails.web.json.JSONElement;
import com.bjrxht.grails.annotation.Title
import com.basic.core.BaseUser;
import java.net.ServerSocket
import com.bjrxht.socket.SocketServerStatus;
import javax.jms.ConnectionFactory
import org.apache.activemq.spring.ActiveMQConnectionFactory
import groovy.jms.JMS
import javax.jms.Message
import javax.jms.MessageListener
import com.nisc.SecurityEngine
import com.nisc.SecurityEngineException
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils;
//确认消息控制类
class InfoController {
    def springSecurityService
    static allowedMethods = [ajaxInsert: "POST", ajaxUpdate: "POST", ajaxDelete: "POST"]

    def index = {
        redirect(action: "ajaxList", params: params)
    }
    def indexTo={
        if(SpringSecurityUtils.ifAnyGranted("ROLE_INCASE")){
            redirect(controller: 'account',action: 'ajaxListAccount',params: params);
        } else {
            redirect(action: 'ajaxList',params: params);
        }
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
        def infoInstanceTotal=0;
        def infoInstances=[];
        if(params.searchField && params.searchValue){
            infoInstances=Info.createCriteria().list{
                order(params.sort,params.order)
                maxResults(params.max.toInteger())
                firstResult(params.offset.toInteger())
                account{
                    or{
                        notEqual('status','注销');
                        isNull('status');
                    }

                }
                if(['java.lang.String','java.lang.Character'].contains(Info.getDeclaredField("${params.searchField}").type.name)){
                    ilike(params.searchField, "%${params.searchValue}%")
                }else{
                    if('java.lang.Integer'==Info.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toInteger());
                    if('java.lang.Long'==Info.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toLong());
                }

            }
            infoInstanceTotal = Info.createCriteria().count {
                account{
                    or{
                        notEqual('status','注销');
                        isNull('status');
                    }

                }
                if(['java.lang.String','java.lang.Character'].contains(Info.getDeclaredField("${params.searchField}").type.name)){
                    ilike(params.searchField, "%${params.searchValue}%")
                }else{
                    if('java.lang.Integer'==Info.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toInteger());
                    if('java.lang.Long'==Info.getDeclaredField("${params.searchField}").type.name)
                        eq(params.searchField,params.searchValue.toLong());
                }
            }
        }else{
            infoInstanceTotal=Info.createCriteria().count {
                account{
                    or{
                        notEqual('status','注销');
                        isNull('status');
                    }

                }
            };
            infoInstances=Info.createCriteria().list{
                order(params.sort,params.order)
                maxResults(params.max.toInteger())
                firstResult(params.offset.toInteger())
                account{
                    or{
                        notEqual('status','注销');
                        isNull('status');
                    }

                }
            }
        }
        jsonMap.total=infoInstanceTotal;
        jsonMap.rows=infoInstances;
        render jsonMap as JSON
    }
    def inList={
        def listMap=[]
        if(params.field){
            ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().find{it.name==params.field}.each{p->
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
                    (new Info()).constraints."${params.field}"?.inList.eachWithIndex{obj,i->
                        def map=[:]
                        map.id=obj;
                        map.text=obj;
                        listMap<<map
                    }
                }
            }
        }else{
            def infoInstances=Info.list([sort:'id',order:'asc'])
            infoInstances.each{
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
        ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().each{p->
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
        def infoInstance = new Info(params)

        dateMap.each{k,v->
            infoInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
        }
        sqldateMap.each{k,v->
            infoInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
        }
        calendarMap.each{k,v->
            def gc=new GregorianCalendar();
            gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
            infoInstance."${k}"=gc;
        }
        timeMap.each{k,v->
            infoInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
        }
        timestampMap.each{k,v->
            infoInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
        }
        infoInstance.money=infoInstance.money*100;
        if (infoInstance.save(flush: true)) {
            def json=null;
            if (!params.api){
                params.api="2"
            }
            if(params.api=="1"){
                json=sendInfo(infoInstance);
            }
            if(params.api=="2"){
                def currentUser = BaseUser.get(springSecurityService.principal.id);
                new TraceLog(operator: currentUser,operating:'发送交易确认信息',channel: 'web',params: params.toString()).save(flush:true);
                json=sendInfoWithSm9(infoInstance);
            }
            //println json.toString();
            map.message = "${message(code: 'default.created.message', args: [message(code: 'info.label', default: 'Info'), infoInstance.id])}"
            map.result=true
        }
        else {
            def messageSource = grailsApplication.getMainContext().getBean("messageSource")
            def annotation=Info.getAnnotation(Title);
            infoInstance.errors.getAllErrors().each{error->
                if(error.class.name=='org.springframework.validation.FieldError'){
                    def args=error.getArguments()
                    if(args.size()>0){
                        //args[0]=message(code:"infoInstance.${error.field}.label", default: "${error.field}")
                        def fieldAnnotation=Info.getDeclaredField(error.field).getAnnotation(Title);
                        if (fieldAnnotation.zh_CN()){
                            args[0]= fieldAnnotation.zh_CN()
                        }
                    }
                    if(args.size()>1){
                        //args[1]=message(code:"infoInstance.label", default: 'Info')
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
        def infoInstance = Info.get(params.id)
        if (infoInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (infoInstance.version > version) {
                    infoInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'info.label', default: 'Info')] as Object[], "Another user has updated this Info while you were editing")
                    map.result=false
                }
            }
            def dateMap=[:];
            def sqldateMap=[:];
            def calendarMap=[:];
            def timeMap=[:];
            def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().each{p->
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

            infoInstance.properties = params
            dateMap.each{k,v->
                infoInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
            }
            sqldateMap.each{k,v->
                infoInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
            }
            calendarMap.each{k,v->
                def gc=new GregorianCalendar();
                gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
                infoInstance."${k}"=gc;
            }
            timeMap.each{k,v->
                infoInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
            }
            timestampMap.each{k,v->
                infoInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
            }
            ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().each{p->
                if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
                    if(params."${p}"){
                        infoInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
                    }
                }
            }
            if (!infoInstance.hasErrors() && infoInstance.save(flush: true)) {

                map.message = "${message(code: 'default.updated.message', args: [message(code: 'info.label', default: 'Info'), infoInstance.id])}"
                map.result=true
            }
            else {
                def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Info.getAnnotation(Title);
                infoInstance.errors.getAllErrors().each{error->
                    if(error.class.name=='org.springframework.validation.FieldError'){
                        def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"infoInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Info.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"infoInstance.label", default: 'Info')
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
            map.message = "${message(code: 'default.not.found.message', args: [message(code: 'info.label', default: 'Info'), params.id])}"
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
        def infoInstance = Info.get(params.id)
        if (infoInstance) {
            if (params.version) {
                def version = params.version.toLong()
                if (infoInstance.version > version) {
                    infoInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'info.label', default: 'Info')] as Object[], "Another user has updated this Info while you were editing")
                    map.result=false
                }
            }
            def dateMap=[:];
            def sqldateMap=[:];
            def calendarMap=[:];
            def timeMap=[:];
            def timestampMap=[:];
            ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().each{p->
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
            infoInstance.properties = params
            dateMap.each{k,v->
                infoInstance."${k}"=java.util.Date.parse('yyyy-MM-dd',v);
            }
            sqldateMap.each{k,v->
                infoInstance."${k}"=new java.sql.Date(java.util.Date.parse('yyyy-MM-dd',v).time);
            }
            calendarMap.each{k,v->
                def gc=new GregorianCalendar();
                gc.setTime(java.util.Date.parse('yyyy-MM-dd',v));
                infoInstance."${k}"=gc;
            }
            timeMap.each{k,v->
                infoInstance."${k}"=new java.sql.Time(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
            }
            timestampMap.each{k,v->
                infoInstance."${k}"=new java.sql.Timestamp(java.util.Date.parse('yyyy-MM-dd hh:mm:ss',v).time);
            }
            ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().each{p->
                if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == Calendar.class){
                    if(params."${p}"){
                        infoInstance."${p}"=Date.parse('yyyy-MM-dd hh:mm:ss',params."${p}");
                    }
                }
            }
            if (!infoInstance.hasErrors() && infoInstance.save(flush: true)) {
                map.message = "${message(code: 'default.updated.message', args: [message(code: 'info.label', default: 'Info'), infoInstance.id])}"
                map.result=true
            }
            else {
                def messageSource = grailsApplication.getMainContext().getBean("messageSource")
                def annotation=Info.getAnnotation(Title);
                infoInstance.errors.getAllErrors().each{error->
                    if(error.class.name=='org.springframework.validation.FieldError'){
                        def args=error.getArguments()
                        if(args.size()>0){
                            //args[0]=message(code:"infoInstance.${error.field}.label", default: "${error.field}")
                            def fieldAnnotation=Info.getDeclaredField(error.field).getAnnotation(Title);
                            if (fieldAnnotation.zh_CN()){
                                args[0]= fieldAnnotation.zh_CN()
                            }
                        }
                        if(args.size()>1){
                            //args[1]=message(code:"infoInstance.label", default: 'Info')
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
            map.message = "${message(code: 'default.not.found.message', args: [message(code: 'info.label', default: 'Info'), params.id])}"
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
        Info.withTransaction { status ->
            ids.each{id->
                def infoInstance = Info.get(id)
                if (infoInstance) {
                    try {
                        boolean hasManyChildren=false;
                        ApplicationHolder.application.getDomainClass(Info.name).getPersistentProperties().toList().each{p->
                            if(p.isAssociation() && (p.oneToMany || p.manyToMany)){
                                if(infoInstance."${p.name}".size()>0){
                                    hasManyChildren=true;
                                }
                            }
                        }
                        if(!hasManyChildren){
                            infoInstance.delete(flush: true);
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
    //
    private JSONElement sendUnRegisterInfoWithSm9(map){

    }
    //将确认消息加密后，组装成json数据
    private JSONElement  sendInfoWithSm9(Info info){
        def money=info.money.longValue().toString();
        if(money.size()>2){
            money="${money[0..(money.size()-3)]}元${money[-2]}角${money[-1]}分"
        }else{
            if (money.size()>1){
                money="${money[0]}角${money[1]}分"
            }else{
                money="${money}分"
            }
        }
        def strategy = Strategy.findByAccount(info?.account);
        def caption="确认消息";
        def content;
        println '111111111'
        if(info.payee==''){
            def message="";
            if(info.chnNo=="posc"){
                 message="机消费";
            }else if(info.chnNo=="atmc"){
                message="机取现";
            }else if(info.chnNo=="cibas"){
                message="取现";
            }
            content="${info.dateCreated.format('yyyy年MM月dd日hh时mm分')},您的${info.account.toString()}账号，正通过${pickUpPayType(info.chnNo)}${message}${money}.请确认?";
        }else{
            content="${info.dateCreated.format('yyyy年MM月dd日hh时mm分')},您的${info.account.toString()}账号，正通过${pickUpPayType(info.chnNo)}方式,支付 ${money} 给 ${info.payee}.请确认?";
        }
        println '222222222'
//        println content;
        SecurityEngine se=SecurityHelper.initSecurityEngine();
//        def receptList="${info.account.phone}@capinfo.com.cn";
        def receptList="${info.account.phone}";
        def signUser=SecurityHelper.serverIBC;
        String title=caption;
        def map=SecurityHelper.encrypt(se,receptList,content,SecurityHelper.serverIBC);
        def body=map.data;
        def bodyLength=map.length;
        def bodyVerify=map.verifyData;
        def bodyVerifyLength=map.verifyLength;
//        println "body=${body}"
        def uri='{"id":"'+info.uuid.encodeAsBase64()+'","oid":"'+info.id.toString().encodeAsBase64()+'","messageLength":"'+bodyLength+'","messageVerify":"'+bodyVerify+'","messageVerifyLength":"'+bodyVerifyLength+'","signUser":"'+signUser+'"}}';

        String phoneNum;
        println '33333333333'
        if(info?.account?.type=="03"){//不需要辅助确认
            def qmap=[:]
            qmap.uuid=info.uuid;
            qmap.result='yes'; //
            qmap.bankId=info.account.bank.id;
            qmap.id=info.id;
            qmap.trCode=info.trCode;
            qmap.seqNo=info.seqNo;
            qmap.chnNo=info.chnNo;
            qmap.action="PushInfo";
            def json=qmap as JSON
            sendPubSubJMSMessage("socketTopic", json.toString());
            def jsonMap=[:];
            jsonMap.result=true;
            return jsonMap as JSONElement;
        }else if(info?.account?.type=="02"){//通过确认器确认
//            def jsonStr = info as JSON;
            def qmap=[:]
            qmap.uuid=info.uuid;
            qmap.bankId=info.account.bank.id;
            qmap.id=info.id;
            qmap.trCode=info.trCode;
            qmap.seqNo=info.seqNo;
            qmap.chnNo=info.chnNo;
            qmap.account=info.account?.number;
            qmap.acc=info.account.toString();
            qmap.payee=info.payee;
            qmap.money=info.money;
            def jsonStr=qmap as JSON
            sendPubSubJMSMessage("socketNotify",jsonStr.toString());
            def jsonMap=[:];
            jsonMap.result=true;
            return jsonMap as JSONElement;
        }
        if (strategy?.type=='complex'){  //策略是复杂策略时手机号与对应交易金额权限设置的大小进行匹配
            //Map<String, Float> maps = new HashMap<String, Float>();
            def maps=[:]
            maps."${strategy?.min1}"=strategy?.phone1+':'+strategy?.isAuthorize1;
            maps."${strategy?.min2}"=strategy?.phone2+':'+strategy?.isAuthorize2;
            // maps.put(strategy?.min3,strategy?.phone3+':'+strategy?.isAuthorize1);
            //  maps.put(strategy?.min4,strategy?.phone4+':'+strategy?.isAuthorize1);
            //Object[] key=maps.keySet().toArray();
            //Arrays.sort(key);   //将key按升序排列；
            maps=maps.sort {a,b->
                return a.key?.toString()?.toFloat()-b.key?.toString()?.toFloat();
            }
            def keys=maps.keySet().toList();
            switch(info?.money){
                case {it>keys[0]?.toString()?.toFloat() && it<keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[0]}";break;
                case { it>keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[1]}";break;
            }
            /*
            switch (info?.money){
                case key[0]: phoneNum=maps.getAt(key[0]);break;
                case key[1]: phoneNum=maps.getAt(key[1]);break;;
          //      case key[2]: phoneNum=maps.getAt(key[2]);break;;
          //      case key[3]: phoneNum=maps.getAt(key[3]);break;;
            }
            */
        }else if(strategy?.type=='close'){  //当账号策略设置是关闭策略时直接向核心系统发送确认yes
            def qmap=[:]
            qmap.uuid=info.uuid;
            qmap.result='yes'; //
            qmap.bankId=info.account.bank.id;
            qmap.id=info.id;
            qmap.trCode=info.trCode;
            qmap.seqNo=info.seqNo;
            qmap.chnNo=info.chnNo;
            qmap.action="PushInfo";
            def json=qmap as JSON;
            println "socketTopic..............................."
            sendPubSubJMSMessage("socketTopic", json.toString());
            def jsonMap=[:];
            jsonMap.result=true;
            return jsonMap as JSONElement;
        }else if (strategy?.type=='between'&&(info?.money<strategy.min||info?.money>strategy.max)){     //当账号设置的策略是区间策略时，且金额不在所设置的区间的时候直接向核心系统发送确认yes
            def qmap=[:]
            qmap.uuid=info.uuid;
            qmap.result='yes'; //
            qmap.bankId=info.account.bank.id;
            qmap.id=info.id;
            qmap.trCode=info.trCode;
            qmap.seqNo=info.seqNo;
            qmap.chnNo=info.chnNo;
            qmap.action="PushInfo";
            def json=qmap as JSON
            sendPubSubJMSMessage("socketTopic", json.toString());
            def jsonMap=[:];
            jsonMap.result=true;
            return jsonMap as JSONElement;
        }
        final StringBuilder parameter = new StringBuilder();
        parameter.append("action=send&broadcast=N&username=");
        parameter.append(info.account.phone.number);
        parameter.append("&title=${title}&message=${body}");
        parameter.append("&uri=${uri}");
        //println  grailsApplication.config.grails.notify.xmpp.server.toString() + "notification_api.do"
        String resp = GetPostUtil.send("POST", grailsApplication.config.grails.notify.xmpp.server.toString() + "notification_api.do", parameter);
        String resp1;
        if (phoneNum&&phoneNum?.trim().split(':')[1]=='false'){
            receptList="${phoneNum?.trim().split(':')[0]}";
            def mapComplex=SecurityHelper.encrypt(se,receptList,content,SecurityHelper.serverIBC);
            body=mapComplex.data;
            bodyLength=mapComplex.length;
            bodyVerify=mapComplex.verifyData;
            bodyVerifyLength=mapComplex.verifyLength;
             uri='{"id":"'+info.uuid.encodeAsBase64()+'","oid":"'+info.id.toString().encodeAsBase64()+'","messageLength":"'+bodyLength+'","messageVerify":"'+bodyVerify+'","messageVerifyLength":"'+bodyVerifyLength+'","signUser":"'+signUser+'"}}';

            final StringBuilder parameter1 = new StringBuilder();
            parameter1.append("action=send&broadcast=N&username=");
            parameter1.append(phoneNum?.trim().split(':')[0]);
            parameter1.append("&title=${title}&message=${body}");
            parameter1.append("&uri=${uri}");
            resp1= GetPostUtil.send("POST", grailsApplication.config.grails.notify.xmpp.server.toString() + "notification_api.do", parameter1);
            if(!resp1){
                resp1='{"result":"-1","description":"server crack"}'
            }
        }
        if(!resp){
            resp='{"result":"-1","description":"server crack"}'
        }
        def jsonMap = [:];
        def list=[];
        list << resp;
        list << resp1;
        jsonMap.str = list;
        //println jsonMap
        //SecurityHelper.releaseSecurityEngine(se);
        //return JSON.parse(resp);
        return jsonMap as JSONElement;
    }
    //将确认消息组装成json数据,jpush方式，不再使用
    private JSONElement  sendInfo(Info info){
        int sendno=info.id;
        def stregy=Strategy.findByAccount(info.account);
        if(stregy && (info.money<stregy.min || info.money>stregy.max)){
            return JSON.parse("{'errcode':'9999','errmsg':'can not match the stregy!'}");
        }
        def url="http://api.jpush.cn:8800/sendmsg/v2/sendmsg";
        def app_key="a50acaf625bb9aa72c255638";
        def master_secret="afa348dcb7bccac6f748b372";
        def receiver_type=3;  //1、指定的 IMEI。此时必须指定 app_key  2、指定的 tag。  3、指定的 alias。   4、 对指定 app_key 的所有用户推送消息。
        def receiver_value=info.account.phone.tag;
        //println "receiver_type=${receiver_type}"
        //println "receiver_value=${receiver_value}"
        def verification_code="${sendno}${receiver_type}${receiver_value}${master_secret}".encodeAsMD5();
        def  msg_type=2;
        def infoid=info.uuid;
        def money=info.money.longValue().toString();
        if(money.size()>2){
            money="${money[0..(money.size()-3)]}元${money[-2]}角${money[-1]}分"
        }else{
            if (money.size()>1){
                money="${money[0]}角${money[1]}分"
            }else{
                money="${money}分"
            }
        }
        def title="确认消息";
        def content="您的${info.account.toString()}通过${pickUpPayType(info.chnNo)}方式,支付 ${money} 给 ${info.payee}.请确认?";
//        println content;
        //def  msg_content="{"'n_content':'您的账户'+${info.account.toString()}+':'+'支付'+ ${info.money.round(2)}+'给'+ ${info.payee}+'.请确认?','n_title':'确认消息','n_id':${info.id}"}";
        //def  msg_content='{"n_content":"'+content+'","n_title":"'+title+'","n_extras":{"id":"'+infoid+'"}}';
        //def  msg_content= '{"n_content":"请确认?","n_title":"确认消息","n_extras":"${info.id}"}';
        def   msg_content='{"message":"'+content.encodeAsBase64()+'","title":"'+title.encodeAsBase64()+'","extras":{"id":"'+infoid.encodeAsBase64()+'","oid":"'+info.id.toString().encodeAsBase64()+'"}}';
        def  platform="android,ios";
        def time_to_live="86400";
        def data= URLEncoder.encode("sendno", "UTF-8") + "=" + URLEncoder.encode("${sendno}", "UTF-8");
        data += "&" + URLEncoder.encode("app_key", "UTF-8") + "=" + URLEncoder.encode("${app_key}", "UTF-8");
        data += "&" + URLEncoder.encode("receiver_type", "UTF-8") + "=" + URLEncoder.encode("${receiver_type}", "UTF-8");
        data += "&" + URLEncoder.encode("receiver_value", "UTF-8") + "=" + URLEncoder.encode("${receiver_value}", "UTF-8");
        data += "&" + URLEncoder.encode("verification_code", "UTF-8") + "=" + URLEncoder.encode("${verification_code}", "UTF-8");
        data += "&" + URLEncoder.encode("msg_type", "UTF-8") + "=" + URLEncoder.encode("${msg_type}", "UTF-8");
        data += "&" + URLEncoder.encode("msg_content", "UTF-8") + "=" + URLEncoder.encode("${msg_content}", "UTF-8");
        data += "&" + URLEncoder.encode("platform", "UTF-8") + "=" + URLEncoder.encode("${platform}", "UTF-8");
        data += "&" + URLEncoder.encode("time_to_live", "UTF-8") + "=" + URLEncoder.encode("${time_to_live}", "UTF-8");
//        println  "msg_content=${msg_content}"

        return this.postData(url,data,"POST",null);

    }
    //使用http post 方式发送数据
    private JSONElement postData(address,data,method,cookie){
        try{
            // Construct data
/*            def file=new File("C:\\Android\\android-sdk\\docs\\sitemap.txt");
            String data = URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(file.text, "UTF-8");
            data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");*/
            // Send data
            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            if(cookie!=null){
                conn.setRequestProperty("Cookie", cookie);
            }
            conn.outputStream.withWriter('utf-8'){writer->
                writer.write(data);
            }
            return JSON.parse(conn.inputStream.getText('utf-8'));
        } catch (Exception e) {
            //view.trayIcon.displayMessage("错误",'服务器连接异常',ERROR)
            //view.optionPane.showMessageDialog(view.appWindow,'服务器连接异常',"error",JOptionPane.WARNING_MESSAGE)
        }
    }
    //处理逻辑
    private void processPushInfo(params,map){
        if (!params.username || !params.password){
            map.code="Q503";
            map.message="用户名和密码不匹配";
        }else{
            def baseUser=BaseUser.findByUsername(params.username);
            if (!baseUser){
                map.code="Q503";
                map.message="用户名和密码不匹配";
            }else{
                if (baseUser.password==springSecurityService.encodePassword(params.password)){
                    if (!params.bankId || !params.number || !params.money){       //!params.payee ||
                        if (!params.money){
                            map.code="Q004";
                            map.message="未提供支付金额";
                        }
                        //if (!params.payee){
                        //    map.code="Q003";
                        //    map.message="未提供收款人信息";
                        //}
                        if (!params.number){
                            map.code="Q002";
                            map.message="未提供账户信息";
                        }
                        if (!params.bankId){
                            map.code="Q001";
                            map.message="未提供银行信息";
                        }
                    }else{
                        try{
                            params.money=params.money.toFloat();
                        }catch(e){
                            map.code="Q005";
                            map.message="提供支付金额不符合要求，请提供float数字";
                        }
                        if (params.payee.size()>250){
                            map.code="Q006";
                            map.message="提供收款人信息不符合要求，请提供250字以内的字符";
                        }
                        if (params.number.size()>100){
                            map.code="Q007";
                            map.message="提供账户信息不符合要求，请提供100字以内的字符";
                        }
                        try{
                            params.bankId=params.bankId.toLong();
                        }catch(e){
                            map.code="Q008";
                            map.message="提供账户银行ID不符合要求，请提供LONG数字";
                        }
                        if(!map.code){
                            def bank=Bank.get(params.bankId);
                            if (bank){
                                def account=Account.findByNumberAndBank(params.number,bank);
                                //log.error "before account"
                                if (account){
                                    Info.withTransaction {
                                        def infoInstance=new Info(params);
                                        infoInstance.account=account;
                                        if (!infoInstance.hasErrors() && infoInstance.save(flush: true)) {
                                            //println "before send"
                                            def json=sendInfoWithSm9(infoInstance);
//                                            println '0====================================='
//                                            def jsonStr = infoInstance as JSON;
//                                            println '1====================================='
//                                            println jsonStr;
//                                            sendPubSubJMSMessage("socketNotify",jsonStr.toString());
//
//                                            println '2====================================='
                                            //def strategy=Strategy.findByAccount(account);
                                            /*
                                             if (strategy  && (strategy.min > infoInstance.money || strategy.max < infoInstance.money) ){
                                                 if (strategy.min > infoInstance.money){
                                                     map.code="Q100";
                                                     map.message="交易金额小于用户设置的提醒策略最小值";
                                                 }
                                                 if (strategy.max < infoInstance.money){
                                                     map.code="Q200";
                                                     map.message="交易金额大于用户设置的提醒策略最大值";
                                                 }
                                                 infoInstance.remark=map.message;
                                                 infoInstance.save();
                                             }else{
                                                 def json='';
                                                 if (!params.api){
                                                     params.api="2"
                                                 }
                                                 if(params.api=="1"){
                                                     json=sendInfo(infoInstance);
                                                 }
                                                 if(params.api=="2"){
                                                     json=sendInfoWithSm9(infoInstance);
                                                 }

                                                // def json=sendInfo(infoInstance);

                                             }*/
                                            //println json.toString()
                                            if(json){
                                                map.code="0000";
                                                map.message="发送成功，请等待消息确认";
                                                map.uuid=infoInstance.uuid;
                                            }else{
                                                map.code="Q020";
                                                map.message="网络出现问题，请联系相关人员";
                                            }
                                        }else{
                                            map.code="Q030";
                                            map.message=infoInstance.errors.toString();
                                        }
                                    }

                                }else{
                                    map.code="Q010";
                                    map.message="未发现匹配账户";
                                    /* def qmap=[:]
                                      qmap.uuid=null;
                                      qmap.result='yes';
                                      qmap.bankId=params.bankId;
                                      qmap.id=null;
                                      qmap.trCode=params.trCode;
                                      qmap.seqNo=params.seqNo;
                                      qmap.chnNo=params.chnNo;
                                      def json=qmap as JSON
                                      sendPubSubJMSMessage("socketTopic", json.toString());
                                      map.code="0000";
                                      map.message="发送成功，请等待消息确认";   */
                                }
                            }else{
                                map.code="Q009";
                                map.message="未发现匹配银行";
                                /*def qmap=[:]
                                 qmap.uuid=null;
                                 qmap.result='yes';
                                 qmap.bankId=params.bankId;
                                 qmap.id=null;
                                 qmap.trCode=params.trCode;
                                 qmap.seqNo=params.seqNo;
                                 qmap.chnNo=params.chnNo;
                                 def json=qmap as JSON
                                 sendPubSubJMSMessage("socketTopic", json.toString());
                                 map.code="0000";
                                 map.message="发送成功，请等待消息确认";  */
                            }
                        }

                    }
                }else{
                    map.code="Q503";
                    map.message="用户名和密码不匹配";
                }
            }
        }
    }
    // 启动服务器socket
    def startSocketServer={
        def smap=[:]
        if(!SocketServerStatus.isRuning2){
            runAsync {
                def server = new ServerSocket(SocketServerStatus.port);
                while(true) {
                    server.accept { socket ->
                        socket.setSoTimeout(9*60*1000);
                        socket.withStreams { input,output ->
                            PrintStream ps = new PrintStream(output,true,"GBK");
//                            ps<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK');
                            //begin queue
                            String currentClientSeqNo='';
                            String currentHeadString=null;
                            //ConnectionFactory jms = new ActiveMQConnectionFactory(brokerURL: "tcp://${request.getServerName()}:${request.serverPort}");
                            ConnectionFactory jms = new ActiveMQConnectionFactory(brokerURL: "tcp://127.0.0.1:61616");
                            use(JMS){
                                jms.topic('socketTopic').subscribe(
                                        {Message m ->
                                            //def uuid=m.text;  //this is string type// other if message is object type// def mstr=new String(m.content.data);
                                            def json=JSON.parse(m.text);
                                            println "from notifySocket message"
//                                            println '11111111111111111111111111111111111111111111'
//                                            println json;
//                                            println '11111111111111111111111111111111111111111111'
                                            def map=[:]
                                            map.code="0000";
                                            map.trCode=json.trCode;
                                            map.uuid=json.uuid;
                                            map.result=json.result;
                                            map.bankId=json.bankId;
                                            map.trCode=json.trCode;
                                            map.id=json.id;
                                            map.seqNo=json.seqNo;
                                            map.chnNo=json.chnNo;
                                            map.action=json.action;
                                            //String echo="ST1${(new java.util.Date()).format('yyyyMMddhhmmss')}${map.trCode}${map.id.toString().padLeft(12,'0')}${map.seqNo}${map.chnNo.padLeft(10,'0')}";
                                            //because no seqNo2 in new protocol,so use under word
                                            String echo="ST1${(new java.util.Date()).format('yyyyMMddhhmmss')}${map.trCode}${map.seqNo}${map.chnNo.padRight(10,'=').replaceAll('=','*')}";
                                            /*
                                            if(currentHeadString==null){
                                                echo="ST1${(new java.util.Date()).format('yyyyMMddhhmmss')}${map.trCode}${map.seqNo}${map.chnNo.padRight(10,'=').replaceAll('=','*')}";
                                            }else{
                                                echo=currentHeadString;
                                            }
                                            */
//                                            println "echo=${echo}"
                                            echo=echo+"<?xml version='1.0' encoding='UTF-8'?><response><action>${map.action}</action><bankId>${map.bankId}</bankId><uuid>${map.uuid?:''}</uuid><code>${map.code}</code><result>${map.result}</result></response>";
//                                            println  "currentClientSeqNo=${currentClientSeqNo}"
//                                            println "map.seqNo=${map.seqNo}"
                                            println "11111111111111111111111111111"
                                            if(map.seqNo==currentClientSeqNo){
                                                println "222222222222222222222222222222222"
                                                //println echo
                                                //output<<echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo //+ "\n"
                                                ps<<((echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK'))
                                            }
                                        } as MessageListener);
                            }
                            //end queue
                            def reader = input.newReader('GBK');
                            StringBuffer sb=new StringBuffer();
                            int charnum=0;
                            int charone;
                            int len=0;
                            def map=[:]
                            String echo;
                            String beginStr;
                            while((charone=reader.read())!=-1){
                                sb<<(char)charone;
                                charnum=charnum+(String.valueOf((char)charone).getBytes('GBK').size());   //charnum=charnum+(String.valueOf((char)charone).getBytes('GBK').size());
                                if(charnum==8){
                                    len=sb.toString().toInteger();
//                                   println "len="+len
                                }
//                                println "charnum="+charnum
//                                println "sb.toString()="+sb.toString()
                                if(charnum==len+8){
                                    try{
                                        def subStr=sb.toString().substring(8);
                                        currentHeadString=subStr.substring(0,46)
//                                        println subStr
                                        String trCode=subStr.substring(17,24);
//                                       println "trCode=${trCode}"
                                        String seqNo=subStr.substring(24,36);
                                        currentClientSeqNo=seqNo;
//                                        println "seqNo=${seqNo}"
                                        /*
                                        String chnNo=pickUpVal(subStr.substring(48,58),'*');
                                        //println "chnNo=${chnNo}"
                                        beginStr=subStr.substring(0,58);
                                        //println "beginStr=${beginStr}"
                                        String buffer=subStr.substring(58);
                                        //println "="+buffer
                                        */
                                        // no seqNo2 in new protocol,so use next method
                                        //println "subStr.substring(36,46)="+subStr.substring(36,46)
//                                        println subStr.substring(36,46).replaceAll('\\*','');
                                        //String chnNo=pickUpVal(subStr.substring(36,46),'*');
                                        String chnNo=subStr.substring(36,46).replaceAll('\\*','').trim()
                                        //println "chnNo=${chnNo}="
                                        beginStr=subStr.substring(0,46);
                                        //println "beginStr=${beginStr}"
                                        String buffer=subStr.substring(46);
                                        println "buffer="+buffer
                                        def xml=new XmlSlurper().parseText(buffer);
                                        map.action=xml.action;
                                        if(xml.action=='PushInfo'){
                                            map.uuid='';
                                            map.bankId=xml.bankId.toString();
                                            def paramsList=[:]
                                            paramsList.username=xml.username.toString();
                                            paramsList.password=xml.password.toString();
                                            paramsList.bankId=xml.bankId.toString();
                                            paramsList.number=xml.number.toString();
                                            paramsList.payee=xml.payee.toString();
                                            paramsList.money=xml.money.toString();
                                            paramsList.trCode=trCode;
                                            paramsList.seqNo=seqNo;
                                            paramsList.chnNo=chnNo;
                                            paramsList.action=xml.action;
                                            log.error("before proess");
                                            TraceLog.withNewSession {
                                                def traceLog;
                                                traceLog= new TraceLog(operating:'发送交易确认信息',channel: 'socket',params: paramsList.toString());
                                                 if(!traceLog.save(flush:true)){
                                                     println "--------------------------"
                                                        println traceLog.errors;
                                                     println "--------------------------"
                                                 }
                                            }
                                            println "before processPushInfo"
//                                            println "paramsList="+paramsList
//                                            println "map="+map
                                            processPushInfo(paramsList,map);
                                            log.error map
                                            echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action>PushInfo</action><bankId>${map.bankId}</bankId><uuid>${map.uuid?:''}</uuid><code>${map.code}</code></response>";
                                        } else if(xml.action=='Unregistration'){
                                            def paramsList=[:]
                                            paramsList.username=xml.username.toString();
                                            paramsList.password=xml.password.toString();
                                            paramsList.bankId=xml.bankId.toString();
                                            paramsList.name=xml.name.toString();
                                            paramsList.identificationType=xml.identificationType.toString();
                                            paramsList.identificationNo=xml.identificationNo.toString();
                                            paramsList.phone=xml.phone.toString();
                                            paramsList.account=xml.account.toString();
                                            TraceLog.withNewSession {
                                                new TraceLog(operating:'发送注销信息',channel: 'socket',params: paramsList.toString()).save(flush:true);
                                            }
                                            def backMap=[:]
                                            if(!paramsList.name){backMap.code='Q001';}
                                            if(!paramsList.phone){backMap.code='Q002';}
                                            if(!paramsList.account){backMap.code='Q003';}
                                            if(!backMap.code){
                                                Account.withNewSession {
                                                    def bank = Bank.get(paramsList.bankId?.toLong()?:-1l);
                                                    def account;
                                                    if(bank){
                                                        account=Account.findByNumberAndBank(paramsList.account?:'-1l',bank);
                                                    }
                                                    if (account){
                                                       def strategy = Strategy.findByAccount(account);
                                                        if(strategy){
                                                            strategy.delete();
                                                        }
//                                                     account.status='注销';
                                                        account.type='03';
                                                        account.save(flush:true);
                                                    }
                                                }
                                                backMap.code='0000';
                                            }
                                            // add user operator
                                            echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action>Unregistration</action><bankId>${paramsList.bankId}</bankId><code>0000</code><result>yes</result></response>";
                                            ps<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK');
                                            return;
                                        }else if(xml.action=='Registration'){

                                            def paramsList=[:]
                                            paramsList.username=xml.username.toString();
                                            paramsList.password=xml.password.toString();
                                            paramsList.bankId=xml.bankId.toString();
                                            paramsList.name=xml.name.toString();
                                            paramsList.identificationType=xml.identificationType.toString();
                                            paramsList.identificationNo=xml.identificationNo.toString();
                                            paramsList.phone=xml.phone.toString();
                                            paramsList.account=xml.account.toString();
                                            paramsList.type=xml.type.toString();
                                            TraceLog.withNewSession {
                                                new TraceLog(operating:'发送注册信息',channel: 'socket','params': paramsList.toString()).save(flush:true);
                                            }

                                            def backMap=[:]

                                            if(!paramsList.name){backMap.code='Q001';}
                                            if(!paramsList.phone){backMap.code='Q002';}
                                            if(!paramsList.account){backMap.code='Q003';}

                                            if(!backMap.code){
                                                Account.withNewSession {
                                                    def account = Account.findByNumber(paramsList.account?:'*');
                                                    if (account){
                                                        backMap.result = false;
                                                        backMap.message = '账户已经存在！';
                                                    } else{
                                                        account = new Account();
                                                    }
                                                    def  phone = Phone.findByNumber(paramsList.phone?:'*');
                                                    if (!phone){
                                                        phone = new Phone(number: paramsList.phone).save(flush: true);
                                                    }else{
                                                        phone.realName=paramsList.name;
                                                        phone.save(flush: true);
                                                    }
                                                    account.number = paramsList.account;
                                                    account.realName = paramsList.name;
                                                    account.phone =phone;
                                                    account.bank = Bank.get(paramsList.bankId.toLong());//Bank.findByName('鹤壁银行');
                                                    account.type=paramsList.type;
                                                    //account.baseUser = springSecurityService.currentUser;
                                                    if (account.save(flush: true)){
                                                        def strategy = new Strategy();
                                                        strategy.account=account;
                                                        strategy.type='open';
                                                        if(!strategy.save(flush: true)){
                                                            println strategy.errors;
                                                        }
                                                        backMap.result = true;
                                                        backMap.message = '账户保存成功！';
                                                    } else{
                                                        backMap.result = false;
                                                        backMap.message = '账户保存失败！';
                                                    }
                                                }
                                                backMap.code='0000';
                                            }
                                            println '1111111111111111111'
                                            echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action>Registration</action><bankId>${paramsList.bankId}</bankId><code>${backMap.code}</code><result>yes</result></response>";
                                            println 'echo='+echo
                                            ps<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK')
                                            return;
                                        }else{

                                            map.code="Q500"
                                            map.message="xml文档格式不正确"
                                            echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action></action><code>500</code></response>";
                                        }
                                    }catch(e){
                                        log.error e.message +"================Q500"
                                        map.code="Q500"
                                        map.message="xml文档格式不正确"
                                        echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action></action><code>500</code></response>";
                                    }
                                    log.error "after map"+map
                                    len=0;
                                    charnum=0;
                                    sb=new StringBuffer();
                                    if(map.code){
//                                        println map
                                        if(map.code=="0000"){
                                            def newmap=[:]
                                            newmap.seqNo=map.seqNo.toString();
                                            newmap.uuid=map.uuid.toString();
                                            newmap.bankId=map.bankId.toString();
                                            newmap.code=map.code.toString();
//                                            println "*********000000000000000000000*************"
                                            def timer=new Timer();
                                            timer.runAfter(SocketServerStatus.timeout){
                                                //if(checkInfoTimeOut(newmap.seqNo,newmap.uuid)){
                                                if(checkTimeOut(newmap.seqNo,newmap.uuid)){
//                                                    println "*********111111111*************"
                                                    newmap.code="Q408"
                                                    newmap.message="连接时间超时,限定时间内用户未给出答复";
                                                    String echoTimeout=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action>${map.action}</action><bankId>${newmap.bankId}</bankId><uuid>${newmap.uuid?:''}</uuid><code>${newmap.code}</code></response>";
                                                    //output<<(echoTimeout.getBytes('GBK').size().toString().padLeft(8,'0')+echoTimeout)
                                                    ps<<(echoTimeout.getBytes('GBK').size().toString().padLeft(8,'0')+echoTimeout).padRight(500," ").getBytes('GBK');
                                                }else{
                                                    println "0000==========================================="+echo;
                                                    ps<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK');
                                                }
                                            }
                                        }else{
                                            if(map.code=='Q009'||map.code=='Q010'){
                                                echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action>${map.action}</action><bankId>${map.bankId}</bankId><uuid>${map.uuid?:''}</uuid><code>0000</code><result>yes</result></response>";
                                            }
                                            println "Q010================================================"
                                            echo=beginStr+"<?xml version='1.0' encoding='UTF-8'?><response><action>${map.action}</action><bankId>${map.bankId}</bankId><uuid>${map.uuid?:''}</uuid><code>0000</code><result>yes</result></response>";
                                            //output<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo)
                                            println echo;
                                            ps<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK');
//                                            ps<<(echo.getBytes('GBK').size().toString().padLeft(8,'0')+echo).padRight(500," ").getBytes('GBK');
                                            println "Q010====================over"
                                            return;
                                        }
                                    }
                                    echo='';
                                    map=[:]
                                }
                            }
                            println 'over'
                            /*
                            while ((buffer = reader.readLine()) != null) {
                                def map=[:]
                                String echo;

                               //+ "\n"
                            }
                          */
                        }
                    }
                }
                /*

               s = new Socket("210.76.97.183", 8989);
               //s = new Socket("localhost", 8989);
               s.setSoTimeout(9*60*1000);
               s.withStreams { input, output ->
                   PrintStream ps = new PrintStream(output,true,"GBK");
                   def str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>PushInfo</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><number>A000001</number><payee>肖鹏</payee><money>500</money></request>'
                   //register
                   str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>Registration</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><name>张强</name><identificationType>身份证</identificationType><identificationNo>110110090000101200</identificationNo><phone>13999888888</phone><account>6228000100001117</account></request>'
                  // UNregister
                   str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>Unregistration</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><name>张强</name><identificationType>身份证</identificationType><identificationNo>110110090000101200</identificationNo><phone>13999888888</phone><account>6228000100001117</account></request>'
                   str=(str.getBytes('GBK').size().toString().padLeft(8,'0')+str)
                   //output << "*bye*\n"
                   ps<<str.getBytes('GBK')
                   reader = input.newReader('UTF-8');
                   int charone
                  StringBuffer sb=new StringBuffer();
                  while((charone=reader.read())!=-1){
                       sb<<(char)charone;
                       println sb.toString();
                  }
               }
               * */
            }
            SocketServerStatus.isRuning2=true;
            smap.result=true;
            smap.message="SocketServer服务启动成功！";
        }else{
            smap.result=false;
            smap.message="SocketServer服务已经启动！";
        }
        render smap as JSON;
    }
    private String pickUpVal(String value,String token){
        /*
        StringBuffer  sb=new StringBuffer();
        boolean isEnd=false;
        value.each{charone->
             if (charone!=token){
                 isBegin=true;
             }
            if (isBegin){
                sb << charone;
            }
        }
        return sb.toString();
        */
        //return value.replaceAll(token,'');
        return value.substring(0,value.indexOf(token));
    }
    public static String pickUpPayType(String chnNo){
        String chnType;
        switch (chnNo) {
            case "cibas":
                chnType="柜面"
                break
            case "atmc":
                chnType="ATM"
                break
            case "cups":
                chnType="银联"
                break
            case "icc":
                chnType="IC卡系统"
                break
            case "posc":
                chnType="POS"
                break
            default:
                chnType=""
                break
        }
    }
    private boolean checkTimeOut(String seqNo,String uuid){
        try{
            if (!seqNo){return false;}
            if (!uuid){return false;}
            Info.withTransaction {
                def info=Info.findBySeqNo(seqNo);
                if (!info){
                    info=Info.findByUuid(uuid);
                }
                def map11=JSON.parse(info.result);
                def result;
                //println '************************';
                //println map11;
                if(map11.strategy!='complex'&&map11.confirm.size()>0){
                    result = map11.confirm[0]?.result;
                }else{
                    def strategy = Strategy.findByAccount(info?.account);
                    def phoneNum;
                    def maps=[:]
                    maps."${strategy?.min1}"=strategy?.phone1+':'+strategy?.isAuthorize1;
                    maps."${strategy?.min2}"=strategy?.phone2+':'+strategy?.isAuthorize2;
                    maps=maps.sort {a,b->
                        return a.key?.toString()?.toFloat()-b.key?.toString()?.toFloat();
                    }
                    def keys=maps.keySet().toList();
                    switch(info?.money){
                        case {it>keys[0]?.toString()?.toFloat() && it<keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[0]}";break;
                        case { it>keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[1]}";break;
                    }
                    //println phoneNum
                    if(phoneNum&&phoneNum?.trim().split(':')[1]=='true'){
                        if(map11.confirm?.size()==1&&map11.confirm[0]?.type=='main'){
                            result= map11.confirm[0]?.result;
                        }else{
                            result='no';
                        }
                    } else{
                        //println map11.confirm?.size();
                        if(map11.confirm?.size()==1){
                            result=map11.confirm[0]?.result;
                        }else if(map11.confirm?.size()>1){
                            def confirmMap1=map11.confirm[0];
                            def confirmMap2=map11.confirm[1];
                            if(confirmMap1.type=='assist'||confirmMap2.type=='assist'){
                                if(phoneNum?.trim().split(':')[0]==confirmMap1.phone||phoneNum?.trim().split(':')[0]==confirmMap2.phone){
                                    if(confirmMap1.result=='yes'&&confirmMap2=='yes'){
                                        result='yes'
                                    }else{
                                        result='no';
                                    }
                                }else{
                                    result='no';
                                }
                            }
                        }else{
                            result='no';
                        }
                    }
                }
                if (['yes','no','alarm'].contains(result)){
                    //println "*********22222222222*************"
                    return false;
                }else{
                    info.remark="timeout";
                    info.save(flush: true);
                    return true;
                }
            }
        }catch(e){
            //println "checkInfoTimeOut="+e.message
            return false;
        }

    }
    private boolean checkInfoTimeOut(String seqNo,String uuid){
        try{
            if (!seqNo){return false;}
            if (!uuid){return false;}
            Info.withTransaction {
                def info=Info.findBySeqNo(seqNo);
                if (!info){
                    info=Info.findByUuid(uuid);
                }
                if (['yes','no','alarm'].contains(info.result)){
                    //println "*********22222222222*************"
                    return false;
                }else{
                    info.remark="timeout";
                    info.save(flush: true);
                    return true;
                }
            }
        }catch(e){
            //println "checkInfoTimeOut="+e.message
            return false;
        }


    }
}
