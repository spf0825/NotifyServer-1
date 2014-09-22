package com.bjrxht.notify
//通知策略
class Strategy {
    //账号
      Account account
      //类型
      String type
      //最大值
     Float  max=9999999
     //最小值
    Float min=0
    Float min1=0;
    Float min2=0;
    Float min3=0;
    Float min4=0;
    String phone1;
    String phone2;
    String phone3;
    String phone4;
    boolean isAuthorize1=false;
    boolean isAuthorize2=false;
    boolean isAuthorize3=false;
    boolean isAuthorize4=false;
    static constraints ={
     account(nullable: false,unique: true);
     type(nullable: false,inList: ['close','open','between','complex']);
     max(nullable: true);
     min(nullable: true);
     min1(nullable: true);
     min2(nullable: true);
     min3(nullable: true);
     min4(nullable: true);
     phone1(size: 0..20,nullable: true,blank: true)
     phone2(size: 0..20,nullable: true,blank: true)
     phone3(size: 0..20,nullable: true,blank: true)
     phone4(size: 0..20,nullable: true,blank: true)
    }
    String toString(){
        return "${account}通知策略"
    }
}
