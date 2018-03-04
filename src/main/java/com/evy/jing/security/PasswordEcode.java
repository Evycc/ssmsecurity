package com.evy.jing.security;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

/**
 * 加密密码
 */
public class PasswordEcode extends MessageDigestPasswordEncoder {
    public PasswordEcode(String algorithm) {
        super(algorithm);
    }

    public PasswordEcode(String algorithm, boolean encodeHashAsBase64) throws IllegalArgumentException {
        super(algorithm, encodeHashAsBase64);
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        return super.encodePassword(rawPass, salt);
    }

    /**
     *
     * @param encPass   数据库中加密保存的密码
     * @param rawPass   用户登录时提交的明文密码
     * @param salt  盐
     * @return  返回true,则验证通过
     */
    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        return super.isPasswordValid(encPass, rawPass, salt);
    }
}
