/**
 * 
 */
package com.garyz.demo.rpc.rmi.service.impl;

import com.garyz.demo.rpc.rmi.service.MyService;

/**
 * @author zengzhiqiang
 * @version 2017年6月14日
 *
 */
public class MyServiceImpl implements MyService{
    public boolean inviteMeIn() {
        return true;
    }
    public String welcome() {
        return "Everybody is welcome!!";
    }
}
