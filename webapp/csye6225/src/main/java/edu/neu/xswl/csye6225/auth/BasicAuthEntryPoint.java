package edu.neu.xswl.csye6225.auth;

import com.alibaba.fastjson.JSON;
import org.apache.http.entity.ContentType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("csye6225");
        super.afterPropertiesSet();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        PrintWriter writer = response.getWriter();
        HashMap<String,String> map = new HashMap<>();
        map.put("message","unauthorized");
        writer.println(JSON.toJSONString(map));
    }
}
