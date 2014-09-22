<%@ page import="com.bjrxht.notify.Phone" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="easyui"/>

    <title><g:message code="default.list.label" args="['null']"/></title>
    <script type="text/javascript" src="${request.contextPath}/phone/js"></script>
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
<table id="phone-table" style="width:auto;height:auto;" collapsible="true"
       title="<g:message code="easyui.table.title" args="['null']"/>" iconCls="icon-edit"
       singleSelect="false" striped="true" rownumbers="true"
       idField="id" url="${request.contextPath}/phone/json">
</table>

<div id="phone-search" maximizable="false" minimizable="false" closed="true" modal="true" shadow="false"
     title="<g:message code="easyui.window.query.title"/>" iconCls="icon-save"
     style="width:500px;height:200px;padding:5px;background: #fafafa;">
    <div id="phone-search-layout" fit="true">
        <div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
            <g:message code="easyui.window.query.field.text"/>
            <select id="searchFieldList" class="easyui-combobox" name="searchFieldList" style="width:100px;"
                    required="true">

                <option value="number">number</option>

                <option value="tag">tag</option>

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

<div id="phone-insert" maximizable="false" minimizable="false" closed="true" modal="true" shadow="false"
     title="<g:message code="easyui.window.insert.title"/>" iconCls="icon-save"
     style="width:500px;height:500px;padding:5px;background: #fafafa;">
    <div id="phone-insert-layout" fit="true">
        <div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
            <form id="phone-form" action="ajaxInsert" method="post" enctype="multipart/form-data">
                <input type="hidden" name="id" value=""/>

                <div id="insert-tabs" class="easyui-tabs" style="width:auto;height:auto;">
                    <div title="<g:message code="easyui.window.insert.first.tab.title"/>" style="padding:20px;">
                        <table>

                            <tr><td>number:</td><td><input class='easyui-validatebox' type='text' required='true'
                                                           name='number'/></td></tr>

                             <!--

                            <tr><td>tag:</td><td><input class='easyui-validatebox' type='text' required='false'
                                                        name='tag'/></td></tr>
                             -->
                        </table>

                    </div>

                    <div title="accounts" closable="true" style="padding:20px;" cache="false">
                        <g:set var="accounts" value="${com.bjrxht.notify.Account.list()}"/>
                        <g:each var="obj" in="${accounts}">
                            <input type="checkbox" value="${obj.id}" name="checkbox_accounts"/>${obj.toString()}<br/>
                        </g:each>
                    </div>

                </div>
            </form>
        </div>

        <div region="south" border="false" style="height: 30%;text-align:right;">
            <a class="easyui-linkbutton" id="insert-ok-button" iconCls="icon-ok" href="javascript:void(0)"
               onclick="insertOk()"><g:message code="easyui.window.insert.button.ok.text"/></a>
            <a class="easyui-linkbutton" id="edit-ok-button" iconCls="icon-ok" href="javascript:void(0)"
               onclick="editOk()"><g:message code="easyui.window.edit.button.ok.text"/></a>
            <a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)"
               onclick="insertCancel()"><g:message code="easyui.window.insert.button.cancel.text"/></a>
        </div>
    </div>
</div>
</body>
</html>
