package com.example.activiti.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author:郭友文
 * @Data:2018/8/24 15:42
 * @Description:
 */
@Component
public class SpringUtil implements ApplicationContextAware {
        /**
         * 当前IOC
         *
         */
        private static ApplicationContext applicationContext;

        /**
         * * 设置当前上下文环境，此方法由spring自动装配
         *
         */
        @Override
        public void setApplicationContext(ApplicationContext arg0)
                throws BeansException {
            applicationContext = arg0;
        }

        /**
         * 从当前IOC获取bean
         *
         * @param id
         * bean的id
         * @return
         *
         */
        public static Object getObject(String id) {
            Object object = null;
            object = applicationContext.getBean(id);
            return object;
        }
        public static <T> T getBean(Class<T> requiredType) {
            return applicationContext.getBean(requiredType);
        }
}

