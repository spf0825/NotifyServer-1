<%@ page import="com.bjrxht.notify.Account" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <sec:ifNotGranted roles="ROLE_INCASE">
        <meta name="layout" content="easyui"/>
    </sec:ifNotGranted>
    <sec:ifAnyGranted roles="ROLE_INCASE">
        <meta name="layout" content="easyui_incase"/>
    </sec:ifAnyGranted>


    <title>鹤壁银行</title>
    <script type="text/javascript" src="${request.contextPath}/account/accountJs"></script>
</head>

<body>
<div id="hiddenDiv" style="display:none;">
    <form id="paginationForm">
        <input type="text" id="max" name="max" value="${params.max}"/>
        <input type="text" id="offset" name="offset" value="${params.offset}"/>
        <input type="text" id="sort" name="sort" value="${params.sort}"/>
        <input type="text" id="order" name="order" value="${params.order}"/>
        <input type="text" id="searchField" name="searchField" value="${params.searchField}"/>
        <input type="text" id="searchValue" name="searchValue" value="${params.searchValue}"/>
    </form>
</div>
<table id="account-table" style="width:auto;height:auto;" collapsible="true"
       title="" iconCls="icon-edit"  fitColumns="true"
       singleSelect="false" striped="true" rownumbers="true"
       idField="id" url="${request.contextPath}/account/json">
</table>

<div id="account-search" maximizable="false" minimizable="false" closed="true" modal="true" shadow="false"
     title="<g:message code="easyui.window.query.title"/>" iconCls="icon-save"
     style="width:500px;height:200px;padding:5px;background: #fafafa;">
    <div id="account-search-layout" fit="true">
        <div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
            <g:message code="easyui.window.query.field.text"/>
            <select id="searchFieldList" class="easyui-combobox" name="searchFieldList" style="width:100px;"
                    required="true">

                <option value="number">number</option>

            </select>
            <g:message code="easyui.window.query.vaule.text"/>
            <input class="easyui-validatebox"
                   name="searchValueInput"
                   id="searchValueInput"
                   required="true">

        </div>

        <div region="south" border="false" style="height: 30%;text-align:right;">
            <a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="searchOk()"><g:message
                    code="easyui.window.query.button.ok.text"/></a>
            <a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)"
               onclick="searchCancel()"><g:message code="easyui.window.query.button.cancel.text"/></a>
        </div>
    </div>
</div>

<div id="account-insert" maximizable="false" minimizable="false" closed="true" modal="true" shadow="false"
     title="<g:message code="easyui.window.insert.title"/>" iconCls="icon-save"
     style="width:500px;height:500px;padding:5px;background: #fafafa;">
    <div id="account-insert-layout" fit="true">
        <div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
            <form id="incase-form" action="save" method="post" enctype="multipart/form-data">
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
                            <input class='easyui-validatebox info' type='text' name='realName'/>
                        </td>
                    </tr>
                    <tr>
                        <td height="30" class="tit" width="20%" >手机</td>
                        <td width="80%" align="left" style="text-align: left">
                            <input id='phoneNumber' class='easyui-validatebox info' type='text' name='phoneNumber'/>
                        </td>
                    </tr>
                    <tr>
                        <td height="30" class="tit" width="20%" >银行</td>
                        <td width="80%" align="left" style="text-align: left">
                            鹤壁银行
                            <input id='bank' class='easyui-validatebox info' type='hidden' name='bankName' value="鹤壁银行"/>
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <div region="south" border="false" style="height: 30%;text-align:right;">
            <a class="easyui-linkbutton" id="insert-ok-button" iconCls="icon-ok" href="javascript:void(0)"
               onclick="if(!$('#phoneNumber').val().match(/^1[3458][0-9]\d{8}$/)){alert('手机号码输入不正确');}else{insertOk()}"><g:message code="easyui.window.insert.button.ok.text"/></a>
            <a class="easyui-linkbutton" id="edit-ok-button" iconCls="icon-ok" href="javascript:void(0)"
               onclick="if(!$('#phoneNumber').val().match(/^1[3458][0-9]\d{8}$/)){alert('手机号码输入不正确');}else{editOk()}"><g:message code="easyui.window.edit.button.ok.text"/></a>
            <a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)"
               onclick="insertCancel()"><g:message code="easyui.window.insert.button.cancel.text"/></a>
        </div>
    </div>
</div>
</body>
</html>
