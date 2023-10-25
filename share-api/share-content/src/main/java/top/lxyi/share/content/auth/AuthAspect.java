package top.lxyi.share.content.auth;

import cn.hutool.json.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.lxyi.share.common.util.JwtUtil;
import top.lxyi.share.content.feign.User;
import top.lxyi.share.content.feign.UserService;

import java.lang.reflect.Method;
import java.util.Objects;

//鉴权切面
@Aspect
@Component
@Slf4j
public class AuthAspect {
    @Resource
    private UserService userService;

    @Around("@annotation(top.lxyi.share.content.auth.CheckAuth)")
    public Object checkAuth(ProceedingJoinPoint point) throws Throwable {
        try {
            //1.验证token 是否合法
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("token");
            boolean isValid = JwtUtil.validate(token);
            if (isValid){
                throw new RuntimeException("Token不合法！");
            }
            //2.验证用户角色是否匹配
            JSONObject jsonObject = JwtUtil.getJSONObject(token);
            long userId = Long.parseLong(jsonObject.get("id").toString());
            User user = userService.getUser(userId).getData();
            String roles = user.getRoles();
            log.info("当前用户角色：>>>>>>>>>>>>>>>>>>>"+ roles);
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
           //3.取到方法头部相应注解的值
            CheckAuth annotation = method.getAnnotation(CheckAuth.class);
            String value = annotation.value();
            if (!Objects.equals(roles,value)){
                throw new RuntimeException("用户无权访问！");

            }
        }catch (Throwable throwable){
            throw new RuntimeException("用户无权访问！",throwable);
        }
        return point.proceed();
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        assert attributes != null ;
        return attributes.getRequest();

    }
}
