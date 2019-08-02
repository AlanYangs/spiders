package com.alany.spider.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alany on 2017/8/15.
 */
public class ReflectUtils {

	/**
	 * 按给定对象instance执行方法
	 * @param className
	 * @param instance
	 * @param methodName
	 * @param params
	 * @return
	 */
    public static Object excMethodByObject(String className, Object instance, String methodName, Object... params) {
        Object returnObj = null;
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName);
            returnObj = method.invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObj;
    }
    /**
     * 使用默认newInstance()方法获取对象调用方法
     * @param className
     * @param methodName
     * @param params
     * @return
     */
    public static Object excMethodByString(String className, String methodName, Object... params) {
        Object returnObj = null;
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.newInstance();
            Method method = clazz.getDeclaredMethod(methodName);
            returnObj = method.invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (returnObj == null) {
            return "error";
        }
        return returnObj;
    }
    /**
     * 获取所有的参数，包含private参数
     * @param className
     * @return
     */
    public static List<String> getAllFieldNames(String className){
    	List<String> fields = new ArrayList<String>();
        try {
            Class<?> clazz = Class.forName(className);
            for (Field field : clazz.getDeclaredFields()) {
				fields.add(field.getName());
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    public static List<Field> getAllFields(String className){
        List<Field> fields = new ArrayList<Field>();
        try {
            Class<?> clazz = Class.forName(className);
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }
    /**
     * 获取所有方法名，包含private方法
     * @param className
     * @return
     */
    public static List<String> getDeclaredMethods(String className){
    	List<String> methods = new ArrayList<String>();
        try {
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getDeclaredMethods()) {//包含private方法
            	methods.add(method.getName());
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methods;
    }
    
    /**
     * 获取所有方法名，只包含public方法
     * @param className
     * @return
     */
    public static List<String> getMethods(String className){
    	List<String> methods = new ArrayList<String>();
        try {
            Class<?> clazz = Class.forName(className);
            for (Method method : clazz.getMethods()) {//只包含public方法
            	methods.add(method.getName());
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return methods;
    }

}
