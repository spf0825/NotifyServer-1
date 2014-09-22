package com.bjrxht.notify
 //关联手机号码
class Phone {
     //电话号码
      String number
      String realName
      static hasMany = [accounts:Account]
      //alias 使用jpush 时提供，
      String tag
    static constraints = {
        number(nullable: false,blank: false,unique: true);
        tag(size: 0..100,nullable: true);
        realName(size: 0..400,nullable: true,blank: true);
    }
    def beforeInsert(){
        tag=number
    }
    def beforeUpdate(){
        if(isDirty("tag")){
            tag=number
        }
    }
    String toString(){
        return number
    }
}
