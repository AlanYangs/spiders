package com.alany.spider.bean;

import com.alany.spider.utils.ReflectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alany on 2019/7/19.
 */
public class BaseBean {

    @Override
    public String toString(){
        String className = this.getClass().getName();
        List<Field> fields = ReflectUtils.getAllFields(className);
        StringBuilder sb = new StringBuilder();
        if(fields == null || fields.size() < 1){
            return "";
        }
        sb.append(this.getClass().getSimpleName()).append("[");

        for (Field field : fields){
            Object value;
            if(field.getGenericType().toString().equals("boolean")){
                value = ReflectUtils.excMethodByObject(className, this,"is" + StringUtils.capitalize(field.getName()));
            }else{
                value = ReflectUtils.excMethodByObject(className, this, "get" + StringUtils.capitalize(field.getName()));
            }

            sb.append(field.getName() + ":" + value + ",");
        }
        sb.setLength(sb.length()-1);
        sb.append("]");

        return sb.toString();
    }

    public Map<String, Object> toMap(){
        String className = this.getClass().getName();
        List<Field> fields = ReflectUtils.getAllFields(className);
        if(fields == null || fields.size() < 1){
            return null;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        for (Field field : fields){
            if ("objectId".equals(field.getName())){ //排除逻辑id
                continue;
            }
            Object value;
            if(field.getGenericType().toString().equals("boolean")){
                value = ReflectUtils.excMethodByObject(className, this,"is" + StringUtils.capitalize(field.getName()));
            }else{
                value = ReflectUtils.excMethodByObject(className, this, "get" + StringUtils.capitalize(field.getName()));
            }

            if (value == null || "".equals(value)) { //排除value为空的属性
                continue;
            }

            map.put(field.getName(), value);
        }
        return map;
    }

}
