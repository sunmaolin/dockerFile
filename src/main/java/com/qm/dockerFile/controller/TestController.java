package com.qm.dockerFile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author 孙茂林
 * @date 2022-04-01
 */
@Controller
public class TestController {

    @Autowired
    RedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        Object num = redisTemplate.opsForValue().get("num");
        if (num == null) {
            redisTemplate.opsForValue().set("num",1);
            return "这是第一次访问！";
        }
        Integer nextNum = (Integer) num + 1;
        redisTemplate.opsForValue().set("num", nextNum);
        return "Hello,这是第" + nextNum + "次访问！";
    }

    @ResponseBody
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello," + name + "!";

    }

    @GetMapping("/image/{imageName}")
    public void image(HttpServletResponse response,@PathVariable String imageName) {

        // 返回jpg图片
        response.setContentType("image/jpeg");

        // 根据图片名生成地址
        String imagePath = "/home/images/" + imageName + ".jpg";

        try (BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
             BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(imagePath))) {

            // 一次读取1KB
            byte[] read = new byte[1024];
            while (inputStream.read(read) != -1) {
                outputStream.write(read);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
