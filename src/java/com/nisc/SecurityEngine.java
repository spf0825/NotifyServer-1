package com.nisc;

//import com.nisc.SecurityEngineException;

public class SecurityEngine {

    private static final SecurityEngine instance  = new SecurityEngine();
    private static boolean libLoaded = false;
    private static boolean isInited = false;

    public final int SM_SIGNERTYPE_BLUE					= 1;	//模糊签名
    public final int SM_SIGNERTYPE_TRANS				= 0;	//透明签名

    public final int CONST_PRIVATETYPE_PKI				= 0;

    public final int SM_OPERATIONTYPE_BASE				= 0;
    public final int SM_OPERATIONTYPE_ENCRYPT			= 1;	//纯加密
    public final int SM_OPERATIONTYPE_ENCRYPTSIGNER		= 2;	//加密模糊签名
    public final int SM_OPERATIONTYPE_TRANSSIGNER		= 3;	//透明签名
    public final int SM_OPERATIONTYPE_SIGNER			= 4;	//模糊签名

    public final int NTLS_PROXY_PORT_SOCKS				= 1080;
    public final int NTLS_PROXY_MODULE_SOCKS			= 0x0003;

    public final int NLTS_MAX_CLIENT_NTLSCONNS			= 4;

    public final int ALG_DEM2_AES128_CBC_HMAC_SHA1		= 0xE0021891;
    public final int ALG_DEM2_RC264_CBC_HMAC_SHA1		= 0xE0021891;
    public final int ALG_DEM2_RC4_HMAC_SHA1				= 0xE0021891;
    public final int ALG_MAC_HMAC_SHA1					= 0xE0021891;

    public int m_hEngine = 0;

    private SecurityEngine() {}

    static
    {
        try{
            System.loadLibrary("SecurityEngine4j");
            libLoaded = true;
        }
        catch (UnsatisfiedLinkError e)
        {
            System.out.println("Could not load SecurityEngine4j library!");
            System.out.println(e.getMessage());
        }
    }

    public static SecurityEngine getInstance() throws SecurityEngineException
    {
        if(libLoaded)
        {
            if( !isInited )
            {
                instance.m_hEngine = instance.createSecurityEngine();
                isInited = true;
            }
            return instance;
        }
        else
            return null;
    }

    public void exitInstance()
    {
        if( m_hEngine > 0 )
            destroySecurityEngine(m_hEngine);
    }

    public void ChangeCurrentUser(
            String userID
    )  throws SecurityEngineException
    {
        changeCurrentUser(m_hEngine, userID);
    }

    public void SetIBCServer(
            String ibcServerAddr
    )  throws SecurityEngineException
    {
        setIBCServer(m_hEngine, ibcServerAddr);
    }

    public void SetIBCParamFile(
            String ibcParamFile
    )  throws SecurityEngineException
    {
        setIBCParamFile(m_hEngine, ibcParamFile);
    }

    public String GetDevicePrivateInfo()  throws SecurityEngineException
    {
        return getDevicePrivateInfo(m_hEngine);
    }

    public String GenChallenge() throws SecurityEngineException
    {
        return genChallenge(m_hEngine);
    }

    public String LoginSystemWithChap(
            String challengeData
    )  throws SecurityEngineException
    {
        return loginSystemWithChap(m_hEngine, challengeData);
    }

    public String VerifyChap(
            String responseData,
            String challengeData
    )  throws SecurityEngineException
    {
        return verifyChap(m_hEngine, responseData, challengeData);
    }

    public String VerifyChapEx(
            String responseData,
            String challengeData,
            String paramFileName
    )  throws SecurityEngineException
    {
        return verifyChapEx(m_hEngine, responseData, challengeData, paramFileName);
    }

    public void LoginLocalDevice(
            int deviceSlot,
            String userID,
            String password
    )  throws SecurityEngineException
    {
        loginLocalDevice(m_hEngine, deviceSlot, userID, password);
    }

    public void LoginLocalFile (
            String userID,
            String password
    )  throws SecurityEngineException
    {
        loginLocalFile(m_hEngine, userID, password);
    }

    public void EncryptP7Data (
            String recpList,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {

        encryptP7Data(m_hEngine, recpList, inputData, inputLen, outputData, outputLen);
    }

    public void CryptExportData(
            String recpList,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) throws SecurityEngineException
    {
        cryptExportData(m_hEngine, recpList, outputData, outputLen, inputData, inputLen);
    }

    private native void cryptExportData(int handle,	String recpList, byte[] outputData,	int[] outputLen, byte[] inputData, int inputLen);

    public void CryptImportData(
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) throws SecurityEngineException
    {
        cryptImportData(m_hEngine, outputData, outputLen, inputData, inputLen);
    }

    private native void cryptImportData(int handle,	byte[] outputData,	int[] outputLen, byte[] inputData, int inputLen);

    public void CryptSignData(
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) throws SecurityEngineException
    {
        cryptSignData(m_hEngine, outputData, outputLen, inputData, inputLen);
    }

    private native void cryptSignData(int handle,byte[] outputData,	int[] outputLen, byte[] inputData, int inputLen);

    public void CryptVerifySignData(
            byte[] inputData,
            int inputLen,
            byte[] signerData,
            int signerLen,
            String signer
    ) throws SecurityEngineException
    {
        cryptVerifySignData(m_hEngine, inputData, inputLen, signerData, signerLen, signer);
    }

    private native void cryptVerifySignData(int handle, byte[] inputData, int inputLen, byte[] signerData, int signerLen, String signer);

    public void DecryptP7Data (
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {
        decryptP7Data(m_hEngine, inputData, inputLen, outputData, outputLen);
    }

    public void EncryptP7File (
            String recpList,
            String inputFileName,
            String outputFileName
    )  throws SecurityEngineException
    {
        encryptP7File(m_hEngine, recpList, inputFileName, outputFileName);
    }

    public void DecryptP7File (
            String inputFileName,
            String outputFileName
    )  throws SecurityEngineException
    {
        decryptP7File(m_hEngine, inputFileName, outputFileName);
    }

    public void EncryptSMS (
            String recpId,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {
        encryptSMS(m_hEngine, recpId, inputData, inputLen, outputData, outputLen);
    }

    public void DecryptSMS (
            String senderId,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {
        decryptSMS(m_hEngine, senderId, inputData, inputLen, outputData, outputLen);
    }

    public void SignData (
            byte[] inputData,
            int inputLen,
            int operationFlag,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {
        signData(m_hEngine, inputData, inputLen, operationFlag, outputData, outputLen);
    }

    public String VerifySignData (
            byte[] signedData,
            int signedDataLen,
            byte[] dataString,
            int dataLen,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {
        return verifySignData(m_hEngine, signedData, signedDataLen, dataString, dataLen, outputData, outputLen);
    }

    public void SignFile (
            String inputFileName,
            int operationFlag,
            String outputFileName
    )  throws SecurityEngineException
    {
        signFile(m_hEngine, inputFileName, operationFlag, outputFileName);
    }

    public String VerifySignFile (
            String signedFileName,
            String dataFileName,
            String outputFileName
    )  throws SecurityEngineException
    {
        return verifySignFile(m_hEngine, signedFileName, dataFileName, outputFileName);
    }

    public String GetFileRecpList (
            String fileName
    )  throws SecurityEngineException
    {
        return getFileRecpList(m_hEngine, fileName);
    }

    public String GetStringRecpList (
            byte[] inputString,
            int inputLen
    )  throws SecurityEngineException
    {
        return getStringRecpList(m_hEngine, inputString, inputLen);
    }

    public void EncryptSignData (
            String recpList,
            byte[] inputString,
            int inputLen,
            byte[] outputString,
            int[] outputLen
    )  throws SecurityEngineException
    {
        encryptSignData(m_hEngine, recpList, inputString, inputLen, outputString, outputLen);
    }

    public String DecryptVerifyData (
            byte[] inputString,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    )  throws SecurityEngineException
    {
        return decryptVerifyData(m_hEngine, inputString, inputLen, outputData, outputLen);
    }

    public void EncryptSignFile (
            String recpList,
            String inputFileName,
            String outputFileName
    )  throws SecurityEngineException
    {
        encryptSignFile(m_hEngine, recpList, inputFileName, outputFileName);
    }

    public String DecryptVerifyFile (
            String inputFileName,
            String outputFileName
    )  throws SecurityEngineException
    {
        return decryptVerifyFile(m_hEngine, inputFileName, outputFileName);
    }

    public void ChangePassword (
            String oldPassword,
            String newPassword
    )  throws SecurityEngineException
    {
        changePassword(m_hEngine, oldPassword, newPassword);
    }

    public void ResetDevicePin (
            int deviceSlot,
            String managerPinCode,
            String userPinCode
    )  throws SecurityEngineException
    {
        resetDevicePin(m_hEngine, deviceSlot, managerPinCode, userPinCode);
    }

    public void Logout()  throws SecurityEngineException
    {
        logout(m_hEngine);
    }

    // 私钥下载相关

    public void GetVerifyCode (
            String memberID,
            String memberPassword
    )  throws SecurityEngineException
    {
        getVerifyCode(m_hEngine, memberID, memberPassword);
    }

    public void ActiveMobileIdAndDownloadKey (
            String memberID,
            String memberPassword,
            String verifyCode
    )  throws SecurityEngineException
    {
        activeMobileIdAndDownloadKey(m_hEngine, memberID, memberPassword, verifyCode);
    }

    public int GetMemberStatus (
            String memberID
    )  throws SecurityEngineException
    {
        return getMemberStatus(m_hEngine, memberID);
    }

    public void RegisterMailId (
            String memberID,
            String memberPassword
    )  throws SecurityEngineException
    {
        registerMailId(m_hEngine, memberID, memberPassword);
    }

    public void DownloadMailKey (
            String memberID,
            String memberPassword
    )  throws SecurityEngineException
    {
        downloadMailKey(m_hEngine, memberID, memberPassword);
    }

    // NTLS

    public int InitNTLSProxy (
            int contextNum,
            int connectNum,
            int moduleNum
    )  throws SecurityEngineException
    {
        return initNTLSProxy(m_hEngine, contextNum, connectNum, moduleNum);
    }

    public void DestroyNTLSProxy (
            int proxyHandle
    )  throws SecurityEngineException
    {
        destroyNTLSProxy(m_hEngine, proxyHandle);
    }

    public void AddNTLSProxyModule (
            int proxyHandle,
            int proxyPort,
            int moduleId
    )  throws SecurityEngineException
    {
        addNTLSProxyModule(m_hEngine, proxyHandle, proxyPort, moduleId);
    }

    public void RemoveNTLSProxyModule (
            int proxyHandle,
            int moduleId
    )  throws SecurityEngineException
    {
        removeNTLSProxyModule(m_hEngine, proxyHandle, moduleId);
    }

    public int ConnectNTLSServer(
            int proxyHandle,
            String serverIP,
            int serverPort,
            String serverId,
            int cipherAlg,
            boolean compressed,
            String userId
    )  throws SecurityEngineException
    {
        return connectNTLSServer(m_hEngine, proxyHandle, serverIP, serverPort, serverId, cipherAlg, compressed, userId);
    }

    public void DisconnectNTLSServer(
            int proxyHandle,
            int ntlsConnection
    )  throws SecurityEngineException
    {
        disconnectNTLSServer(m_hEngine, proxyHandle, ntlsConnection);
    }

    public int ReconnectNTLSServer(
            int proxyHandle,
            int ntlsConnection,
            String serverIP,
            int serverPort
    )  throws SecurityEngineException
    {
        return reconnectNTLSServer(m_hEngine, proxyHandle, ntlsConnection, serverIP, serverPort);
    }

    public void NtlsBroken(int hProxyClient, int hNTLSConnect)
    {
        //System.out.println(String.format("NTLS is broken, hProxyClient : %d, hNTLSConnect : %d",
        //			hProxyClient, hNTLSConnect));
    }

    //------------------------

    private native int createSecurityEngine() ;

    private native void destroySecurityEngine(
            int handle
    ) ;

    private native void changeCurrentUser(
            int handle,
            String userID
    ) ;

    private native void setIBCParamFile(
            int handle,
            String ibcParamFile
    ) ;

    private native void setIBCServer(
            int handle,
            String ibcServerAddr
    ) ;

    private native String getDevicePrivateInfo(
            int handle
    ) ;

    private native String genChallenge(
            int handle
    );

    private native String loginSystemWithChap(
            int handle,
            String challengeData
    ) ;

    private native String verifyChap(
            int handle,
            String responseData,
            String challengeData
    )	;

    private native String verifyChapEx(
            int handle,
            String responseData,
            String challengeData,
            String paramFileName
    )	;

    private native void loginLocalDevice(
            int handle,
            int deviceSlot,
            String userID,
            String password
    ) ;

    private native void loginLocalFile (
            int handle,
            String userID,
            String password
    ) ;

    private native void encryptP7Data (
            int handle,
            String recpList,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native void decryptP7Data (
            int handle,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native void encryptP7File (
            int handle,
            String recpList,
            String inputFileName,
            String outputFileName
    ) ;

    private native void decryptP7File (
            int handle,
            String inputFileName,
            String outputFileName
    ) ;

    private native void encryptSMS (
            int handle,
            String recpId,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native void decryptSMS (
            int handle,
            String senderId,
            byte[] inputData,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native void signData (
            int handle,
            byte[] inputData,
            int inputLen,
            int operationFlag,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native String verifySignData (
            int handle,
            byte[] signedData,
            int signedDataLen,
            byte[] dataString,
            int dataLen,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native void signFile (
            int handle,
            String inputFileName,
            int operationFlag,
            String outputFileName
    ) ;

    private native String verifySignFile (
            int handle,
            String signedFileName,
            String dataFileName,
            String outputFileName
    ) ;

    private native String getFileRecpList (
            int handle,
            String fileName
    ) ;

    private native String getStringRecpList (
            int handle,
            byte[] inputString,
            int inputLen
    ) ;

    private native void encryptSignData (
            int handle,
            String recpList,
            byte[] inputString,
            int inputLen,
            byte[] outputString,
            int[] outputLen
    ) ;

    private native String decryptVerifyData (
            int handle,
            byte[] inputString,
            int inputLen,
            byte[] outputData,
            int[] outputLen
    ) ;

    private native void encryptSignFile (
            int handle,
            String recpList,
            String inputFileName,
            String outputFileName
    ) ;

    private native String decryptVerifyFile (
            int handle,
            String inputFileName,
            String outputFileName
    ) ;

    private native void changePassword (
            int handle,
            String oldPassword,
            String newPassword
    ) ;

    private native void resetDevicePin (
            int handle,
            int deviceSlot,
            String managerPinCode,
            String userPinCode
    ) ;

    private native void logout (
            int handle
    ) ;

    private native void getVerifyCode (
            int handle,
            String memberID,
            String memberPassword
    ) ;

    private native void activeMobileIdAndDownloadKey (
            int handle,
            String memberID,
            String memberPassword,
            String verifyCode
    ) ;

    private native int getMemberStatus (
            int handle,
            String memberID
    ) ;

    private native void registerMailId (
            int handle,
            String memberID,
            String memberPassword
    ) ;

    private native void downloadMailKey (
            int handle,
            String memberID,
            String memberPassword
    ) ;

    // NTLS
    private native int initNTLSProxy (
            int handle,
            int contextNum,
            int connectNum,
            int moduleNum
    ) ;

    private native void destroyNTLSProxy (
            int handle,
            int proxyHandle
    ) ;

    private native void addNTLSProxyModule (
            int handle,
            int proxyHandle,
            int proxyPort,
            int moduleId
    ) ;

    private native void removeNTLSProxyModule (
            int handle,
            int proxyHandle,
            int moduleId
    ) ;

    private native int connectNTLSServer(
            int handle,
            int proxyHandle,
            String serverIP,
            int serverPort,
            String serverId,
            int cipherAlg,
            boolean compressed,
            String userId
    ) ;

    private native void disconnectNTLSServer(
            int handle,
            int proxyHandle,
            int ntlsConnection
    ) ;

    private native int reconnectNTLSServer(
            int handle,
            int proxyHandle,
            int ntlsConnection,
            String serverIP,
            int serverPort
    ) ;
}