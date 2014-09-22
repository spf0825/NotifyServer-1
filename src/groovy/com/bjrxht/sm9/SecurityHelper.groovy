package com.bjrxht.sm9

import com.nisc.SecurityEngine
import com.nisc.SecurityEngineException;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-6-8
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */
//安全辅助类
class SecurityHelper {
    public static String serverIBC="notifyServer@capinfo.com.cn";
    public static String serverPass="notifyServer";

    private static boolean isInit=false;
    public static SecurityEngine initSecurityEngine(){
        //return null;
        SecurityEngine se = SecurityEngine.getInstance();
        if(!isInit){
            isInit=true;

            String info=se.GetDevicePrivateInfo();
            def list=info.tokenize(";");
            se.LoginLocalDevice(0,serverIBC,serverPass);
            //se.LoginLocalDevice(0, "", "");
            //Unknow reason,can't skip downloadmailkey
            //if(list.size()>2 && list[2].isNumber() && list[2].toInteger()>0){
            //}else{
            //se.DownloadMailKey(serverIBC,serverPass);
            //}

        }

        return se;
    }
    public static releaseSecurityEngine(SecurityEngine se){
        se.Logout();
        se.exitInstance();
        se = null;
    }
    /*
    public static Map encrypt(SecurityEngine se,String receptList,String plainText){
        def map=[:]
        map.data=plainText;
        map.length=plainText.length();
        return map;
    }
   */
    public static Map encrypt(SecurityEngine se,String receptList,String plainText,String signUser){
        def map=[:]
        /*
        map.data=plainText;
        map.length=plainText.size();
        map.verifyData='';
        map.verifyLength=0;
        map.signUser=signUser;
        return map;
         */
        byte[] cipherData = new byte[1024];
        int[] cipherLen = new int[1];
        cipherLen[0] = 1024;
        int[] cipherLen1 = new int[1];
        cipherLen1[0] = 1024;
        //se.CryptExportData(receptList,plainText.getBytes('UTF-8'), plainText.getBytes('UTF-8').size(), cipherData, cipherLen);
        se.CryptExportData(receptList, plainText.getBytes('UTF-8'), plainText.getBytes('UTF-8').length, null, cipherLen)
        //System.out.println("CryptExportData() calc length OK, result length : " + cipherLen[0]);;
        cipherData = new byte[cipherLen[0]];
        se.CryptExportData(receptList, plainText.getBytes('UTF-8'), plainText.getBytes('UTF-8').length, cipherData, cipherLen);
        //System.out.println("CryptExportData() OK, result length : " + cipherLen[0]);
        //签名
        se.CryptSignData(cipherData, cipherLen[0], null, cipherLen1);
        byte[] cipherData1 = new byte[cipherLen1[0]];
        se.CryptSignData(cipherData, cipherLen[0], cipherData1, cipherLen1);
        //println  transBase64ToUrl(new BASE64Encoder().encode(cipherData))
        map.data=transBase64ToUrl(new BASE64Encoder().encode(cipherData));
        map.length=cipherLen[0];
        map.verifyData=transBase64ToUrl(new BASE64Encoder().encode(cipherData1));
        map.verifyLength=cipherLen1[0];
        map.signUser=signUser;
        /*
        byte[] subData=new byte[cipherLen[0]];
        for(int i=0;i<cipherLen[0];i++){
            subData[i]=cipherData[i];
        }
        map.data=subData.toList().collect{it.toString()}.join(',');
        */
        return map;
    }

    /*
   public static String decrypt(SecurityEngine se,String base64Str,int strLength){
       return base64Str;
   }
   */
   public static String decrypt(SecurityEngine se,Map map){
       //return  map.data;
       //
       byte[] decryptedData = new byte[1024];
       byte[] cipherData = new byte[1024];
       byte[] cipherData1 = new byte[1024];
       int[] decrtypedLen = new int[1];
       int[] cipherLen = new int[1];
       decrtypedLen[0] = 1024;
       cipherLen[0] = map.length.toInteger();
       //println(transUrlToBase64(map.data))
       //println  new BASE64Decoder().decodeBuffer(transUrlToBase64(map.data))
       cipherData=new BASE64Decoder().decodeBuffer(transUrlToBase64(map.data));
       int[] cipherLen1 = new int[1];
       cipherLen1[0] = map.verifyLength.toInteger();
       cipherData1=new BASE64Decoder().decodeBuffer(transUrlToBase64(map.verifyData));
       /*
       def abcList=base64Str.tokenize(',').collect{Byte.valueOf(it)}
       abcList.eachWithIndex {b,i->
           cipherData[i]=b;
       }
       */
       try{
           //println("before verify");
           se.CryptVerifySignData(cipherData, cipherLen[0], cipherData1, cipherLen1[0],map.signUser);
           //println("after verify");
           se.CryptImportData(cipherData, cipherLen[0], decryptedData, decrtypedLen);
           //println("after decrypt");
       }catch(Exception e){
           println "verify failure ${e.message}"
           return null;
       }
       return new String(decryptedData,0,decrtypedLen[0],'UTF-8');
   }

// 从适用于URL的Base64编码字符串转换为普通字符串
    public static String transUrlToBase64(String urlString)
    {
        return urlString.replaceAll('\\.', '=').replaceAll('\\*', '+').replaceAll('\\-', '/');
    }
// 从普通字符串转换为适用于URL的Base64编码字符串
    public static String transBase64ToUrl(String base64String)
    {
        return base64String.replaceAll('\\+', '*').replaceAll('\\/', '-').replaceAll('\\=', '.');
    }
}
