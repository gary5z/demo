/**
 * 
 */
package com.garyz.demo.rpc.netty;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author zengzhiqiang
 * @version 2017年6月14日
 *
 */
public class RpcBootstrap {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring-netty-server.xml");
	}
}
