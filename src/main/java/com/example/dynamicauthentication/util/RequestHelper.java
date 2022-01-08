package com.example.dynamicauthentication.util;

import com.example.dynamicauthentication.entity.Endpoint;
import org.reflections.Reflections;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RequestHelper {
    private static final String CONTROLLER_PACKAGE_NAME = "com.example.dynamicauthentication.controller";

    public static List<Endpoint> scanEndpoints() {
        List<Endpoint> res = new ArrayList<Endpoint>();
        Reflections reflections = new Reflections(CONTROLLER_PACKAGE_NAME);
        try {
            Set<Class<?>> listControllers = reflections.getTypesAnnotatedWith(RestController.class);
            for (Class controller : listControllers) {
                if (!controller.isAnnotationPresent(RequestMapping.class)) {
                    continue;
                }
                RequestMapping controllerRequestMapping = (RequestMapping) controller.getAnnotation(RequestMapping.class);
                String controllerPath = controllerRequestMapping.value()[0];
                String controllerName = controller.getSimpleName();
                System.out.println(controllerName);
                //get method
                Method[] declaredMethods = controller.getDeclaredMethods();
                for (Method method : declaredMethods) {
                    if (!method.isAnnotationPresent(RequestMapping.class)) {
                        continue;
                    }
                    RequestMapping methodRequestMapping = method.getDeclaredAnnotation(RequestMapping.class);
                    String methodPath = methodRequestMapping.value()[0];
                    RequestMethod requestMethod = methodRequestMapping.method()[0];
                    String httpMethod = requestMethod.name();
                    String name = method.getName();
                    Endpoint endpoint = new Endpoint();
                    endpoint.setMethod(httpMethod);
                    endpoint.setName(name);
                    endpoint.setPath(controllerPath.concat(methodPath));
                    res.add(endpoint);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return res;
    }

    public static HashMap<String, ArrayList<Endpoint>> detectChanges(List<Endpoint> prev, List<Endpoint> current) {
        ArrayList<Endpoint> added = new ArrayList<>();
        ArrayList<Endpoint> updated = new ArrayList<>();
        ArrayList<Endpoint> unChanged = new ArrayList<>();
        HashMap<String, Endpoint> prevEndPointsMap = new HashMap<>();
        for (Endpoint endPoint : prev) {
            String key = endPoint.getKey();
            prevEndPointsMap.put(key, endPoint);
        }
        for (Endpoint endpoint : current) {
            String currentKey = endpoint.getKey();
            if(prevEndPointsMap.containsKey(currentKey)) {
                Endpoint prevItem = prevEndPointsMap.get(currentKey);
                if(endpoint.equals(prevItem)) {
                    unChanged.add(endpoint);
                } else {
                    endpoint.setId(prevEndPointsMap.get(currentKey).getId() );
                    updated.add(endpoint);
                }
                prevEndPointsMap.remove(currentKey);
            } else {
                added.add(endpoint);
            }
        }
        ArrayList<Endpoint> deleted = new ArrayList<>(prevEndPointsMap.values());
        HashMap<String, ArrayList<Endpoint>> res = new HashMap<>();
        res.put("added", added);
        res.put("updated", updated);
        res.put("unchanged", unChanged);
        res.put("deleted", deleted);
        return res;

    }
    public static void main(String[] args) {
        List<Endpoint> endpoints = scanEndpoints();
        for (Endpoint ep :
                endpoints) {
            System.out.println(ep.getName() + " " + ep.getPath() + " " + ep.getMethod());
        }
    }
}
