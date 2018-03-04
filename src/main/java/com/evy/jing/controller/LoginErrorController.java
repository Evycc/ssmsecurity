package com.evy.jing.controller;

import com.evy.jing.EnumMsg.EnumLoginError;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.service.SeUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class LoginErrorController {
    @Resource
    private JedisCache jedisCache;
    @Resource
    private SeUserService seUserService;

    /**
     * 如果有登录错误信息，返回该错误信息
     * @return
     */
    @RequestMapping(value = "/hasLoginError", method = RequestMethod.POST)
    public ResponseEntity<List<String>> loginError(){
        List<String> hasErrorList = new ArrayList<String>();
        Object obj;

        //是否存在账号或密码错误信息
        obj = jedisCache.get(EnumLoginError.ERROR_LOGIN_PASSWORD);
        if (obj != null){
            jedisCache.remove(EnumLoginError.ERROR_LOGIN_PASSWORD);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }
        //是否账号是锁定状态
        obj = jedisCache.get(EnumLoginError.ERROR_LOGIN_LOCKED);
        if (obj != null){
            jedisCache.remove(EnumLoginError.ERROR_LOGIN_LOCKED);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }
        //账号是否启用
        obj = jedisCache.get(EnumLoginError.ERROR_LOGIN_DISABLE);
        if (obj != null){
            jedisCache.remove(EnumLoginError.ERROR_LOGIN_DISABLE);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }
        //密码凭证是否过期
        obj = jedisCache.get(EnumLoginError.ERROR_LOGIN_CREDENTIAL);
        if (obj != null){
            jedisCache.remove(EnumLoginError.ERROR_LOGIN_CREDENTIAL);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }
        //其他错误
        obj = jedisCache.get(EnumLoginError.ERROR_OTHER);
        if (obj != null){
            jedisCache.remove(EnumLoginError.ERROR_OTHER);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }
        //踢出判断
        obj = jedisCache.get(EnumLoginError.ERROR_KICKOUT);
        if (obj != null){
            jedisCache.remove(EnumLoginError.ERROR_KICKOUT);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }
        //密码更改判断
        obj = jedisCache.get(EnumLoginError.CHANGE_PASSWORD);
        if (obj != null){
            jedisCache.remove(EnumLoginError.CHANGE_PASSWORD);
            hasErrorList.add((String) obj);
            return new ResponseEntity<List<String>>(hasErrorList, HttpStatus.OK);
        }

        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 查找是否存在username 或 email
     * @return  存在返回true，不存在返回false
     */
    @RequestMapping(value = "/hasExistUsername", method = RequestMethod.POST)
    public ResponseEntity<List<String>> hasExistUsername(@RequestBody String username){
        List<String> list = new ArrayList<String>();

        if (seUserService.isExistUsername(username) || seUserService.isExistEmail(username)) {
            list.add(Boolean.TRUE.toString());
            return new ResponseEntity<List<String>>(list, HttpStatus.OK);
        }

        list.add(Boolean.FALSE.toString());
        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }

    /**
     * 查找是否存在email
     * @return  存在返回true，不存在返回false
     */
    @RequestMapping(value = "/hasExistEmail", method = RequestMethod.POST)
    public ResponseEntity<List<String>> hasExistEmail(@RequestBody String email){
        List<String> list = new ArrayList<String>();

        if (seUserService.isExistEmail(email)) {
            list.add(Boolean.TRUE.toString());
            return new ResponseEntity<List<String>>(list, HttpStatus.OK);
        }

        list.add(Boolean.FALSE.toString());
        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }
}
