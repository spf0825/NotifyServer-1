package com.bjrxht.notify

import grails.converters.JSON
import java.net.URLEncoder
import org.codehaus.groovy.grails.web.json.JSONElement;
import groovy.util.Node
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
//发送消息jpush 示例
class NoticeController {

    def index() { }
    def send={
        //http://api.jpush.cn:8800/sendmsg/v2/sendmsg
        //sendno
        //app_key
        //receiver_type   2:tag 3:alias 4:all
        //receiver_value
        //verification_code
        //msg_type  １、通知 ２、自定义消息（只有 Android 支持）
        //msg_content  {"n_content":"通知内容"}
        //platform

        int sendno=4;
        def url="http://api.jpush.cn:8800/sendmsg/v2/sendmsg";
        def app_key="a50acaf625bb9aa72c255638";
        def master_secret="afa348dcb7bccac6f748b372";
        def receiver_type=2;
        def receiver_value="pll";
        def verification_code="${sendno}${receiver_type}${receiver_value}${master_secret}".encodeAsMD5();
        def  msg_type=1;
        def  msg_content= '{"n_content":"通知内容aaa","n_title":"标题"}'
        def  platform="android,ios";
        def data= URLEncoder.encode("sendno", "UTF-8") + "=" + URLEncoder.encode("${sendno}", "UTF-8");
        data += "&" + URLEncoder.encode("app_key", "UTF-8") + "=" + URLEncoder.encode("${app_key}", "UTF-8");
        data += "&" + URLEncoder.encode("receiver_type", "UTF-8") + "=" + URLEncoder.encode("${receiver_type}", "UTF-8");
        data += "&" + URLEncoder.encode("receiver_value", "UTF-8") + "=" + URLEncoder.encode("${receiver_value}", "UTF-8");
        data += "&" + URLEncoder.encode("verification_code", "UTF-8") + "=" + URLEncoder.encode("${verification_code}", "UTF-8");
        data += "&" + URLEncoder.encode("msg_type", "UTF-8") + "=" + URLEncoder.encode("${msg_type}", "UTF-8");
        data += "&" + URLEncoder.encode("msg_content", "UTF-8") + "=" + URLEncoder.encode("${msg_content}", "UTF-8");
        data += "&" + URLEncoder.encode("platform", "UTF-8") + "=" + URLEncoder.encode("${platform}", "UTF-8");
        def json=this.postData(url,data,"POST",null);
        render json.errcode+":"+json.errmsg;
    }
    def create={
        String data= "name=${URLEncoder.encode('meeting','UTF-8')}&meetingID=${URLEncoder.encode('abc456','UTF-8')}&attendeePW=${URLEncoder.encode('111222','UTF-8')}&moderatorPW=${URLEncoder.encode('333444','UTF-8')}&welcome=${URLEncoder.encode('welcome','UTF-8')}&voiceBridge=72345";
        String str="create${data}"
        def address="http://119.255.34.118/bigbluebutton/api/create?${data}&checksum=${getCheckSum(str)}";
        def xml=this.xmlPostData(address,null,"GET",null);
        //println xml.returncode.toString();
        render xml.returncode.text();
    }
    def join={
        String data="meetingID=${URLEncoder.encode('abc456','UTF-8')}&fullName=${URLEncoder.encode('喻户一致','UTF-8')}&password=${URLEncoder.encode('111222','UTF-8')}";
        String str="join${data}"
        def address="http://119.255.34.118/bigbluebutton/api/join?${data}&checksum=${getCheckSum(str)}";
        println "joinAddress=${address}"
        def xml=this.xmlPostData(address,null,"GET",null);
        //println xml.returncode;
        render xml.returncode.text();
    }
    def manage={
        String data="meetingID=${URLEncoder.encode('abc456','UTF-8')}&password=${URLEncoder.encode('333444','UTF-8')}&fullName=${URLEncoder.encode('发言人','UTF-8')}";
        String str="join${data}"
        def address="http://119.255.34.118/bigbluebutton/api/join?${data}&checksum=${getCheckSum(str)}";
        println "manageAddress=${address}"
        def xml=this.xmlPostData(address,null,"GET",null);
        render xml.returncode.text();
    }
    def isRun={
        String data="meetingID=${URLEncoder.encode('abc456','UTF-8')}";
        String str="isMeetingRunning${data}"
        def address="http://119.255.34.118/bigbluebutton/api/isMeetingRunning?${data}&checksum=${getCheckSum(str)}";
        def xml=this.xmlPostData(address,null,"GET",null);
        render xml.returncode.text();
    }
    def end={
        String data="meetingID=${URLEncoder.encode('abc456','UTF-8')}&password=${URLEncoder.encode('333444','UTF-8')}";
        String str="end${data}"
        def address="http://119.255.34.118/bigbluebutton/api/end?${data}&checksum=${getCheckSum(str)}";
        def xml=this.xmlPostData(address,null,"GET",null);
        render xml.returncode.text();
    }
    private String getCheckSum(string){
        //String string="createname=abc456&meetingID=abc456&attendeePW=111222&moderatorPW=333444";
        String salt="b8e1cd380e70f0dd4debf97ae77026be";    //b8e1cd380e70f0dd4debf97ae77026be
        //println DigestUtils.shaHex(string + salt);
        //def mdpe=new MessageDigestPasswordEncoder("SHA-1");
        //println  mdpe.encodePassword("${string}${salt}",null);
        //println "${string}${salt}".encodeAsSHA1();
        return "${string}${salt}".encodeAsSHA1();
    }
    private Node xmlPostData(address,data,method,cookie){
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
            if (data){
                conn.outputStream.withWriter('utf-8'){writer->
                    writer.write(data);
                }
            }
            String str=conn.inputStream.getText('utf-8');
            println str
            println "******************"
            return new XmlParser().parseText(str);
        } catch (Exception e) {
            println e.message
            return null;
            //view.trayIcon.displayMessage("错误",'服务器连接异常',ERROR)
            //view.optionPane.showMessageDialog(view.appWindow,'服务器连接异常',"error",JOptionPane.WARNING_MESSAGE)
        }
    }
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

    private JSONElement getJson(address,cookie){
        try {
            URL url = new URL(address);
            URLConnection conn = url.openConnection();
            if(cookie!=null){
                conn.setRequestProperty("Cookie", cookie);
            }
            conn.connect();
            return JSON.parse(conn.inputStream.getText('utf-8'));
        } catch (Exception e) {
            //view.trayIcon.displayMessage("错误",'服务器连接异常',ERROR)
            //view.optionPane.showMessageDialog(view.appWindow,'服务器连接异常',"error",JOptionPane.WARNING_MESSAGE)
        }
    }
}
