package com.nisc;

public class SecurityEngineException extends Exception{

    private static final long serialVersionUID = 1L;
    private int m_status;
    private String m_message;
    private int m_errdata;

    private String errnum2str(int errnum)
    {
        if( errnum == -2001 )
            return "找不到指定的设备";
        else if( errnum == -2002 )
            return "找不到指定的标识";
        else if( errnum == -2003 )
            return "找不到指定的引擎";
        else if( errnum == -2004 )
            return "必须先登录";
        else if( errnum == -2005 )
//            return "必须使用设备中的用户先进行登录";
            return "设备中未检测到有私钥，请先下载私钥";
        else if( errnum == -2006 )
            return "设备已经打开，不允许进行此项操作";
        else if( errnum == -2007 )
            return "不能够对登录的设备进行此项操作";
        else if( errnum == -2008 )
            return "错误的编码";
        else if( errnum == -2009 )
            return "传入的参数为空";
        else if( errnum == -2010 )
            return "缓冲区不能为空";
        else if( errnum == -2011 )
            return "输入的密码长度不正确";
        else if( errnum == -2012 )
            return "当前平台不支持此操作";

        else if( errnum == -2014 )
            return "当前模式下不支持此操作";
        else if( errnum == -2015 )
            return "使用之前必须使用 InitSecurityEngine 进行初始化, 仅在SYMBIAN中";
        else if( errnum == -2016 )
            return "产品试用时间已过";

        else if( errnum == -2017 )
            return "本地时间与服务器时间相差超过一天，请更改本地时间";
        else if( errnum == -2018 )
            return "不支持指定的用途";
        else if( errnum == -2019 )
            return "未设置应用的ID，对于公网的私钥，您必须设置此值。";
        else if( errnum == -2020 )
            return "应用程序的类型设置错误";
        else if( errnum == -2021 )
            return "没有相应的数据";
        else if( errnum == -1234 )
            return "参数错误";
        else if( errnum == -24 )
            return "设备操作失败";
        else if( errnum == -93 )
            return "密码错误";
        else if( errnum == 10)
            return "安全应用的Id非法";
        else if( errnum == 11)
            return "安全应用的密码错误";
        else if( errnum == 12)
            return "enterpriseId或enterprisePassword错误";
        else if( errnum == 13)
            return "transactionId错误";
        else if( errnum == 20 )
            return "用户不存在";
        else if( errnum == 21 )
            return "用户已存在";
        else if( errnum == 22 )
            return "用户密码错误";
        else if( errnum == 23 )
            return "密钥有效期错误";
        else if( errnum == 24 )
            return "密钥用途非法";
        else if( errnum == 25 )
            return "密钥生成错误";
        else if( errnum == 26 )
            return "用户已被禁用";
        else if( errnum == 27 )
            return "设置用户状态失败";
        else if( errnum == 28 )
            return "获取系统参数失败";
        else if( errnum == 29 )
            return "密钥可生产次数非法";
        else if( errnum == 33 )
            return "用户未经验证";
        else if( errnum == 34 )
            return "用户私钥已过期";
        else if( errnum == 35 )
            return "服务器端未设置随机数或SESSION已经过期";
        else if( errnum == 36 )
            return "服务器端取得签名属性失败";
        else if( errnum == 37 )
            return "服务器端检查签名数据失败";
        else if( errnum == 38 )
            return "服务器验证签名数据失败";
        else if( errnum == 39 )
            return "操作员不存在";
        else if( errnum == 40 )
            return "操作员状态不合法";
        else if( errnum == 41 )
            return "此平台已经取消此WEBSERVICE";
        else if( errnum == 60 )
            return "非法手机号码标识";
        else if( errnum == 61 )
            return "未获取手机验证码";
        else if( errnum == 62 )
            return "手机验证码错误";
        else if( errnum == 63 )
            return "私钥可下载次数为零";
        else if( errnum == 64 )
            return "非法的私钥标识";
        else if( errnum == 65 )
            return "手机标识已验证成功,请收取验证码,并下载私钥";
        else if( errnum == 66 )
            return "私钥已挂失";
        else if( errnum == 67 )
            return "非法私钥下载介质";
        else if( errnum == 68 )
            return "设备序列号不合法或为空";
        else if( errnum == 107 )
            return "连接被拒绝";
        else if( errnum == -501 )
            return "密钥设备已满";
        else if( errnum == 1004 )
            return "SOAP接口协议出错";
        else if (errnum == 10049)
            return "请求的地址无效";
        else if (errnum == 10035)
            return "无法立即完成一个非阻挡性套接字操作";
        else if (errnum == 11165)
            return "套接字操作尝试一个无法连接的主机";
        else if (errnum == 1007)
            return "InternetConnect错误";
        else if( errnum == -100 )
            return "Could not load system library";
        else
            return "未知错误 : "+errnum;
    }

    public SecurityEngineException(int status)
    {
        m_status = status;
        m_message = errnum2str(m_status);
        m_errdata = 0;
    }

    public SecurityEngineException(int status, String message)
    {
        m_status = status;
        m_message = message;
    }

    public SecurityEngineException(int status, int errdata)
    {
        m_status = status;
        m_errdata = errdata;
        m_message = errnum2str(m_status);
    }

    public int getStatus()
    {
        return m_status;
    }

    public int getErrdata()
    {
        return m_errdata;
    }

    public String getMessage()
    {
        return m_message;
    }

}
