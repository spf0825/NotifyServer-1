<html xmlns="http://www.w3.org/1999/html">
<head>
  <title></title>

   <style type="text/css">
 .bodyDiv{font-size:14px;border: 1px solid #BDD0DC; overflow: hidden; width: 60%; text-align: left;line-height:1.5;padding-left: 20px;}
 .theme{color: #19538A;font-weight: bold;}
 .formDiv{border: 1px solid #CCCCCC; overflow: hidden;margin-bottom:15px;margin-right: 10px; background: #F8F8F8;}
 .inputCls{font-size: 14px; color:#747474;width: 96%;height: 35px; line-height:2;margin-top: 10px; margin-left: 10px;}
{ font-size:13px;margin: 10px;border: 1px solid #BDD0DC; height: 30px;}
 .checkBoxDiv{margin-top: 5px;margin-left: 10px; color:#D2CCD3; background: #EFEFEF;margin-right: 10px;
               border:1px solid #cccccc ; overflow: hidden;;}
 img{line-height: 1;}
 .sendButton{font-size: 14px;float: right; height: 35px; line-height:1; margin-right: 10px; margin-bottom: 10px; width: 100px;cursor:pointer;
                  background:#36424A; color: red; border:0;}
 .radioDiv{margin: 7px;}
   </style>

  <script type="text/javascript">
   $(function(){
     Object obj=new Object();
       var title= $("#title").val();
       var content= $("#content").val();
       obj.title=title;
       obj.content= content;
       $.post("${request.contextPath}/message/send",obj,
               function(data, textStatus){
                 if(data.result){
                   $.message.alert('发送成功',data.message);
        }
                   alert("bbbbbbbbbbbbbbbbb");
       });
       alert("ccccccccccc");
   });
  </script>

</head>
<body>
<center>
    <div class="bodyDiv">
        <g:form action="send" method="post" class="form">
            <span class="theme">1、通知标题（可选，仅适应于 Android）</span> <br/>
        <div class="formDiv">
                <input type="text" class="inputCls" id="title" name="title" value="${params?.title}"/>
                <span>还可以输入30个汉字</span>
        </div>
        <span class="theme">2、通知内容（必须）</span> <br/>
        <div class="formDiv">
                <input type="text" class="inputCls" id="content" name="content" value="${params?.content}"/>
             <span>还可以输入40个汉字</span>
        </div>
        <span class="theme">选择推送对象</span> <br/>
        <div class="formDiv">
                <div class="checkBoxDiv">
                    <input type="checkbox" name="choiceIos" value=""><img src="${request.contextPath}/images/ios.png"/>ios</input>
                    <input type="checkbox" name="choiceAndroid" value=""><img src="${request.contextPath}/images/android.png"/>Android</input>
                </div>
            <div class="radioDiv">
               <input type="radio"  name="owner" value="owner" id="owner" checked="checked">广播(所有人)</input>
               <input type="radio" name="tag" value="tag" id="tag">设备标签(Tag)</input>
               <input type="radio" name="alias" value="alias" id="alias">设备别名(Alias)</input>
               <input type="radio" name="imei" value="imei" id="imei">单个设备</input>
            </div>
        </div>
       <Button class="sendButton" type="submit">发送通知</Button>
            </g:form>
    </div>
</center>
</body>
</html>