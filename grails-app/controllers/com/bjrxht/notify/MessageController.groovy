package com.bjrxht.notify

import com.bjrxht.sm9.SecurityHelper
import com.bjrxht.socket.SocketServerStatus
import com.bjrxht.util.HexUtil
import grails.converters.JSON
import groovy.jms.JMS
import org.apache.activemq.spring.ActiveMQConnectionFactory

import javax.jms.ConnectionFactory
import javax.jms.Message
import javax.jms.MessageListener
import java.math.MathContext
import com.nisc.SecurityEngine;
//提供android手机交互操作类 //
class MessageController {
     //
    def index() {
        //def message = "Hi, this is a Hello World with JMS & ActiveMQ, " + new Date()
        //sendJMSMessage("feedback", message+"=feedback")
        //sendJMSMessage("notification", message+"=notification")
        //sendPubSubJMSMessage("feedback", message+"=pubsub=feedback")
        /*
        def qmap=[:]
        qmap.uuid='1111';
        qmap.result=true;
        qmap.bankId=123;
        def json=qmap as JSON
        sendPubSubJMSMessage("socketTopic", json.toString());
        */
        def map=[:];
        map.type='main';
        map.confirm='handle';
        map.result='yes';
        map.phone='123';
        def jsonMap=[:];
        jsonMap.strategy='complex';
        def list=[];
        list << map;
        jsonMap.confirm=[];
        jsonMap.confirm=list;
        println jsonMap.toString();
        println '*************************';
        def json = jsonMap as JSON;

        render json.toString();
       // render "notification="
        //log.error 'dddddd'
        //sendJMSMessage("topic.notification", message)
        // sendTopicJMSMessage("topic.notification", message)
        //render message
    }
    def test = {
        def map=[:];
        map.type='main';
        map.cofirm='handle';
        map.result='yes';
        map.phone='123';
        def jsonMap=[:];
        jsonMap.strategy='complex';
        jsonMap.confirm=[:];
        jsonMap.confirm=map;
        println jsonMap.toString();
        println '*************************';
        def json = jsonMap as JSON;
        render json;
    }
    // 手机SM9加密后，确认处理
    def receiveSm9={
        //log.error params
        //def receiver=params.phone;
        //def type=params.type;
        //def transId=params.transId;
        def map=[:]
        map.result=false
        def socketFlag=false;
        //if(params.signUser && params.data && params.length && params.verifyData && params.verifyLength){
            SecurityEngine se=SecurityHelper.initSecurityEngine();
            def securityMap=[:]
            securityMap.data=params.data;
            securityMap.length=params.length.toString().toInteger().intValue();
            securityMap.verifyData=params.verifyData;
            securityMap.verifyLength=params.verifyLength.toString().toInteger().intValue();
            securityMap.signUser=params.signUser;
            def passStr=SecurityHelper.decrypt(se,securityMap);
            def passStrjson=JSON.parse(passStr);
            if(passStrjson.infoId){
                String token=passStrjson.token.toString();
                String choice=passStrjson.choice.toString();
                def info=Info.findByUuid(passStrjson.infoId.toString());
                new TraceLog(operating:'用户确认交易信息',channel: 'web',params: passStrjson.toString()).save(flush:true);
                if (info){
                    // info.result=choice;
                    def map1 = [:];
                    def jsonMap = [:];
                    def map2=[:];
                    def flag = false;
                    def type = Strategy.findByAccount(info.account)?.type;
                    if (type=='complex'){
                        if (info.result){
                            flag = true;
                        }else{
                            if (info.account?.phone?.number==token){
                                map1.type='main';
                                map1.confirm='handle';
                            }else{
                                map1.type='assist';
                                map1.confirm='handle';
                            }
                            map1.result = choice;
                        }
                    }else if (type=='close'){
                        map1.result='yes';
                    }else{
                        map1.result = choice;
                    }
                    def list =[];
                    if (flag){
                        if (info.account?.phone?.number==token){
                            map2.type='main';
                            map2.confirm='handle';
                        }else{
                            map2.type='assist';
                            map2.confirm='handle';
                        }
                        map2.result = choice;
                        map2.phone = token;
                        list=JSON.parse(info.result).confirm;
                        list << map2;
                    }else{
                        map1.phone=token;
                        list << map1;
                    }
                    jsonMap.confirm=[];
                    jsonMap.confirm=list;
                    jsonMap.strategy = type;
                    def jsonStr = jsonMap as JSON;
                    info.result = jsonStr.toString();
                    println jsonStr.toString();
                    info.save(flush: true);
                    /*
                    def json=[:];
                    json.result=info.result;
                    json.uuid=info.uuid;
                    */
                    sendPubSubJMSMessage("feedback", info.uuid);
                    if (info.remark!="timeout"){
                        /*******************************************************************************************************************/
                        def map11=JSON.parse(info.result);
                        def result;
                        println '************************';
                        println map11;
                        if(map11.strategy!='complex'&&map11.confirm.size()>0){ //其他策略，并且收到回复确认
                            result = map11.confirm[0]?.result;
                            socketFlag=true;
                        }else if(map11.strategy=='complex'){
                            def strategy = Strategy.findByAccount(info?.account);
                            def phoneNum;
                            def maps=[:]
                            maps."${strategy?.min1}"=strategy?.phone1+':'+strategy?.isAuthorize1;
                            maps."${strategy?.min2}"=strategy?.phone2+':'+strategy?.isAuthorize2;
                            maps=maps.sort {a,b->
                                return a.key?.toString()?.toFloat()-b.key?.toString()?.toFloat();
                            }
                            def keys=maps.keySet().toList();
                            if(info?.money<keys[0]?.toString()?.toFloat()){  //复杂策略时交易金额小于最小策略设置金额时直接取主手机结果
                                socketFlag=true;
                                result = map11.confirm[0]?.result;
                            } else{
                                switch(info?.money){
                                    case {it>keys[0]?.toString()?.toFloat() && it<keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[0]}";break;
                                    case { it>keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[1]}";break;
                                }
                                println phoneNum
                                if(phoneNum&&phoneNum?.trim().split(':')[1]=='true'){
                                    if(map11.confirm?.size()==1&&map11.confirm[0]?.type=='main'){
                                        result= map11.confirm[0]?.result;
                                    }else{
                                        result='no';
                                    }
                                }else{
                                    println map11.confirm?.size();
                                    if(map11.confirm?.size()==1){
                                        result=map11.confirm[0]?.result;
                                    }else if(map11.confirm?.size()>1){
                                        def confirmMap1=map11.confirm[0];
                                        def confirmMap2=map11.confirm[1];
                                        //  if(confirmMap1.type=='assist'||confirmMap2.type=='assist'){
                                        //    if(phoneNum?.trim().split(':')[0]==confirmMap1.phone||phoneNum?.trim().split(':')[0]==confirmMap2.phone){
                                        println "confirmMap1.result="+confirmMap1.result
                                        println "confirmMap2.result="+confirmMap2.result
                                        if(confirmMap1.result=='yes'&&confirmMap2.result=='yes'){
                                            println "here yes"
                                            socketFlag=true;
                                            result='yes'
                                        }else{
                                            println 'here no'
                                            result='no';
                                        }
                                        // }else{
                                        //   result='no';
                                        // }
                                        // }
                                    }else{
                                        result='no';
                                    }
                                }
                            }

                        }
                        println socketFlag;
                        println result;

                        /*******************************************************************************************************************/
                      if(socketFlag){
                          def qmap=[:]
                          qmap.uuid=info.uuid;
                          qmap.result=result; //
                          qmap.bankId=info.account.bank.id;
                          qmap.id=info.id;
                          qmap.trCode=info.trCode;
                          qmap.seqNo=info.seqNo;
                          qmap.chnNo=info.chnNo;
                          def json=qmap as JSON
                          sendPubSubJMSMessage("socketTopic", json.toString());
                      }
                    }
                    map.result=true
                }else{
                    map.result=false
                }
            }
        //}

        render map as JSON;
    }
   public void saveInfo(Info info,String token,String choice){
       // info.result=choice;
       def map1 = [:];
       def jsonMap = [:];
       def map2=[:];
       def flag = false;
       def strategy
           strategy = Strategy.findByAccount(info.account);
       def type = strategy?.type;
       if (type=='complex'){
           if (info.result){
               flag = true;
           }else{
               if (info.account?.phone?.number==token){
                   map1.type='main';
                   map1.confirm='handle';
               }else{
                   map1.type='assist';
                   map1.confirm='handle';
               }
               map1.result = choice;
           }
       }else if (type=='close'){
           map1.result='yes';
       }else{
           map1.result = choice;
       }
       def list =[];
       if (flag){
           if (info.account?.phone?.number==token){
               map2.type='main';
               map2.confirm='handle';
           }else{
               map2.type='assist';
               map2.confirm='handle';
           }
           map2.result = choice;
           map2.phone = token;
           list=JSON.parse(info.result).confirm;
           list << map2;
       }else{
           map1.phone=token;
           list << map1;
       }
       jsonMap.confirm=[];
       jsonMap.confirm=list;
       jsonMap.strategy = type;
       def jsonStr = jsonMap as JSON;
       info.result = jsonStr.toString();
       println jsonStr.toString();
           info.save(flush: true);
       /*
       def json=[:];
       json.result=info.result;
       json.uuid=info.uuid;
       */
       sendPubSubJMSMessage("feedback", info.uuid);
    }
    public boolean sendResult(Info info){
        boolean socketFlag=false;
        if (info.remark!="timeout"){
            /*******************************************************************************************************************/
            def map11=JSON.parse(info.result);
            def result;
            println '************************';
            println map11;
            if(map11.strategy!='complex'&&map11.confirm.size()>0){ //其他策略，并且收到回复确认
                result = map11.confirm[0]?.result;
                socketFlag=true;
            }else if(map11.strategy=='complex'){

                def strategy;
                Strategy.withNewSession {
                    strategy= Strategy.findByAccount(info?.account);
                }
                def phoneNum;
                def maps=[:]
                maps."${strategy?.min1}"=strategy?.phone1+':'+strategy?.isAuthorize1;
                maps."${strategy?.min2}"=strategy?.phone2+':'+strategy?.isAuthorize2;
                maps=maps.sort {a,b->
                    return a.key?.toString()?.toFloat()-b.key?.toString()?.toFloat();
                }
                def keys=maps.keySet().toList();
                if(info?.money<keys[0]?.toString()?.toFloat()){  //复杂策略时交易金额小于最小策略设置金额时直接取主手机结果
                    socketFlag=true;
                    result = map11.confirm[0]?.result;
                } else{
                    switch(info?.money){
                        case {it>keys[0]?.toString()?.toFloat() && it<keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[0]}";break;
                        case { it>keys[1]?.toString()?.toFloat()}: phoneNum=maps."${keys[1]}";break;
                    }
                    println phoneNum
                    if(phoneNum&&phoneNum?.trim().split(':')[1]=='true'){
                        if(map11.confirm?.size()==1&&map11.confirm[0]?.type=='main'){
                            result= map11.confirm[0]?.result;
                        }else{
                            result='no';
                        }
                    }else{
                        println map11.confirm?.size();
                        if(map11.confirm?.size()==1){
                            result=map11.confirm[0]?.result;
                        }else if(map11.confirm?.size()>1){
                            def confirmMap1=map11.confirm[0];
                            def confirmMap2=map11.confirm[1];
                            //  if(confirmMap1.type=='assist'||confirmMap2.type=='assist'){
                            //    if(phoneNum?.trim().split(':')[0]==confirmMap1.phone||phoneNum?.trim().split(':')[0]==confirmMap2.phone){
                            println "confirmMap1.result="+confirmMap1.result
                            println "confirmMap2.result="+confirmMap2.result
                            if(confirmMap1.result=='yes'&&confirmMap2.result=='yes'){
                                println "here yes"
                                socketFlag=true;
                                result='yes'
                            }else{
                                println 'here no'
                                result='no';
                            }
                            // }else{
                            //   result='no';
                            // }
                            // }
                        }else{
                            result='no';
                        }
                    }
                }

            }
            println socketFlag;
            println result;
            /*******************************************************************************************************************/
        }
        return socketFlag;
    }
    // 手机未加密处理，确认处理
    def receive={
        //def receiver=params.phone;
        //def type=params.type;
        //def transId=params.transId;
        def map=[:]
        if(params.infoId){
            //def info=Info.get(new String(params.infoId.decodeBase64()).toLong());
            def info=Info.findByUuid(new String(params.infoId.decodeBase64()));
            if (info){
                info.result="${ new String(params.choice.decodeBase64())}";
                info.save(flush: true);
                /*
                def json=[:];
                json.result=info.result;
                json.uuid=info.uuid;
                */
                sendPubSubJMSMessage("feedback", info.uuid);
                if (info.remark!="timeout"){
                    def qmap=[:]
                    qmap.uuid=info.uuid;
                    qmap.result=info.result;
                    qmap.bankId=info.account.bank.id;
                    qmap.id=info.id;
                    qmap.trCode=info.trCode;
                    qmap.seqNo=info.seqNo;
                    qmap.chnNo=info.chnNo;
                    def json=qmap as JSON
                    sendPubSubJMSMessage("socketTopic", json.toString());
                }
                map.result=true
            }else{
                map.result=false
            }
        }

        render map as JSON;
    }
    // 根据手机号获取别名，jpush使用，当前不使用
    def getTagByPhone={
        /*
        def phone=Phone.findByNumber(params.number);
        def map=[:]
        if (phone && phone.tag){
            map.result=true;
            map.tag=phone.tag
        }else{
            map.result=false;
        }
        */
        def map=[:]
        map.result=true;
        map.tag= params.number;
        render map as JSON;
    }
    //根据手机号码获取绑定银行账户类别
    def getAccountNumber={
        def phone=Phone.findByNumber(params.number);
        def accounts=phone.accounts+Strategy.findByPhone1(params.number)?.account+Strategy.findByPhone2(params.number)?.account;
        accounts=accounts.findAll{
            it!=null && it.status!='注销';
        }.sort{it.number};
        def map=[:]
        if (accounts.size()>0){
            //println "aaaaaaaaaa="+accounts.size();
            map.result=true;
            map.accounts=accounts.collect{return "${it.bank.name}-${it.number}"}.join(";");
            map.types = accounts.collect {
                if(it.phone.number==params.number){
                    return 'main';
                }else{
                    return 'assist';
                }
            }.join(";");
            map.strategyType=accounts.collect {
                def strategy = Strategy.findByAccount(it);
                if(strategy){
                    return strategy.type;
                }else{
                    return 'open';
                }
            }.join(";");
        }else{
            map.result=false;
        }
        render map as JSON;
    }
    //根据手机号码获取手机策略
    def strategyAction={
        new TraceLog(operating:'用户策略操作',channel: 'web',params: params.toString()).save(flush:true);
        def map=[:]
        def list=params.accountNumber.toString().tokenize("-");
        def bank=Bank.findByName(list[0]);
        if (bank){
            def account=Account.findByBankAndNumber(bank,list[1]);
            if (account){
                def strategy=Strategy.findByAccount(account);
                if(params.do=="getInfo"){
                    if (strategy){
                        map.type = strategy.type;
                        if (strategy.type=='between'){
                            map.max=(strategy.max.toBigDecimal()/100).round(new MathContext(0)).toPlainString();
                            map.min=(strategy.min.toBigDecimal()/100).round(new MathContext(0)).toPlainString();
                        }else if (strategy.type=='complex'){
                            map.min1=(strategy.min1.toBigDecimal()/100).round(new MathContext(0)).toPlainString();
                            map.min2=(strategy.min2.toBigDecimal()/100).round(new MathContext(0)).toPlainString();
                            map.phone1 = strategy.phone1;
                            map.phone2 = strategy.phone2;
                           //map.phone3 = strategy.phone3;
                           //map.phone4 = strategy.phone4;
                           //map.min3=strategy.min3.toBigDecimal().round(new MathContext(0)).toPlainString();
                           //map.min4=strategy.min4.toBigDecimal().round(new MathContext(0)).toPlainString();
                        }
                    }else{
                        map.type='open';
                    }
                    map.result=true;
                }
                if(params.do=="setInfo"){
                    if (!strategy){
                        strategy=new Strategy();
                        strategy.account=account;
                    }
                    if (params.type=='between'){
                        strategy.min=params.min?.toString()?.toFloat()?.round(0)*100;
                        strategy.min1=0.0f;
                        strategy.min2=0.0f;
                        strategy.phone1='';
                        strategy.phone2='';
                    }else if (params.type=='complex'){
                        if(params.min1&&params.min1!=""){
                            strategy.min1=params.min1?.toString()?.toFloat()?.round(0)*100;
                        }else{
                            strategy.min1=0.0f;
                        }
                        if(params.min2&&params.min2!=""){
                            strategy.min2=params.min2?.toString()?.toFloat()?.round(0)*100;
                        }else{
                            strategy.min2=0.0f;
                        }
                        strategy.phone1=params.phone1;
                        strategy.phone2=params.phone2;
                        strategy.min=0.0f;
                    }else{
                        strategy.min1=0.0f;
                        strategy.min2=0.0f;
                        strategy.phone1='';
                        strategy.phone2='';
                        strategy.min=0.0f;
                    }
                    strategy.type=params.type;
                    strategy.save(flush: true);
                    map.result=true;
                }else{
                    map.result=false;
                }
            }else{
                map.result=false;
            }
        }else{
            map.result=false;
        }
        println map as JSON;
        render map as JSON;
    }
    //提供手机检查服务器版本，若不同，则下载新版本安装
    def checkVersion={
       int currentVersion=16;
       String cpkUri="http://182.116.61.42:8888/NotifyServer/js/TradeConfirmor.apk";
       def map=[:]
       map.currentVersion=currentVersion;
       map.cpkUri=cpkUri;
       render map as JSON;
    }
    def enStr={
        SecurityEngine se=SecurityHelper.initSecurityEngine();
        def receptList=SecurityHelper.serverIBC;
        def securityMap=[:]
        securityMap
        def map=SecurityHelper.encrypt(se,receptList,"加密信息测试能否解密，服务器邮件标识，客户端手机标识",SecurityHelper.serverIBC);
        //println map
        println SecurityHelper.decrypt(se,map)
        render 'ok';
    }
    def deStr={
        def map=[:]
        map.length=163;
        map.data="2,34,-55,-105,-70,5,-99,48,66,42,-85,81,22,37,99,-35,3,-48,-121,-105,106,58,-39,-90,43,-8,-62,-69,2,25,-6,9,45,30,6,-27,-56,-45,25,-65,-93,-104,47,116,104,-63,-40,-113,-29,58,87,37,-16,-16,-97,-34,5,29,120,127,63,-125,83,-28,-77,89,48,104,16,32,4,77,0,-34,99,32,43,40,33,-8,112,100,47,8,-109,-114,16,-99,-69,-14,85,-124,-15,-42,100,122,126,63,28,-80,46,28,123,99,-73,17,56,107,68,-87,-105,18,23,122,119,86,-111,-94,-27,103,-96,-28,-39,-63,123,-123,-122,-107,70,17,0,-41,24,51,-82,-68,-36,80,0,112,-3,-126,42,101,-89,24,112,61,-105,36,-93,-128,-79,-51,37,114,-109,-36,76,-112,26,104,-74"
        //SecurityEngine se=SecurityHelper.initSecurityEngine();

    }

    def startSocketServer={
        def smap=[:]
        def oneRAPDU=[:]//一条接受的apdu
        oneRAPDU["dataSize"]=0
        if(!SocketServerStatus.isRuning1){
            runAsync {
                def server = new ServerSocket(SocketServerStatus.apdu_port);
                while(true) {
                    server.accept { Socket socket ->
                       // socket.setSoTimeout(9*60*1000);
                        socket.withStreams { InputStream input,OutputStream output ->
                            String currentClientSeqNo='';
                            boolean isResult=false;//是否接受返回的结果值
                            boolean isConfirm = false;//是否有交易信息
                            boolean isReturn = false;//是否返回结果值；返回值为true才能发另外一条；每次发送设置为false等待接收
                            boolean confirmFlag = false;//当有确认消息过来时设置为true;
                            def map = [:];
                            ConnectionFactory jms = new ActiveMQConnectionFactory(brokerURL: "tcp://127.0.0.1:61616");
                            use(JMS){
                                jms.topic('socketNotify').subscribe(
                                    {Message m ->
                                        def json=JSON.parse(m.text);
                                        println 'socketNotify======================================'
                                        def phone;
                                        Account.withNewSession {
                                            phone = Account.findByNumber(json.account)?.phone?.number;
                                        }
                                        println "currentClientSeqNo================="+currentClientSeqNo;
                                        println "phone================="+phone;
                                        if(phone==currentClientSeqNo){
                                            map.phone = phone;
                                            //获取info信息并存储到map里
                                            map.uuid=json.uuid;
                                            map.bankId=json.bankId;
                                            map.id=json.id;
                                            map.trCode=json.trCode;
                                            map.seqNo=json.seqNo;
                                            map.chnNo=json.chnNo;
                                            map.account = json.account;
                                            map.acc= json.acc;
                                            map.money=json.money;
                                            map.payee=json.payee;
                                        }
                                        def seqNo=json.seqNo;
                                        def money = json.money?.longValue().toString();;
                                        if(money.size()>2){
                                            money="${money[0..(money.size()-3)]}元${money[-2]}角${money[-1]}分"
                                        }else{
                                            if (money.size()>1){
                                                money="${money[0]}角${money[1]}分"
                                            }else{
                                                money="${money}分"
                                            }
                                        }
                                        def content = "您的${map.acc}通过${InfoController.pickUpPayType(map.chnNo)}方式,支付 ${money} 给 ${map.payee}.请确认?";
                                        println json;
//                                        byte[] moneys = HexUtil.str2Bcd((money).padLeft(12,'0'));
//                                       moneys = byteMerger(HexString2Bytes('1006'),moneys);
//                                        byte[] seqNos = HexUtil.str2Bcd((seqNo).padLeft(12,'0'));
//                                        seqNos = byteMerger(HexString2Bytes('1106'),seqNos);
                                        byte[] date = content.getBytes('GBK');
                                        date =  byteMerger(HexString2Bytes('16'+date.size().encodeAsHex().padLeft(2,'0')),date);
//                                        byte [] temp =   byteMerger(byteMerger(moneys,seqNos),date);
                                        byte [] temp = date;
                                        byte[] cudp =  byteMerger(HexString2Bytes('00D00000'+temp.size().encodeAsHex().padLeft(2,'0')),temp);
                                        byte[] bys =  byteMerger(HexString2Bytes(cudp.size().encodeAsHex().padLeft(4,'0')),cudp);
                                        map.phone = phone;
                                        if(phone==currentClientSeqNo){
                                            def  str = "000D00A4040008A000000001336f78";//选择指令信息
                                            try{
                                                //output.write(HexString2Bytes(str));
                                                confirmFlag = true;//核心系统有确认消息发送
                                                if(isReturn){//是否已接收返回值；如果接收到返回值则发送确认信息
                                                     output.write(bys);//发送确认信息
                                                     isReturn = false;//等待返回值
                                                     isConfirm=true;//有确认消息发送；
                                                     
                                                }else if(!isReturn){//如果没有接收到返回值
                                                     sleep(1000);//等待1s，等待已发送的心跳接收返回值；
                                                     if(isReturn){
                                                          output.write(bys);//发送确认信息
                                                          isReturn = false;//等待返回值
                                                          isConfirm=true;//有确认消息发送；
                                                     }
                                                }
                                                
                                            }catch(SocketException se){
                                                println se.message;
                                                output.close();
                                                input.close();
                                                socket.close();
                                            }
                                        }
//                                        if(json.chnNo=='posc'){
//                                            def  str = "000D00A4040008A000000001336f78";
//                                            output.write(HexString2Bytes(str));
//                                        }else{
//                                            println bys;
//                                            println bys.size();
//                                            println cudp.size();
//                                            output.write(bys);
//                                        }
                                    } as MessageListener);
                            }

                            def i = 0;
                            oneRAPDU["dataSize"]=0
                            oneRAPDU["str"]='';
                            input.eachByte {item->
                                println item.encodeAsHex()+'======='+i;
                                oneRAPDU["str"]=oneRAPDU["str"]+item.encodeAsHex();
                                if(oneRAPDU["dataSize"]==0){
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1;
                                    oneRAPDU["length"]=item.encodeAsHex();
                                }else if(oneRAPDU["dataSize"]==1){
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1;
                                    oneRAPDU["length"]=oneRAPDU["length"]+item.encodeAsHex();
                                 //   oneRAPDU["length"]=Integer.parseInt(oneRAPDU["length"]);
                                 //   println oneRAPDU["length"];
                                    oneRAPDU["length"]=Integer.parseInt(oneRAPDU["length"],16);
                                    println "length==================="+ oneRAPDU["length"]
                                }else if(oneRAPDU["dataSize"]==2){
                                    if(isResult){
                                        oneRAPDU["result1"]=item.encodeAsHex();
                                    }
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1;
                                    oneRAPDU["CLAByte"]=item
                                    oneRAPDU["CLA"]=item.encodeAsHex()
                                }
                                else if(oneRAPDU["dataSize"]==3){
                                    if(isResult){
                                        oneRAPDU["result1"]=oneRAPDU["result1"]+item.encodeAsHex();
                                        println "result1======================"+ oneRAPDU["result1"];
                                        oneRAPDU["dataSize"]=0;//重新接收值；
                                        def result = "no";
                                         boolean confirm = false;
                                         if(oneRAPDU["result1"]=="9000"){//确认结果为9000是确认成功
                                             result="yes";
                                             println "result==true"
                                             confirm=true;
                                             isReturn = true;//已接收返回值成功
                                         }else if(oneRAPDU["result1"]=="9300"){//确认结果为9000是确认失败
                                             println "result==false"
                                             result = "no";
                                             confirm=true;
                                             isReturn = true;//已接收返回值成功
                                         }
                                         oneRAPDU["result1"]="";
                                        // oneRAPDU["result2"]="";
     
                                         if(isConfirm && confirm && map.phone==currentClientSeqNo){//是确认结果值且返回值为9000
                                            isConfirm = false;//重置交易信息为false;
                                            confirmFlag = false;//确认信息已经发送
                                             def info;
                                             Info.withNewSession {
                                                 info=Info.findByUuid(map.uuid.toString());
                                                 saveInfo(info,map.phone,result);//保存确认结果
                                             }
                                             boolean socketFlag =sendResult(info);
                                             //发送确认消息给核心系统
                                             if(socketFlag){
                                                 def qmap=[:]
                                                 qmap.uuid=map.uuid;
                                                 qmap.result=result; //
                                                 qmap.bankId=map.bankId;
                                                 qmap.id=map.id;
                                                 qmap.trCode=map.trCode;
                                                 qmap.seqNo=map.seqNo;
                                                 qmap.chnNo=map.chnNo;
                                                 qmap.action="PushInfo";
                                                 def json=qmap as JSON;
                                                 sendPubSubJMSMessage("socketTopic", json.toString());
                                             }
                                          }
                                    /****************************确认结果返回指令结束*************************************/
                                       def str1 = "000D00A4040008A000000001336f78";
                                        if(!confirmFlag && isReturn){//核心系统没有确认消息且已接收返回值；如果有核心系统确认消息则不发送心跳包
                                             output.write(HexUtil.HexString2Bytes(str1));//返回启用会话结果指令给确认器
                                             isReturn = false;//等待返回值
                                        }
                                        
                                        
                                    }
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1
                                    oneRAPDU["INSByte"]=item
                                    oneRAPDU["INS"]=item.encodeAsHex()
                                }
                                else if(oneRAPDU["dataSize"]==4){
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1
                                    oneRAPDU["P1Byte"]=item
                                    oneRAPDU["P1"]=item.encodeAsHex()
                                }
                                else if(oneRAPDU["dataSize"]==5){
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1
                                    oneRAPDU["P2Byte"]=item
                                    oneRAPDU["P2"]=item.encodeAsHex()
                                }
                                else if(oneRAPDU["dataSize"]==6){
                                    if(isResult){
                                        oneRAPDU["result2"]=item.encodeAsHex();
                                    }
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1
                                    oneRAPDU["LCByte"]=item
                                    oneRAPDU["LC"]=Integer.parseInt(item.encodeAsHex(), 16)
                                    println "lc===================="+oneRAPDU["LC"]
                                    oneRAPDU["dataHex"]=''
                                }
                                else if(oneRAPDU["dataSize"]>6 && oneRAPDU["dataSize"]<(oneRAPDU["length"]+1)){
                                    if(oneRAPDU["dataSize"]==7){

                                        oneRAPDU["ACCLEN"]=Integer.parseInt(item.encodeAsHex(), 16);//获取主账户长度
                                        oneRAPDU["ACCNO"]='';
                                    }else{
                                        if(oneRAPDU["dataSize"]>7&&oneRAPDU["dataSize"]<oneRAPDU["ACCLEN"]+8){
                                            oneRAPDU["ACCNO"]=oneRAPDU["ACCNO"]+item.encodeAsHex();//获取主账户
                                        }
                                        oneRAPDU["dataHex"]=oneRAPDU["dataHex"]+item.encodeAsHex();
                                    }
                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1;
                                }
                                else if(oneRAPDU["dataSize"]>6 && oneRAPDU["dataSize"]==(oneRAPDU["length"]+1)){        //处理完成1条APDU
                                    oneRAPDU["dataHex"]=oneRAPDU["dataHex"]+item.encodeAsHex();
                                    println oneRAPDU["str"];
                                    currentClientSeqNo=oneRAPDU["ACCNO"].replaceAll("f","");
                                    println 'Account'+"============================="+currentClientSeqNo;
                                    println 'SES_KEY'+"============================="+ oneRAPDU["dataHex"];
                                    def str="";
                                    def str1="";
                                    if(oneRAPDU["INS"].toString().toUpperCase()=='A5'){
                                        str="00029000";
                                        str1 = "000D00A4040008A000000001336f78";
                                    }
                                    oneRAPDU=[:];    //逻辑处理完成后清空
                                    oneRAPDU["dataSize"]=0;
                                    isResult=true;//设置接受返回结果指令
                                    output.write(HexUtil.HexString2Bytes(str));//返回启用会话结果指令给确认器：即发送心跳包
                                    isReturn = false;//等待返回值


                                    /*********************确认器测试。实际部署需删除这段代码-begin***********************************/
//                                    Thread.sleep(1000);
//                                    output.write(HexUtil.HexString2Bytes(str1));//发送选择指令
//                                    /*****************发送确认信息*****************/
//                                    def seqNo="000000000071";
//                                    def money = "500";
//                                    def content = "您的123456通过pos机方式,支付5.00给王某，请确认?";
////                                    def content = "您的${map.account.toString()}通过${InfoController.pickUpPayType()(map.chnNo)}方式,支付 ${map.money} 给 ${map.payee}.请确认?";
//                                    byte[] moneys = HexUtil.str2Bcd((money).padLeft(12,'0'));
//                                    moneys = byteMerger(HexString2Bytes('1006'),moneys);
//                                    byte[] seqNos = HexUtil.str2Bcd((seqNo).padLeft(12,'0'));
//                                    seqNos = byteMerger(HexString2Bytes('1106'),seqNos);
//                                    byte[] date = content.getBytes('GBK');
//                                    date = byteMerger(HexString2Bytes('16'+date.size().encodeAsHex().padLeft(2,'0')),date);
//                                    byte[] temp = date;
////                                    byte[] temp = byteMerger(byteMerger(moneys,seqNos),date);
//                                    byte[] cudp = byteMerger(HexString2Bytes('00D00000'+temp.size().encodeAsHex().padLeft(2,'0')),temp);
//                                    byte[] bys = byteMerger(HexString2Bytes(cudp.size().encodeAsHex().padLeft(4,'0')),cudp);
////                                    Thread.sleep(1000);
//                                    output.write(bys);
                                /******************************确认器测试。实际部署需删除这段代码-end****************************/
                                }
                             /*
                                else if(oneRAPDU["dataSize"]>6&&oneRAPDU["dataSize"]==7){
//                                    oneRAPDU["dataSize"]=oneRAPDU["dataSize"]+1;
                                    //确认结果返回指令开始
                                   
                                    oneRAPDU["dataSize"]=0
                                    def result = "no";
                                    if(isResult){
                                        oneRAPDU["result2"]=oneRAPDU["result2"]+item.encodeAsHex();
                                        println "result2======================"+ oneRAPDU["result2"]
                                    }
                                    boolean confirm = false;
                                    if(oneRAPDU["result2"]=="9000"){//确认结果为9000是确认成功
                                        result="yes";
                                        println "result==true"
                                        confirm=true;
                                    }else if(oneRAPDU["result2"]=="9300"){//确认结果为9000是确认失败
                                        println "result==false"
                                        result = "no";
                                        confirm=true;
                                    }
                                    oneRAPDU["result1"]="";
                                    oneRAPDU["result2"]="";

                                    if(confirm && map.phone==currentClientSeqNo){
                                        def info;
                                        Info.withNewSession {
                                            info=Info.findByUuid(map.uuid.toString());
                                            saveInfo(info,map.phone,result);//保存确认结果
                                        }
                                        boolean socketFlag =sendResult(info);
                                        //发送确认消息给核心系统
                                        if(socketFlag){
                                            def qmap=[:]
                                            qmap.uuid=map.uuid;
                                            qmap.result=result; //
                                            qmap.bankId=map.bankId;
                                            qmap.id=map.id;
                                            qmap.trCode=map.trCode;
                                            qmap.seqNo=map.seqNo;
                                            qmap.chnNo=map.chnNo;
                                            qmap.action="PushInfo";
                                            def json=qmap as JSON;
                                            sendPubSubJMSMessage("socketTopic", json.toString());
                                        }
                                    }
                                    
                                    //确认结果返回指令结束
                                }
                              */
                                i++;
                            }

                        }
                    }
                }
                /*
            //   s = new Socket("210.76.97.183", 8222);
   s = new Socket("localhost", 8222);
               s.setSoTimeout(9*60*1000);
               s.withStreams { input, output ->
                   PrintStream ps = new PrintStream(output,true,"GBK");
                   def str='00820144454444'
                   //register
                 //  str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>Registration</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><name>张强</name><identificationType>身份证</identificationType><identificationNo>110110090000101200</identificationNo><phone>13999888888</phone><account>6228000100001117</account></request>'
                  // UNregister
                  // str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>Unregistration</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><name>张强</name><identificationType>身份证</identificationType><identificationNo>110110090000101200</identificationNo><phone>13999888888</phone><account>6228000100001117</account></request>'
                  // str=(str.getBytes('GBK').size().toString().padLeft(8,'0')+str)
                   //output << "*bye*\n"
                   byte[] bytes = [24, -128, -91, 1, 1, 19, 10, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 18, 34, 34, 34, 34, 34, 34, 34, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
                  output.write(bytes);
                   reader = input.newReader('UTF-8');
                   int charone
                  StringBuffer sb=new StringBuffer();
                  while((charone=reader.read())!=-1){
                       sb<<(char)charone;
                       println sb.toString();
                  }
               }
 s = new Socket("localhost", 8222);
               s.setSoTimeout(9*60*1000);
               s.withStreams { input, output ->
                   PrintStream ps = new PrintStream(output,true,"GBK");
                   def str='00820144454444'
                   //register
                 //  str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>Registration</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><name>张强</name><identificationType>身份证</identificationType><identificationNo>110110090000101200</identificationNo><phone>13999888888</phone><account>6228000100001117</account></request>'
                  // UNregister
                  // str='ST1201305221759271234567000000000071posc******<?xml version="1.0" encoding="UTF-8" standalone="no" ?><request><action>Unregistration</action><username>hbBank</username><password>defaultPasswd</password><bankId>1</bankId><name>张强</name><identificationType>身份证</identificationType><identificationNo>110110090000101200</identificationNo><phone>13999888888</phone><account>6228000100001117</account></request>'
                  // str=(str.getBytes('GBK').size().toString().padLeft(8,'0')+str)
                   //output << "*bye*\n"
                   byte[] bytes =[0, 24, -128, -91, 1, 1, 19, 10, 17, 17, 17, 17, 17, 17, 17, 17, 17, 17, 34, 34, 34, 34, 34, 34, 34, 34];
                  output.write(bytes);
                   reader = input.newReader('UTF-8');
                   int charone=0;
                   byte[] buff = new byte[1024];
                  while ((charone = input.read(buff)) > 0)
                    {
                    println buff;
                  }
               }
               * */
            }
            SocketServerStatus.isRuning1=true;
            smap.result=true;
            smap.message="SocketServer服务启动成功！";
        }else{
            smap.result=false;
            smap.message="SocketServer服务已经启动！";
        }
        render smap as JSON;
    }
//    public static byte[] HexString2Bytes(String src){
//        byte[] ret = new byte[8];
//        byte[] tmp = src.getBytes();
//        for(int i=0; i<8; i++){
//            ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
//        }
//        return ret;
//    }

    public static String enUnicode(String content){//将汉字转换为16进制数
        String enUnicode=null;
        for(int i=0;i<content.length();i++){
            if(i==0){

                enUnicode=getHexString(Integer.toHexString(content.charAt(i)).toUpperCase());
            }else{
                enUnicode=enUnicode+getHexString(Integer.toHexString(content.charAt(i)).toUpperCase());
            }
        }
        return enUnicode;
    }

    private static String getHexString(String hexString){
        String hexStr="";
            for(int i=hexString.length();i<4;i++){
                if(i==hexString.length())
                    hexStr="0";
                else
                    hexStr=hexStr+"0";
            }
            return hexStr+hexString;
    }

    def hex={
        def seqNo="000000000071";
        def money = "500";
        def content = "您的123456通过pos机方式,支付5.00给王某，请确认?";
//                                    def content = "您的${map.account.toString()}通过${InfoController.pickUpPayType()(map.chnNo)}方式,支付 ${map.money} 给 ${map.payee}.请确认?";
        byte[] moneys = HexUtil.str2Bcd((money).padLeft(12,'0'));
        moneys = byteMerger(HexString2Bytes('1006'),moneys);
        byte[] seqNos = HexUtil.str2Bcd((seqNo).padLeft(12,'0'));
        seqNos = byteMerger(HexString2Bytes('1106'),seqNos);
        byte[] date = content.getBytes('GBK');
        date = byteMerger(HexString2Bytes('16'+date.size().encodeAsHex().padLeft(2,'0')),date);
        byte[] temp = byteMerger(byteMerger(moneys,seqNos),date);
//                                    byte[] cudp = byteMerger(HexString2Bytes('00D00000'+temp.size().encodeAsHex().padLeft(2,'0')),temp);
        byte[] cudp = date;
        byte[] bys = byteMerger(HexString2Bytes(cudp.size().encodeAsHex().padLeft(4,'0')),cudp);
        println bys;
//        println Integer.parseInt('18', 16)
//        println Integer.parseInt('0018', 16)
//        println Integer.parseInt('4745', 16)
//        println Integer.parseInt('86', 16)
//        byte bb = 24;
//        println byte2HexStr(bb)+'|||||||||||||||000000000000000'
//        println bb.encodeAsHex()+'|||||||||||||||000000000000000'
//        println HexUtil.str2Bcd(('500').padLeft(12,'0'))
//        println byteMerger(HexString2Bytes('1106'),HexUtil.str2Bcd(('500').padLeft(12,'0')))
//        println Integer.parseInt('A',16).byteValue().encodeAsHex();
//        def str="1880A50101130A1111111111111111111112222222222222222";
      // def str="00029000";
     //  def str="000D00A4040008A000000001336f78";
//       def str="4745";
       def str='001880a50101130a111111111111111111112222222222222222';
       println  HexString2Bytes(str);
//        def str1='1352561163';
//       println str2HexStr(str1);
//        println str2HexStr('2.4f')
//        println byte2HexStr(HexUtil.str2Bcd('2.4f'))
//        println HexString2Bytes(byte2HexStr(HexUtil.str2Bcd('2.4f')).replaceAll(' ',''))
       // println HexString2Bytes(HexUtil.byte2HexStr(HexUtil.str2Bcd('2.4f')));
     //   println HexUtil.byte2HexStr(HexUtil.HexString2Bytes(str));
       // println HexUtil.str2Bcd('2.4f'.padLeft(12,'0'));
      //  println HexUtil.bcd2Str(HexUtil.str2Bcd('2.4f'.padLeft(12,'0')));
      //  byte [] b = [19, -112, 96, -127, 50, 79];
        byte [] b =[0, 0, 0, 0, 2, 79];
        byte [] a =[23, 0];
        byte [] c =[13];
        byteMerger(HexString2Bytes('1106'),HexUtil.str2Bcd(('500').padLeft(12,'0'))).each {
            println it.encodeAsHex();
        }

       // println hexStr2Str(str);
//     byte[] b=  str.bytes;
//        println b;
//        for (int i = 0; i < b.length; i++) {
//            String hex = Integer.toHexString(b[i] & 0xFF);
//            if (hex.length() == 1) {
//                hex = '0' + hex;
//            }
//           System.out.print(hex.toUpperCase() );
//        }

        render 'ok';
    }
    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
    public static String cbcd2string(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int h = ((b[i]&0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (b[i] & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }
    public static byte[] str2cbcd(String s) {
        if (s.length() % 2 != 0) {
            s = "0" + s;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = s.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }
    public static String str2HexStr(String str)
        {
              char[] chars = "0123456789ABCDEF".toCharArray();
               StringBuilder sb = new StringBuilder("");
                byte[] bs = str.getBytes();
              int bit;
            for (int i = 0; i < bs.length; i++)
              {
                     bit = (bs[i] & 0x0f0) >> 4;
                    sb.append(chars[bit]);
                    bit = bs[i] & 0x0f;
                   sb.append(chars[bit]);
              sb.append(' ');
               }
             return sb.toString().trim();
         }
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(src0)).byteValue();
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(src1)).byteValue();
        byte ret = (byte)(_b0 ^ _b1);
        return ret;
    }

    public static byte[] HexString2Bytes(String src){
        byte[] ret = new byte[src.length()/2];
        byte[] tmp = src.getBytes();
        //    println src.length()/2;
        for(int i=0; i<src.length()/2; i++){
            ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
        }
        return ret;
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
