package org.java.userauth.web;

import org.apache.commons.lang3.StringUtils;
import org.java.insurance.userauth.pojo.UserInfo;
import org.java.insurance.userauth.utils.JwtUtils;
import org.java.insurance.util.CookieUtils;
import org.java.userauth.config.JwtProperties;
import org.java.userauth.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class UserAuthController {


    @Autowired
    private UserAuthService userAuthService;


    @Autowired
    private JwtProperties jwtProperties;


    /**
     *     /**
     *      * 接收用户名，密码，判断数据是否正确，如果正确，则通过jwt生成token发送到客户端保存
     *      * 直接访问地址: localhost:15500/accredit
     *      * 通过网关访问的地址是: api.insurance.com/api/userauth/accredit
     *      *
     * @param username
     * @param password
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/accredit")
    public ResponseEntity<Boolean> accredit(@RequestParam("username")String username,
                                            @RequestParam("password")String password,
                                            HttpServletRequest request,
                                            HttpServletResponse response){


      String toekn=userAuthService.accredit(username,password);


      if (toekn==null){
          return ResponseEntity.badRequest().build();
      }

        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),toekn,jwtProperties.getExpire()*60);


      return ResponseEntity.ok().build();
    }




    /**
     * 获得cookie,通过jwt解析cookie中的token,如果信息正确就返回载荷（userInfo）
     *
     * @return 通过网关的请求地址是:  http://api.insurance.com/api/userauth/verify
     * 如果进入该方法验证，只要信息正确，不论之前有效时间是多少，都重新设置为30分钟
     */
     @GetMapping("/verify")
     public ResponseEntity<UserInfo> verify(@CookieValue(name = "shopping-token")String token,
                                            HttpServletRequest request,
                                            HttpServletResponse response){



         //判断token是否为空
         if (StringUtils.isBlank(token)) {
             //用户没有传递一个名称为shopping-token的cookie
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

         try {
             //如果不为空，通过公钥解析数据，得到载荷信息

             UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

             //数据有效，重新设置时间
             token = JwtUtils.generateToken(userInfo, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

             //cookie的有效时间，重新设置为30分钟
             CookieUtils.setCookie(request, response,
                     jwtProperties.getCookieName(), token, jwtProperties.getExpire() * 60);

             return ResponseEntity.ok(userInfo);//token有效，返回用户信息

         } catch (Exception e) {

             e.printStackTrace();
             //如果解析不正确，会产生异常
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }
     }

}
