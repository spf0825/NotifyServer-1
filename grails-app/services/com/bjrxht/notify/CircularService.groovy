package com.bjrxht.notify
 //消息通知订阅类
class CircularService {

    boolean transactional = false
    static exposes = ['jms']
    static destination = "feedback"
    static pubSub = true
    //static messageSelector = "MessageType = 'DATA'"
    //static listenerMethod = "onMessage"
    def onMessage(params){
       // println "feedback-GOT MESSAGE: $params"
    }
}
