package common;

import java.io.Serializable;

public class Response implements Serializable {
    private boolean ifRun=false;
    private String strErr=null;
    private String strRes=null;
    private Object objRes=null;

    public Response(boolean ifRun, String strErr, String strRes) {
        this.ifRun = ifRun;
        this.strErr = strErr;
        this.strRes = strRes;
    }

    public Response(boolean ifRun, String strErr, String strRes, Object objRes) {
        this.ifRun = ifRun;
        this.strErr = strErr;
        this.strRes = strRes;
        this.objRes = objRes;
    }

    public boolean isIfRun() {
        return ifRun;
    }

    public void setIfRun(boolean ifRun) {
        this.ifRun = ifRun;
    }

    public String getStrErr() {
        return strErr;
    }

    public void setStrErr(String strErr) {
        this.strErr = strErr;
    }

    public String getStrRes() {
        return strRes;
    }

    public void setStrRes(String strRes) {
        this.strRes = strRes;
    }

    public Object getObjRes() {
        return objRes;
    }

    public void setObjRes(Object objRes) {
        this.objRes = objRes;
    }
}
