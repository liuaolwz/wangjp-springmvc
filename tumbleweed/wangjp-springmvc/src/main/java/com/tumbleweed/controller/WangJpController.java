package com.tumbleweed.controller;

import com.tumbleweed.annotation.Controller;
import com.tumbleweed.annotation.Qualifier;
import com.tumbleweed.annotation.RequestMapping;
import com.tumbleweed.annotation.RequestParam;
import com.tumbleweed.service.WangjpService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 描述:控制层类
 *
 * @author: mylover
 * @Time: 12/02/2017.
 */
@Controller("wangjpController")
@RequestMapping("/wangjp")
public class WangJpController {

    @Qualifier("wangjpServiceImpl")
    private WangjpService wangjpService;

    @RequestMapping("/insert")
    public void insert(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("name") String userName
    ) {
        try {
            PrintWriter pw = response.getWriter();
            String result = wangjpService.insert(null);
            pw.write(userName + ":" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/query")
    public void query(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("name") String userName
    ) {
        try {
            PrintWriter pw = response.getWriter();
            String result = wangjpService.query(null);
            pw.write(userName + ":" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
