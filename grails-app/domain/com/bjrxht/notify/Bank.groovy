package com.bjrxht.notify
//银行表
class Bank {
    //银行名称
    String name;
    static constraints = {
     name(size: 0..200,nullable: false,blank:false,unique: true);
    }
    String toString(){
        return name
    }
}
