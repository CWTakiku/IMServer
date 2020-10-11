package po;

import retrofit2.http.PUT;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {

    public static final int SUCCESS = 1;
    // 未知错误
    public static final int ERROR_UNKNOWN = 0;

    // 没有找到用户信息
    public static final int ERROR_NOT_FOUND_USER = 4041;
    // 没有找到群信息
    public static final int ERROR_NOT_FOUND_GROUP = 4042;
    // 没有找到群成员信息
    public static final int ERROR_NOT_FOUND_GROUP_MEMBER = 4043;

    // 创建用户失败
    public static final int ERROR_CREATE_USER = 3001;
    // 创建群失败
    public static final int ERROR_CREATE_GROUP = 3002;
    // 创建群成员失败
    public static final int ERROR_CREATE_MESSAGE = 3003;

    // 请求参数错误
    public static final int ERROR_PARAMETERS = 4001;
    // 请求参数错误-已存在账户
    public static final int ERROR_PARAMETERS_EXIST_ACCOUNT = 4002;
    // 请求参数错误-已存在名称
    public static final int ERROR_PARAMETERS_EXIST_NAME = 4003;

    // 服务器错误
    public static final int ERROR_SERVICE = 5001;

    // 账户Token错误，需要重新登录
    public static final int ERROR_ACCOUNT_TOKEN = 2001;
    // 账户登录失败
    public static final int ERROR_ACCOUNT_LOGIN = 2002;
    // 账户注册失败
    public static final int ERROR_ACCOUNT_REGISTER = 2003;
    // 没有权限操作
    public static final int ERROR_ACCOUNT_NO_PERMISSION = 2010;

    public static final int ERROR_UOLOAD_OSS = 6001;

    //创建社团错误
    public static final int ERROR_CREATE_COMMUNITY = 6002;


    /**
     * 1：成功，其他：失败
     */
    private int code;

    private String message;

    private T result;

    public int getCode() {
        return code;
    }

    public boolean isSuccess() {
        if (code == SUCCESS) {
            return true;
        }
        return false;
    }

    public void setErrorCode(int errorCode) {
        this.code = errorCode;
    }

    public String getErrorMsg() {
        return message;
    }

    public void setErrorMsg(String errorMsg) {
        this.message = errorMsg;
    }

    public T getData() {
        return result;
    }

    public void setData(T data) {
        this.result = data;
    }

}