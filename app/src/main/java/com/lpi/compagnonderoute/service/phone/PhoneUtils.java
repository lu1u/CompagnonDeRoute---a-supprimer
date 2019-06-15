package com.lpi.compagnonderoute.service.phone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import com.lpi.reportlibrary.Report;

import java.lang.reflect.Method;

public class PhoneUtils
{
	public static void rejectCall(@NonNull final Context context)
	{
		try
		{
			TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			// Get the getITelephony() method
			Class<?> classTelephony = Class.forName(telephonyManager.getClass().getName());
			Method method = classTelephony.getDeclaredMethod("getITelephony");
			// Disable access check
			method.setAccessible(true);
			// Invoke getITelephony() to get the ITelephony interface
			Object telephonyInterface = method.invoke(telephonyManager);
			// Get the endCall method from ITelephony
			Class<?> telephonyInterfaceClass =Class.forName(telephonyInterface.getClass().getName());
			Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
			// Invoke endCall()
			methodEndCall.invoke(telephonyInterface);
		} catch (Exception e)
		{
			Report r = Report.getInstance(context);
			r.log(Report.NIVEAU.ERROR, "Erreur lors du rejet d'un appel");
			r.log(Report.NIVEAU.ERROR, e);
		}
	}
}
