package com.tumbleweed.service.impl;


import com.tumbleweed.annotation.Service;
import com.tumbleweed.service.WangjpService;

@Service("wangjpServiceImpl")
public class WangjpServiceImpl implements WangjpService {
    
    public String query(String param) {
        return this.getClass().getName() + "---query";
    }
    
    public String insert(String param) {
        return this.getClass().getName() + "---insert";
    }

    public String delete(String param) {
        return this.getClass().getName() + "---delete";
    }

    public String update(String param) {
        return this.getClass().getName() + "---update";
    }
    
}
