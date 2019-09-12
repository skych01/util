package http;


/**
 * @author chen
 * 用于封装对外接口的返回结果返回格式如下：
 *      成功：
 *           {
 *                  success：true,
 *                  message:null,
 *                  content:body   (相应内容)
 *           }
 *
 */
public class JSONResult {
    private boolean success;
    private String message;
    private Object content;

    public JSONResult(){}

    public JSONResult(boolean success, String message, Object content) {
        this.setSuccess(success);
        this.setMessage(message);
        this.setContent(content);
    }

    public static JSONResult success(Object content) {
        return new JSONResult(Boolean.TRUE, null, content);
    }

    public static JSONResult success() {
        return success(null);
    }

    public static JSONResult fail(String message) {
        return new JSONResult(Boolean.FALSE, message, null);
    }
    public static JSONResult fail() {
        return new JSONResult(Boolean.FALSE, null, null);
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "JSONResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", content=" + content +
                '}';
    }

    public static void main(String[] args) {
        JSONResult jsonResult = JSONResult.success("content");
        System.out.println(jsonResult);
    }


}
