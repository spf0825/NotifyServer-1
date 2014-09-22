<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 13-10-31
  Time: 上午9:37
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="easyui_incase"/>

    <title><g:message code="default.list.label" args="['null']"/></title>
    <script type="text/javascript" src="${request.contextPath}/account/js"></script>
</head>
<body>

<form id="incaseForm" action="save" method="post" enctype="multipart/form-data">
    <input type="hidden" name="id" value=""/>
    <table class="td_left reg" width="96%" infoder="0" cellspacing="0" cellpadding="0" style="infoder-top:1px #999999 solid;table-layout: fixed;word-wrap:break-word;">
    <tr>
        <td height="30" class="tit" width="20%" >账号</td>
        <td width="80%" align="left" style="text-align: left">
            <input class='easyui-validatebox info' type='text' name='number'/>
        </td>
    </tr>
    <tr>
        <td height="30" class="tit" width="20%" >姓名</td>
        <td width="80%" align="left" style="text-align: left">
            <input class='easyui-validatebox info' type='text' name='number'/>
        </td>
    </tr>
    <tr>
        <td height="30" class="tit" width="20%" >手机</td>
        <td width="80%" align="left" style="text-align: left">
            <input class='easyui-validatebox info' type='text' name='phone'/>
        </td>
    </tr>
    <tr>
        <td height="30" class="tit" width="20%" >银行</td>
        <td width="80%" align="left" style="text-align: left">
            <input class='easyui-validatebox info' type='text' readonly="readonly" value="鹤壁银行"/>
            <input class='easyui-validatebox info' type='hidden' name='bankName' value="鹤壁银行"/>
        </td>
    </tr>
</table>
</form>
<div style="padding: 10px" align="right">
    <a class="easyui-linkbutton"  id="edit-ok-button"  href="javascript:void(0)" onclick="$('#incaseForm').submit();"><g:message code="easyui.window.edit.button.ok.text"/></a>
    <a class="easyui-linkbutton"  id="edit-cancel-button" href="javascript:void(0)" onclick="$('input[type=text]').val('');"><g:message code="easyui.window.insert.button.cancel.text"/></a>
</div>
</body>
</html>