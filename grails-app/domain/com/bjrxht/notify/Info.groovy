package com.bjrxht.notify

import com.bjrxht.grails.annotation.Title
@Title(zh_CN="确认信息")
class Info {
    @Title(zh_CN="UUID")
    String uuid;
    @Title(zh_CN="账户")
    Account account
    @Title(zh_CN="支付金额")
    float money
    @Title(zh_CN="收款人")
    String payee
    @Title(zh_CN="确认结果")
    String result    //json字符串存储{strategy:'',confirm:{phone:'1322',result:'yes',type:'main',confirm:'handle'},{phone:'1322',result:'yes',type:'assist',confirm:'grant/handle'}}
    @Title(zh_CN="备注")
    String remark
    @Title(zh_CN="交易码")
    String trCode
    @Title(zh_CN="流水号")
    String seqNo
    @Title(zh_CN="渠道代码")
    String chnNo
    @Title(zh_CN="操作时间")
    Date dateCreated;
    @Title(zh_CN="修改时间")
    Date lastUpdated;
    static constraints = {
        uuid(size: 0..500,nullable: true,unique: true);
        account(nullable:false);
        money(nullable:false);
        payee(size: 0..500,nullable: false);
        remark(nullable: true,size: 0..100);
       // result(size: 0..50,nullable: true,inList: ['yes','no','alarm']);
        result(size: 0..500,nullable: true);
        trCode(nullable: true,size: 0..50);
        seqNo(nullable: true,size: 0..50);
        chnNo(nullable: true,size: 0..50);
    }
    static mapping = {
        uuid column: 'info_id'
    }
    def beforeInsert(){
        uuid=UUID.randomUUID().toString();
    }

    def beforeUpdate() {
        if (isDirty('uuid')) {
            uuid=Info.get(id).uuid;
        }
    }
}
