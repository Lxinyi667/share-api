package top.lxyi.share.content.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
//checkAuth鉴权注解
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAuth {
    String value();
}
