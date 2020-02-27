// ISecurityCenter.aidl
package com.zzr.binderpooldemo;

// Declare any non-default types here with import statements

interface IBinderPool {
    IBinder query(int binderCode);
}
