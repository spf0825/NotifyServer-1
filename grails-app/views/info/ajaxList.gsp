

<%@ page import="com.bjrxht.notify.Info" %>
<%
    //params.nolayout=true;
    %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="easyui" />

        <title><g:message code="default.list.label" args="['确认信息']" />-<sec:loggedInUserInfo field="username"/></title>
		<script type="text/javascript" src="${request.contextPath}/info/js"></script>
        <style type="text/css" media="screen">
        html, body  { height:100%; }
        body { margin:0; padding:0; overflow:auto;background-color: #ffffff; }
        object:focus { outline:none; }
        #flashContent { display:none; }
        </style>

        <link rel="stylesheet" type="text/css" href="${request.contextPath}/NotifyFlex-debug/history/history.css" />
        <script type="text/javascript" src="${request.contextPath}/NotifyFlex-debug/history/history.js"></script>
        <script type="text/javascript" src="${request.contextPath}/NotifyFlex-debug/swfobject.js"></script>
        <script type="text/javascript">
            // For version detection, set to min. required Flash Player version, or 0 (or 0.0.0), for no version detection.
            var swfVersionStr = "11.1.0";
            // To use express install, set to playerProductInstall.swf, otherwise the empty string.
            var xiSwfUrlStr = "playerProductInstall.swf";
            var flashvars = {};
            var params = {};
            params.quality = "high";
            params.bgcolor = "#ffffff";
            params.allowscriptaccess = "sameDomain";
            params.allowfullscreen = "true";
            var attributes = {};
            attributes.id = "NotifyFlex";
            attributes.name = "NotifyFlex";
            attributes.align = "middle";
            swfobject.embedSWF(
                    "${request.contextPath}/NotifyFlex-debug/NotifyFlex.swf", "flashContent",
                    "1px", "1px",
                    swfVersionStr, xiSwfUrlStr,
                    flashvars, params, attributes);
            // JavaScript enabled so display the flashContent div in case it is not replaced with a swf object.
            swfobject.createCSS("#flashContent", "display:block;text-align:left;");
        </script>
        <script type="text/javascript">
            function getInitParams(){
                //alert('getInitParams');
                var obj=new Object();
                obj.init=true;
                obj.destination="tpc";
                //obj.subtopic="topic.notification";
                return obj;
            }
            function onMessage(body){
                //alert(body);
                $.messager.show({
                    title:'信息更新',
                    msg:'用户确认了UUID为'+body+'消息',
                    timeout:4000,
                    showType:'slide'
                });
                $('#info-table').datagrid('reload');
            }
            function initConsumer(){
                var obj=new Object();
                obj.init=true;
                obj.destination="tpc";
                document.getElementById("NotifyFlex").flexCall(obj);
            }
        </script>
    </head>
    <body>

    <div id="hiddenDiv" style="display:none;">
    	<form id="paginationForm">
	    	<input type="text" id="max" name="max" value="${params.max}" />
	    	<input type="text" id="offset" name="offset" value="${params.offset}" />
	    	<input type="text" id="sort" name="sort" value="${params.sort}" />
	    	<input type="text" id="order" name="order" value="${params.order}" />
	    	<input type="text" id="searchField" name="searchField" value="${params.searchField}" />
	    	<input type="text" id="searchValue" name="searchValue" value="${params.searchValue}" />
    	</form>
    </div>
	<table id="info-table" style="width:auto;height:auto;"  collapsible="true"
			title="<g:message code="easyui.table.title" args="['确认信息']"/>" iconCls="icon-edit"
			 singleSelect="false"  striped="true" rownumbers="true"
			idField="id" url="${request.contextPath}/info/json">
   </table>
	
	<div id="info-search"   maximizable="false" minimizable="false"  closed="true" modal="true" shadow="false" title="<g:message code="easyui.window.query.title"/>" iconCls="icon-save" style="width:500px;height:200px;padding:5px;background: #fafafa;">
		<div id="info-search-layout" fit="true">
			<div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
				<g:message code="easyui.window.query.field.text"/>
				<select id="searchFieldList" class="easyui-combobox" name="searchFieldList" style="width:100px;" required="true">
				
							<option value="money">支付金额(分)</option>
						
							<option value="payee">收款人</option>
						
							<option value="result">确认结果</option>
						
						
					   
						
					   
				</select>				
				<g:message code="easyui.window.query.vaule.text"/>
					<input class="easyui-validatebox" 
						name="searchValueInput"
						id="searchValueInput"
						 required="true">
				
			</div>
			<div region="south" border="false" style="height: 30%;text-align:right;">
				<a class="easyui-linkbutton" iconCls="icon-ok" href="javascript:void(0)" onclick="searchOk()"><g:message code="easyui.window.query.button.ok.text"/></a>
				<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" onclick="searchCancel()"><g:message code="easyui.window.query.button.cancel.text"/></a>
			</div>
		</div>
	</div>
	<div id="info-insert"   maximizable="false" minimizable="false"   closed="true" modal="true" shadow="false" title="<g:message code="easyui.window.insert.title"/>" iconCls="icon-save" style="width:500px;height:500px;padding:5px;background: #fafafa;">
		<div id="info-insert-layout" fit="true">
			<div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
				<form id="info-form" action="ajaxInsert" method="post" enctype="multipart/form-data">
				<input type="hidden" name="id" value=""/>
				<div id="insert-tabs" class="easyui-tabs"  style="width:auto;height:auto;">
					<div title="<g:message code="easyui.window.insert.first.tab.title"/>" style="padding:20px;">							
					    	<table>
                                <tr><td>trCode:</td><td><input class='easyui-validatebox' type='text' required='true'  value="trCode"
                                                               name='trCode'/></td></tr>
                                <tr><td>seqNo:</td><td><input class='easyui-validatebox' type='text' required='true'   value="seqNo"
                                                               name='seqNo'/></td></tr>
                                <tr><td>chnNo:</td><td><input class='easyui-validatebox' type='text' required='true'    value="cups"
                                                               name='chnNo'/></td></tr>
								
								   		<tr><td>账户:</td><td><input class='easyui-combobox' editable='false' valueField='id' textField='text' value="1" url='${request.contextPath}/info/inList?field=account' name='account.id'/></td></tr>
								   	
								   	
								
					    					<tr><td>支付金额:</td><td><input class='easyui-numberspinner' type='text' precision="0" required='true' name='money' value="99999999"/>元</td></tr>
								   		
								   	
								
					    					<tr><td>收款人:</td><td><textarea class='easyui-validatebox'  name='payee'  required='true' style='height:100px;'  >肖鹏</textarea></td></tr>
								   		
								   	        <tr><td>API:</td><td>    <select name="api" class="easyui-combobox"   value="2"  style="width:200px;">
                                                   <option value="1">jpush</option>
                                                   <option value="2" selected="selected">xmpp+sm9</option>
                                               </select>  </td></tr>
								              <!--
					    					<tr><td>确认结果:</td><td><select class='easyui-combobox' editable='false' valueField='id' textField='text' url='${request.contextPath}/info/inList?field=result' name='result'/></td></tr>
								   		     -->
					    	</table>
					</div>
				</div>
				</form>			
			</div>
			<div region="south" border="false" style="height: 30%;text-align:right;">
				<a class="easyui-linkbutton" id="insert-ok-button" iconCls="icon-ok" href="javascript:void(0)" onclick="insertOk()"><g:message code="easyui.window.insert.button.ok.text"/></a>
				<a class="easyui-linkbutton"  id="edit-ok-button"  iconCls="icon-ok" href="javascript:void(0)" onclick="editOk()"><g:message code="easyui.window.edit.button.ok.text"/></a>
				<a class="easyui-linkbutton" iconCls="icon-cancel" href="javascript:void(0)" onclick="insertCancel()"><g:message code="easyui.window.insert.button.cancel.text"/></a>
			</div>
		</div>		
	</div>

    <div id="flashContent">
    </div>
    </body>
</html>
