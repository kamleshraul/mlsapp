package org.mkcl.els.hook.factory;

import org.mkcl.els.hook.IControllerHook;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class ControllerHookFactory {
	
	private final static String ADAPTER_CLASS = "org.mkcl.els.hook.adapter.ControllerHookAdapter";

	public static IControllerHook getControllerHook(String className) {
		
		IControllerHook hook = null;
		Object obj = null;
		
		try {
			obj = Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// In case parameter className does not map to any subclass of 
			// IControllerHook, return the reference to ControllerHookAdapter class
			try {
				obj = Class.forName(ADAPTER_CLASS).newInstance();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		if(obj instanceof IControllerHook) {
			hook = (IControllerHook) obj;
		}
		return hook;
	}
}
