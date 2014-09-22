package com.nisc;

public class SecurityEngineEvent {

    public static void NtlsBroken(int hProxyClient, int hNTLSConnect)
    {
        System.out.println(String.format("NTLS is broken, hProxyClient : %d, hNTLSConnect : %d",
                hProxyClient, hNTLSConnect));
    }
}
