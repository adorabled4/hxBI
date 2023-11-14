package com.dhx.bi.service;

import com.dhx.bi.common.BaseResponse;

/**
 * @author adorabled4
 * @className LoginAdapter
 * @date : 2023/11/08/ 20:56
 **/
public interface Login3rdTarget {


    BaseResponse loginByGitee(String state,String code );

    BaseResponse loginByGithub(String state,String code);
}
