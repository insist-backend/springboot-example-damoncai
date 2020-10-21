package com.ximen.zuul.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.ximen.zuul.common.EncryptUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhishun.cai
 * @date 2020/7/23 14:35
 * @note
 */
public class AuthFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //1.获取令牌内容
        RequestContext ctx = RequestContext.getCurrentContext();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof OAuth2Authentication)){// 无token访问网关内资源的情况，目前仅有uua服务直接暴露
            return null;
        }
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication)authentication;
        Authentication userAuthentication = oauth2Authentication.getUserAuthentication();
        Object principal = oauth2Authentication.getPrincipal();

        //2.组装文明token,转发给微服务，名称为 json-token
        List<String> authorities = new ArrayList();
        userAuthentication.getAuthorities().forEach(s -> authorities.add(((GrantedAuthority) s).getAuthority()));

        OAuth2Request oAuth2Request = oauth2Authentication.getOAuth2Request();
        Map<String, String> requestParameters = oAuth2Request.getRequestParameters();
        Map<String,Object> jsonToken = new HashMap<>(requestParameters);

        if(userAuthentication != null){
            jsonToken.put("principal",userAuthentication.getName());
            jsonToken.put("authorities",authorities);
        }
        ctx.addZuulRequestHeader("jsontoken",
                EncryptUtil.encodeUTF8StringBase64(JSON.toJSONString(jsonToken)));

        return null;
    }
}
