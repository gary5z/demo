/**
 * 
 */
package com.garyz.demo.rpc.rmi;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.garyz.demo.rpc.rmi.remote.RemoteService;

/**
 * @author zengzhiqiang
 * @version 2017年6月14日
 *
 */
public class RmiClientDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-rmi-client.xml");
		RemoteService service = (RemoteService)context.getBean("clientSideService");
		System.out.println(service.welcome());
	}

}
