/**
 * 
 */
package com.garyz.demo.rpc.netty.server;

/**
 * @author zengzhiqiang
 * @version 2017年6月15日
 *
 */
public class RpcResponse {

	private String requestId;
	private boolean isError;
	private Throwable error;
	private Object result;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable error) {
		this.error = error;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
