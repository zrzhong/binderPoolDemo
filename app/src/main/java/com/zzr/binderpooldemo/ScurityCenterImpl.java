package com.zzr.binderpooldemo;

import android.os.RemoteException;

public class ScurityCenterImpl extends ISecurityCenter.Stub {
    private static final char SECRETE_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRETE_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return password;
    }
}
