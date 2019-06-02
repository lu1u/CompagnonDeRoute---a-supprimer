package com.lpi.compagnonderoute.service.phone;

import android.content.Context;
import android.telephony.TelephonyManager;
import java.lang.reflect.Method;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class Phone
{
	public static void rejectCall(Context context)
	{
//		try
//		{
//			TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//			// Get the getITelephony() method
//			Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
//			Method method = classTelephony.getDeclaredMethod("getITelephony");
//			// Disable access check
//			method.setAccessible(true);
//			// Invoke getITelephony() to get the ITelephony interface
//			Object telephonyInterface = method.invoke(telephonyManager);
//			// Get the endCall method from ITelephony
//			Class<?> telephonyInterfaceClass =Class.forName(telephonyInterface.getClass().getName());
//			Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
//			// Invoke endCall()
//			methodEndCall.invoke(telephonyInterface);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		//}
	}
}
