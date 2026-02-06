package org.zeushotel.fastmart.nucleus.viewresponse;

import lombok.Data;

@Data
public class UnifiedApiResponse<T> {
    private Integer statusCode;
    private String messageText;
    private T responseData;
    
    public static <T> UnifiedApiResponse<T> success(T data) {
        UnifiedApiResponse<T> response = new UnifiedApiResponse<>();
        response.setStatusCode(200);
        response.setMessageText("操作成功");
        response.setResponseData(data);
        return response;
    }
    
    public static <T> UnifiedApiResponse<T> fail(String message) {
        UnifiedApiResponse<T> response = new UnifiedApiResponse<>();
        response.setStatusCode(400);
        response.setMessageText(message);
        return response;
    }
}
