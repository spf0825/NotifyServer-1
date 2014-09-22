package com.nisc;
import com.nisc.SecurityEngine;
import com.nisc.SecurityEngineException;

import java.io.IOException;
//import java.util.Scanner;
class SimpleThread extends Thread {
    public SecurityEngine se;
    public SimpleThread(SecurityEngine set)
    {
        se = set;
    }

    public void run(){
        while(true)
        {
            try
            {
                String plainText = "this_is_a_test_data";

                int[] cipherLen = new int[1];
                cipherLen[0] = 1024;
                byte[] decryptedData = new byte[1024];
                int[] decrtypedLen = new int[1];
                decrtypedLen[0] = 1024;

                se.EncryptP7Data("test@nisc.com", plainText.getBytes(), plainText.length(), null, cipherLen);
                //System.out.println("EncryptP7Data() OK, result length : " + cipherLen[0]);
                byte[] cipherData = new byte[cipherLen[0]];
                se.EncryptP7Data("test@nisc.com", plainText.getBytes(), plainText.length(), cipherData , cipherLen);
                //System.out.println("EncryptP7Data() OK, result length : " + cipherLen[0]);
                String recpList = se.GetStringRecpList(cipherData, cipherLen[0]);
                //System.out.println("GetStringRecpList() OK, recpList : " + recpList);

                se.DecryptP7Data(cipherData, cipherLen[0], decryptedData, decrtypedLen);
                //System.out.println("DecryptP7Data() OK, result length : " + decrtypedLen[0]);
                //System.out.print("DECRYPTED : ");
                /*for( int i=0; i<decrtypedLen[0]; i++ )
                            {
                                System.out.print((char)decryptedData[i]);
                            }
                            System.out.println("");
                            */
            }
            catch(Exception e)
            {

            }

        }
        //notify();
    }
}

public class SecurityEngineTest {

    private static void TestLoginSystemWithChap(SecurityEngine se) throws SecurityEngineException
    {
        String challengeData = "需要挑战的数据";
        String responseData = se.LoginSystemWithChap(challengeData);
        System.out.println("LoginSystemWithChap() OK : " + responseData);
    }



    private static void TestEncryptP7Data(SecurityEngine se) throws SecurityEngineException
    {
        String plainText = "this_is_a_test_data";

        int[] cipherLen = new int[1];
        cipherLen[0] = 1024;
        byte[] decryptedData = new byte[1024];
        int[] decrtypedLen = new int[1];
        decrtypedLen[0] = 1024;

        se.EncryptP7Data("test@nisc.com", plainText.getBytes(), plainText.length(), null, cipherLen);
        System.out.println("EncryptP7Data() OK, result length : " + cipherLen[0]);
        byte[] cipherData = new byte[cipherLen[0]];
        se.EncryptP7Data("test@nisc.com", plainText.getBytes(), plainText.length(), cipherData , cipherLen);
        System.out.println("EncryptP7Data() OK, result length : " + cipherLen[0]);
        String recpList = se.GetStringRecpList(cipherData, cipherLen[0]);
        System.out.println("GetStringRecpList() OK, recpList : " + recpList);

        se.DecryptP7Data(cipherData, cipherLen[0], decryptedData, decrtypedLen);
        System.out.println("DecryptP7Data() OK, result length : " + decrtypedLen[0]);
        System.out.print("DECRYPTED : ");
        for( int i=0; i<decrtypedLen[0]; i++ )
        {
            System.out.print((char)decryptedData[i]);
        }
        System.out.println("");
    }

    private static void TestEncryptData(SecurityEngine se) throws SecurityEngineException
    {
        String plainText = "this_is_a_test_data dddddddddddddddddddddddddddddddddddddddddddddffffffffffff";

        int[] cipherLen = new int[1];
        cipherLen[0] = 1024;
        byte[] decryptedData = new byte[1024];
        int[] decrtypedLen = new int[1];
        decrtypedLen[0] = 1024;

        se.CryptExportData("demo@olymtech.net", plainText.getBytes(), plainText.length(), null, cipherLen);
        System.out.println("EncryptP7Data() calc length OK, result length : " + cipherLen[0]);
        byte[] cipherData = new byte[cipherLen[0]];
        se.CryptExportData("demo@olymtech.net", plainText.getBytes(), plainText.length(), cipherData , cipherLen);
        System.out.println("EncryptP7Data() OK, result length : " + cipherLen[0]);

        se.CryptImportData(cipherData, cipherLen[0], decryptedData, decrtypedLen);
        System.out.println("DecryptP7Data() OK, result length : " + decrtypedLen[0]);
        System.out.print("DECRYPTED : ");
        for( int i=0; i<decrtypedLen[0]; i++ )
        {
            System.out.print((char)decryptedData[i]);
        }
        System.out.println("");
    }

    private static void TestEncryptP7File(SecurityEngine se) throws SecurityEngineException
    {
        String plainFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.java";
        String cipherFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.java.p7m";
        String decryptedFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.java.txt";

        se.EncryptP7File("test@nisc.com", plainFileName, cipherFileName);
        System.out.println("EncryptP7File() OK!");

        se.DecryptP7File(cipherFileName, decryptedFileName);
        System.out.println("DecryptP7File() OK!");

        String recpList = se.GetFileRecpList(cipherFileName);
        System.out.println("GetFileRecpList() OK, recpList : " + recpList);
    }

    private static void TestEncryptSMS(SecurityEngine se) throws SecurityEngineException
    {
        String plainText = "this_is_a_test_message";
        byte[] cipherData = new byte[1024];
        int[] cipherLen = new int[1];
        cipherLen[0] = 1024;
        byte[] decryptedData = new byte[1024];
        int[] decrtypedLen = new int[1];
        decrtypedLen[0] = 1024;

        se.EncryptSMS("test1@hnict.net", plainText.getBytes(), plainText.length(), cipherData, cipherLen);
        System.out.println("EncryptSMS() OK, result length : " + cipherLen[0]);

        se.DecryptSMS("test1@hnict.net", cipherData, cipherLen[0], decryptedData, decrtypedLen);
        System.out.println("DecryptSMS() OK, result length : " + decrtypedLen[0]);
        System.out.print("DECRYPTED : ");
        for( int i=0; i<decrtypedLen[0]; i++ )
        {
            System.out.print((char)decryptedData[i]);
        }
        System.out.println("");
    }

    private static void TestSignData(SecurityEngine se) throws SecurityEngineException
    {
        String plainText = "this_is_a_test_data";
        byte[] signedData = new byte[1024];
        int[] signedLen = new int[1];
        signedLen[0] = 1024;
        byte[] oriData = new byte[1024];
        int[] oriLen = new int[1];
        oriLen[0] = 1024;

        se.SignData(plainText.getBytes(), plainText.length(), se.SM_SIGNERTYPE_BLUE, signedData, signedLen);
        System.out.println("SignData() OK, result length : " + signedLen[0]);

        String signerList = se.VerifySignData(plainText.getBytes(), plainText.length(), signedData, signedLen[0], oriData, oriLen);
        //System.out.println(String.format("VerifySignData() OK, signerList : %s, result length : %d", signerList, oriLen[0]));
        System.out.print("ORIGINAL : ");
        for( int i=0; i<oriLen[0]; i++ )
        {
            System.out.print((char)oriData[i]);
        }
        System.out.println("");
    }

    private static void TestSignFile(SecurityEngine se) throws SecurityEngineException
    {
        String plainFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.java";
        String signedFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.java.p7s";
        String originalFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.revert.txt";

        se.SignFile(plainFileName, se.SM_SIGNERTYPE_TRANS, signedFileName);
        System.out.println("SignFile() OK!");

        String signerList = se.VerifySignFile(plainFileName, signedFileName, originalFileName);
        System.out.println("VerifySignFile() OK!, signerList : " + signerList);
    }

    private static void TestEncryptSignData(SecurityEngine se) throws SecurityEngineException
    {
        String plainText = "this_is_a_test_data";
        byte[] cipherData = new byte[1024];
        int[] cipherLen = new int[1];
        cipherLen[0] = 1024;
        byte[] decryptedData = new byte[1024];
        int[] decrtypedLen = new int[1];
        decrtypedLen[0] = 1024;

        se.EncryptSignData("test@nisc.com", plainText.getBytes(), plainText.length(), cipherData, cipherLen);
        System.out.println("EncryptSignData() OK, result length : " + cipherLen[0]);

        String signerList = se.DecryptVerifyData(cipherData, cipherLen[0], decryptedData, decrtypedLen);
        System.out.println("DecryptVerifyData() OK, signerList : "+signerList+", result length : " + decrtypedLen[0]);
        System.out.print("DECRYPTED : ");
        for( int i=0; i<decrtypedLen[0]; i++ )
        {
            System.out.print((char)decryptedData[i]);
        }
        System.out.println("");
    }

    private static void TestEncryptSignFile(SecurityEngine se) throws SecurityEngineException
    {
        String plainFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.java";
        String cipherFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.es.p7m";
        String decryptedFileName = "F:\\dir.work\\nisc-sdk\\SecureToolkit.es.txt";

        se.EncryptSignFile("test@nisc.com", plainFileName, cipherFileName);
        System.out.println("EncryptP7File() OK!");

        String signerList = se.DecryptVerifyFile(cipherFileName, decryptedFileName);
        System.out.println("DecryptVerifyFile() OK, signerList : "+signerList);
    }

    private static void TestGetVerifyCode(SecurityEngine se) throws SecurityEngineException
    {
        se.GetVerifyCode("13502881686", "7654321");
        System.out.println("GetVerifyCode() OK!");
    }

    private static void TestActiveMobileIdAndDownloadKey(SecurityEngine se) throws SecurityEngineException
    {
        se.ActiveMobileIdAndDownloadKey("13502881686", "7654321", "208095");
        System.out.println("ActiveMobileIdAndDownloadKey() OK!");
    }

    private static void TestGetMemberStatus(SecurityEngine se) throws SecurityEngineException
    {
        int ikeystate = se.GetMemberStatus("13502881686");
        System.out.println("GetMemberStatus() OK, keyState : " + ikeystate);
    }

    public static void pressEnterToContinue() {
        System.out.println("Press ENTER to continue...");
        try {
            System.in.read();
            while (System.in.available() > 0)
                System.in.read(); //flush the buffer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void TestNTLS(SecurityEngine se) throws SecurityEngineException
    {
        int proxyHandle = se.InitNTLSProxy(se.NLTS_MAX_CLIENT_NTLSCONNS, 128, 2);
        System.out.println("InitNTLSProxy() OK!");

        se.AddNTLSProxyModule(proxyHandle, se.NTLS_PROXY_PORT_SOCKS, se.NTLS_PROXY_MODULE_SOCKS);
        System.out.println("AddNTLSProxyModule() OK!");

        int ntlsConnection = se.ConnectNTLSServer(proxyHandle, "124.232.130.66", 3000, null,
                se.ALG_DEM2_AES128_CBC_HMAC_SHA1, true, "test1@hnict.net");
        System.out.println("ConnectNTLSServer() OK!");

        pressEnterToContinue();

        se.DisconnectNTLSServer(proxyHandle, ntlsConnection);
        System.out.println("DisconnectNTLSServer() OK!");

        se.DestroyNTLSProxy(proxyHandle);
        System.out.println("DestroyNTLSProxy() OK!");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println(System.getProperty("java.library.path"));
        try {
            SecurityEngine se = SecurityEngine.getInstance();


            System.out.println("test start");


            se.LoginLocalDevice(0, "", "");
            se.DownloadMailKey("demo@olymtech.net", "123456");

            /*
               SimpleThread thread1 = new SimpleThread();
               thread1.se = se;

               thread1.start();

               SimpleThread thread2 = new SimpleThread();
               thread2.se = se;

               thread2.start();

               while(true){//等待所有子线程执行完
                   if(!thread1.hasThreadRunning()){
                   break;
                   }
                   if(!thread2.hasThreadRunning()){
                   break;
                   }

               Thread.sleep(500);
               }

                        int SIZE = 50;
            ExecutorService exec = Executors.newFixedThreadPool(SIZE);

            try {
                for (int index = 0; index < SIZE; index++) {
                    exec.execute(new SimpleThread(se));
                    Thread.sleep(2);
                    System.out.println("main in : " + index);
                }

                exec.shutdown();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

               System.out.println("GetDevicePrivateInfo() **********************************************OK");

               String sChallenge = se.GenChallenge();
               String sResponse = se.LoginSystemWithChap(sChallenge);
               String sUser = se.VerifyChap(sResponse, sChallenge);
               System.out.println("VerifyChap sUser is" + sUser);
               System.out.println("GetDevicePrivateInfo() OK, ret : " + se.GetDevicePrivateInfo());
               */

            TestEncryptData(se);
            TestEncryptP7Data(se);
            //TestEncryptP7File(se);
            //TestEncryptSMS(se);
            TestSignData(se);
            //TestSignFile(se);
            //TestEncryptSignData(se);
            //TestEncryptSignFile(se);
            //TestGetVerifyCode(se);
            //TestActiveMobileIdAndDownloadKey(se);
            //TestGetMemberStatus(se);
            //TestNTLS(se);
        } catch (SecurityEngineException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("ERROR : " + e.getStatus());
        }
    }

}
