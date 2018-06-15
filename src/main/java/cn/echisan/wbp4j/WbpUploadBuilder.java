package cn.echisan.wbp4j;

import cn.echisan.wbp4j.exception.Wbp4jException;
import cn.echisan.wbp4j.utils.CookieHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by echisan on 2018/6/13
 */
public class WbpUploadBuilder {

    private static final Logger logger = LoggerFactory.getLogger(WbpUploadBuilder.class);

    private String username;
    private String password;
    private String cookieFileName;
    /**
     * 开发模式，默认开启的。
     * 如果开启的话，每次启动都会登录一遍，
     * 开启了之后，程序启动的时候都会去检查cookie文件是否存在
     * 如果存在直接使用该cookie而不用每次都会登录一遍
     */
    private boolean dev = true;

    public WbpUploadBuilder setAccount(String username, String password) {
        this.username = username;
        this.password = password;
        return new WbpUploadBuilder();
    }

    public WbpUploadBuilder setCookieFileName(String cookieFileName) {
        this.cookieFileName = cookieFileName;
        return this;
    }

    public WbpUploadBuilder setDev(boolean dev){
        this.dev = dev;
        return this;
    }

    public WbpUpload build() throws Wbp4jException{
        return dev ? devMode():prodMode();
    }

    private WbpUpload devMode(){
        if (!CookieHolder.exist()){
            logger.info("cookie file not found,will do login.");
            doLogin();
        }
        logger.info("cookie file found!!");
        return new WbpUpload();
    }

    private WbpUpload prodMode(){
        doLogin();
        return new WbpUpload();
    }

    private void doLogin(){
        if (username ==null || password==null){
            throw new Wbp4jException("username and password can not be null!");
        }
        try {
            WbpLogin.login(username, password);
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException |
                IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException |
                InvalidKeyException e) {
            throw new Wbp4jException("登录失败,密码加密失败,原因: " + e.getMessage());
        }
    }
}
