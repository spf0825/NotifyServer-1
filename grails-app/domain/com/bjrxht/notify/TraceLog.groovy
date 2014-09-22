package com.bjrxht.notify

import com.basic.core.BaseUser
import com.bjrxht.grails.annotation.Title
@Title(zh_CN="跟踪日志")
class TraceLog {
    @Title(zh_CN="跟踪日志")
    BaseUser operator
    @Title(zh_CN="操作描述")
    String operating
    @Title(zh_CN="渠道类型")
    String channel
    @Title(zh_CN="参数")
    String params
    @Title(zh_CN="操作时间")
    Date dateCreated;
    @Title(zh_CN="修改时间")
    Date lastUpdated;
    static constraints = {
        operator(nullable:true);
        operating(size: 0..400,nullable:false);
        channel(size: 0..50,nullable:false,inList: ['web','socket']);
        params(size: 0..8000,nullable:false);
    }
}
