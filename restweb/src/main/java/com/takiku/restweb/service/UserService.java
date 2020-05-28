package com.takiku.restweb.service;


import po.UserCertification;

/**
 * Date: 2019-04-07
 * Time: 18:35
 *
 * @author yrw
 */
public interface UserService  {


    UserCertification verifyAndGet(String userId,String token);


}
