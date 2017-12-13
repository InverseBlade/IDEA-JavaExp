package common;

import java.io.Serializable;

public class Request implements Serializable {
    private String action=null;
    private String param=null;
    private Object objReq=null;

    public Request(String action, String param) {
        this.action = action;
        this.param = param;
    }

    public Request(String action, String param, Object objReq) {
        this.action = action;
        this.param = param;
        this.objReq = objReq;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Object getObjReq() {
        return objReq;
    }

    public void setObjReq(Object objReq) {
        this.objReq = objReq;
    }
}
