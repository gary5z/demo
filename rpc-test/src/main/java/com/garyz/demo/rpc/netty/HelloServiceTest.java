/**
 * 
 */
package com.garyz.demo.rpc.netty;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.garyz.demo.rpc.netty.client.RpcProxy;
import com.garyz.demo.rpc.netty.server.service.HelloService;

/**
 * @author zengzhiqiang
 * @version 2017年6月15日
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-netty-client.xml")
public class HelloServiceTest {

	@Autowired
	private RpcProxy rpcProxy;

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-netty-client.xml");
		RpcProxy rpcProxy = ctx.getBean(RpcProxy.class);
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello("World");
		System.out.println(result);
	}

	@Test
	public void helloTest() {
		HelloService helloService = rpcProxy.create(HelloService.class);
		String result = helloService.hello("World");
		Assert.assertEquals("Hello! World", result);
	}
}
