package com.zl.chat.servlet;

import com.zl.chat.http.MyHttpRequest;
import com.zl.chat.http.MyHttpResponse;

/**
 * 自定义servlet
 *
 * @author zhuliang
 * @date 2019/6/22 23:55
 */
public class MyServlet {

    public void doGet(MyHttpRequest request, MyHttpResponse response) {
        doPost(request, response);
    }

    public void doPost(MyHttpRequest request, MyHttpResponse response) {
        if (request.getRequest()==null){
            response.write("");
            return;
        }
        response.write(request.getParameter("name"));
    }


}
