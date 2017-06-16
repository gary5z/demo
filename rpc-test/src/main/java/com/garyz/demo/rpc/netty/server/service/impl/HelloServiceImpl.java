/**
 * 
 */
package com.garyz.demo.rpc.netty.server.service.impl;

import com.garyz.demo.rpc.netty.server.service.HelloService;
import com.garyz.demo.rpc.netty.server.service.RpcService;

/**
 * @author zengzhiqiang
 * @version 2017年6月15日
 *
 */
@RpcService(HelloService.class) // 指定远程接口
public class HelloServiceImpl implements HelloService {

	public String hello(String name) {
		return "Hello! " + name;
	}

}
