package com.bjrxht.notify

import com.basic.core.BaseUser
//交易通知系统账户（绑定手机）
class Account {
     //账号
    String number
     //账户余额  String remain
    //电话
    Phone phone
    //银行
    Bank bank
    //姓名
    String realName;
    BaseUser baseUser;
    String identificationType
    String identificationNo
    String status;
    String type;//使用确认类型：01：手机；02：确认器；03：表示不使用任何确认工具
    static constraints = {
      number(size: 0..200,nullable: false,blank: false,unique: true);
      realName(size: 0..400,nullable: true,blank: true);
      status(size: 0..400,nullable: true,blank: true);
      bank(nullable: false);
      phone(nullable: true);
      baseUser(nullable: true);
      identificationType(size: 0..50,nullable: true,blank: true);
      identificationNo(size: 0..200,nullable: true,blank: true);
      type(size: 0..10,nullable: true,blank:true);
    }
    String toString(){
        if(number.size()>8){
            return "${bank.name}账户${number[0..3]}${''.padLeft(number.size()-8,'*')}${number[-4..-1]}"
        }else{
            return "${bank.name}账户${number}"
        }
    }
}
