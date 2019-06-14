package com.nowcoder.controller;

import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger looger = LoggerFactory.getLogger(LoginController.class);

    //    UserService 组件初始化
    @Autowired
    UserService userService;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      HttpServletResponse response) {
        try {
//        调用 UserService 的 注册方法
            Map<String, String> map = userService.register(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            looger.error("注册异常 " + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/login"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
                        HttpServletResponse response) {
        try {
//        调用 UserService 的 登录方法
            Map<String, String> map = userService.login(username, password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                return "redirect:/";
            }else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            looger.error("登陆异常 " + e.getMessage());
            return "login";
        }
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/";
    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String reg(Model model){
        return "login";
    }
}
