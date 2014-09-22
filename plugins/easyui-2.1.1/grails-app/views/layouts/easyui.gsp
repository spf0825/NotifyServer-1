<%@ page import="org.springframework.web.servlet.support.RequestContextUtils;com.bjrxht.socket.SocketServerStatus" %>
<!DOCTYPE html>
<html>
    <head>
        <title><g:layoutTitle default="Grails-easyui" /></title>        
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
    <link rel="stylesheet"  id="themeContent" type="text/css" href="${easyui.path()}/js/easyui/themes/${session.skin?:'default'}/easyui.css">
		<link rel="stylesheet" type="text/css" href="${easyui.path()}/js/easyui/themes/icon.css">
		<script type="text/javascript" src="${easyui.path()}/js/easyui/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="${easyui.path()}/js/easyui/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="${easyui.path()}/js/easyui/locale/easyui-lang-${RequestContextUtils.getLocale(request)}.js"></script> 
        <script>
            $.ajaxSetup({
                    //contentType:"application/x-www-form-urlencoded;charset=utf-8",
                    //timeout:100,
                    cache:false,
                    complete:function(XMLHttpRequest, textStatus){
                        if (XMLHttpRequest.status==401){
                            needAuthentication();
                        }
                    }
                    //,
                    //only for xml and html, not work on json type
                    //error: function(XMLHttpRequest, textStatus, errorThrown){
                    //    if (XMLHttpRequest.status==401){
                    //        needAuthentication();
                    //    }
                    //}
            });
            function getRootWin(){
                var win = window;
                while (win != win.parent){
                    win = win.parent;
                }
                return win;
            }
            function needAuthentication(){
                var mainWin=getRootWin();
                mainWin.location.href="${request.contextPath}/logout";
            }
		$.fn.datebox.defaults.formatter = function(date) {
			var y = date.getFullYear();
			var m = date.getMonth() + 1;
			var d = date.getDate();
			return y + '-' + (m < 10 ? '0' + m : m) + '-' + (d < 10 ? '0' + d : d);
		};
		$.fn.datebox.defaults.parser = function(dateStr) {
			var date=new Date();
			if(dateStr){
				var a=dateStr.split('-');
				date.setFullYear(new Number(a[0]));
				date.setMonth(new Number(a[1])-1);
				date.setDate(new Number(a[2]));
			}		
			return date;
		};
		$.fn.datetimebox.defaults.formatter = function(date) {
			var y = date.getFullYear();
			var m = date.getMonth() + 1;
			var d = date.getDate();
			var h =	date.getHours();
			var mi= date.getMinutes();
			var s = date.getSeconds();
			return y + '-' + (m < 10 ? '0' + m : m) + '-' + (d < 10 ? '0' + d : d)+" "+(h<10 ? '0'+ h:h)+":"+(mi<10 ? '0'+ mi:mi)+":"+(s<10 ? '0'+ s:s);
		};
		$.fn.datetimebox.defaults.parser = function(dateStr) {
			var date=new Date();
			if(dateStr){
				var timearray = dateStr.split(' ');
				var a=timearray[0].split('-');
				date.setFullYear(new Number(a[0]));
				date.setMonth(new Number(a[1])-1);
				date.setDate(new Number(a[2]));
				var b=timearray[1].split(':');
				date.setHours(new Number(b[0]));
				date.setMinutes(new Number(b[1]));
				date.setSeconds(new Number(b[2]));
			}		
			return date;
		};
		<g:if test="${!params.nolayout}">
		$(function(){
			$('#layoutControllerTree').tree({
				checkbox: false,
				url:'${request.contextPath}/easyui/controllerTree',
				onClick:function(node){
					$(this).tree('toggle', node.target);
					if(node.attributes.type=='controller'){
						mainAddTab(node.id,"${request.contextPath}/"+node.id+"?nolayout=true");
					}
				}
			});
			
			$('#systemi18nmenu').combobox({
				onChange:function(newValue, oldValue){
					var obj=new Object();
					obj.lang=newValue;
					$.post("${request.contextPath}/easyui/changeLang",obj,
						function (data, textStatus){
							location.reload(true);
						}, "json");
				}
			});
			$('#systemskin').combobox({
				onChange:function(newValue, oldValue){
					var obj=new Object();
					obj.skin=newValue;
                    $('#themeContent').attr('href', '${easyui.path()}/js/easyui/themes/'+newValue+'/easyui.css');
                    /*
					$.post("${request.contextPath}/easyui/changeSkin",obj,
						function (data, textStatus){
							location.reload(true);
						}, "json");
						*/
				}
			});
            $('#url-dialog').dialog({
                width: 880,
                height: 600,
                //left: 10,
                //top: 10,
                model:true,
                maximized:false,
                onClose:function(){
                    $('#urlFrame').attr("src","${request.contextPath}/easyui/blank");
                }
            });
		});
        function mainAddTab(title, url){

            if(url.lastIndexOf('?')>0){
                url=url+"&nolayout=true";
            }else{
                url=url+"?nolayout=true";
            }

            var content = '<iframe scrolling="yes" frameborder="0" name="'+title+'-Frame" id="'+title+'-Frame" src="'+url+'" style="width:100%;height:97%;scrolling:yes;overflow:scroll;overflow-y:hidden;overflow-x:hidden;"></iframe>';
            //$('#frameLayout').layout('panel','center').panel('refresh',url);
            /*
            $.get(url,null,
                      function (data, textStatus){
                          content =data;
                      }, "html");
                  */
      		       if ($('#layoutMainTabs').tabs('exists', title)){
      		        $('#layoutMainTabs').tabs('select', title);
                      var tab=$('#layoutMainTabs').tabs('getTab',title);
                      $('#layoutMainTabs').tabs('update', {
                          tab: tab,
                          options:{
                              title:title,
                              content:content
                          }
                      });
      		    } else {
      		        $('#layoutMainTabs').tabs('add',{
      		            title:title,
      		            content:content,
      		            closable:true
      		        });
      		    }

      		}
        function editDomainData(title,url,callerFrameId){
            if(url.lastIndexOf('?')>0){
                url=url+"&nolayout=true&callerFrameId="+callerFrameId;
            }else{
                url=url+"?nolayout=true&callerFrameId="+callerFrameId;
            }
            $('#urlFrame').attr("src",url);
            $('#url-dialog').dialog('setTitle',title);
            $('#url-dialog').dialog('open');
            $('#url-dialog').dialog('expand',true);
        }
        function refreshDomainList(callerFrameId,message){
            closeUrlDialog();
            var caller=document.getElementById(callerFrameId);
          	if(caller){
                  caller.contentWindow.refreshTableData(message);
            }
        }
        function openUrl(title,url){
            $('#urlFrame').attr("src",url);
            $('#url-dialog').dialog('setTitle',title);
            $('#url-dialog').dialog('open');
            $('#url-dialog').dialog('expand',true);
        }
        function closeUrlDialog(){
            $('#url-dialog').dialog('collapse',true);
            $('#url-dialog').dialog('close');
        }
        function startSocket(){
            $.post("${request.contextPath}/info/startSocketServer",null,
                    function (data, textStatus){
                        if(data.result){
                            $.messager.show({
                                title:'<g:message code="easyui.message.delete.sucess.title"/>',
                                msg:data.message,
                                timeout:4000,
                                showType:'slide'
                            });
                        }else{
                            $.messager.alert('<g:message code="easyui.message.delete.fault.title"/>',data.message,'error');
                        }
                    }, "json");
            $('#sockerButton').linkbutton('disable');
        }
		</g:if>
            function preCloses(){
                $("#preLoading").fadeOut("normal",function(){
             	        $(this).remove();
                 });
             	}
             	var pc;
             	$.parser.onComplete = function(){
             	    if(pc) clearTimeout(pc);
             	    pc = setTimeout(preCloses, 1000);
                }
		</script>
        <g:layoutHead />
    </head>
<g:if test="${!params.nolayout}">
<body id="frameLayout" class="easyui-layout">
<div id='preLoading'
     style="position:absolute;z-index:1000;top:0px;left:0px;width:100%;height:100%;background:#DDDDDB;text-align:center;padding-top: 20%;">
    <h1><image src='${easyui.path()}/images/loading/loading11.gif'/><br/>
        <font color="#15428B"><g:message code="easyui.dialog.loading.title"/>......</font></h1>
</div>
		<div region="north" title="${message(code:'app.layout.north.title')}" split="true" style="height:100px;padding:10px;">
			<div style="padding:5px;width:auto;text-align:right;float:right;">
                Socket Server 状态:<g:if test="${SocketServerStatus.isRuning}">运行</g:if><g:else>停止</g:else>
                <a class="easyui-linkbutton" id="sockerButton" iconCls="icon-ok" <g:if test="${SocketServerStatus.isRuning}">disabled="true"</g:if> href="javascript:void(0)" onclick="startSocket()">启动</a>
				<input editable='false' valueField='id' class="easyui-combobox" value="${RequestContextUtils.getLocale(request)}"
				textField='text' url='${request.contextPath }/easyui/systemi18njson' style="width:60px;"
				id='systemi18nmenu' name='systemi18nmenu'/>
				<input editable='false' valueField='id' class="easyui-combobox" value="${session.skin?:'default'}"
				textField='text' url='${request.contextPath }/easyui/systemskinjson' style="width:80px;"
				id='systemskin' name='systemskin'/>	
				<a href="javascript:void(0)"   class="easyui-splitbutton" plain="true"  menu="#helpMenu" iconCls="icon-help">
				<g:message code="app.layout.menu.help.label"/></a>
				<a href="${request.contextPath}/logout"  class="easyui-linkbutton" plain="true" iconCls="icon-no">
				<g:message code="app.layout.menu.quit.label"/></a>	
			</div>
			 <div id="helpMenu" style="width:100px;">
				<div iconCls="icon-application"><g:message code="app.layout.submenu.help.help"/></div>
				<div iconCls="icon-application"><g:message code="app.layout.submenu.help.about"/></div>
			</div>
		</div>
		<div region="south" title="${message(code:'app.layout.south.title')}" split="true" style="height:50px;padding:10px;background:#efefef;">
			<p>bottom</p>
		</div>
		<div region="west" split="true" title="${message(code:'app.layout.west.title')}" style="width:280px;padding1:1px;overflow:hidden;">
			<div class="easyui-accordion" fit="true" border="false">
				<div title="menu1" selected="true" style="overflow:auto;">						
					 <p><ul id="layoutControllerTree"></ul></p>
			
				</div>
				<div title="menu2" style="padding:10px;">
					<p>content2</p>
				</div>
				<div title="menu3">
					<p>content3</p>
				</div>
			</div>
		</div>
		<div region="center" title="${message(code:'app.layout.center.title')}" style="overflow:hidden;">
			<div class="easyui-tabs" id="layoutMainTabs" fit="true" border="false">
				<div title="${message(code:'app.layout.center.maintab.title')}" icon="icon-reload" style="overflow:hidden;padding:5px;">
					<g:layoutBody />
				</div>
				
			</div>
		</div>
<div id="url-dialog"  style="width:auto;height:auto;" maximized="false" collapsed="true"
     minimizable="false" maximizable="false" modal="true" closed="true">
    <IFRAME id="urlFrame" scrolling="yes"
            style="width:99%;height:98%;OVERFLOW:SCROLL;OVERFLOW-Y:auto;OVERFLOW-X:auto" src=""></IFRAME>
</div>
    </body>
</g:if>
<g:else>
	<body>
    <div id='preLoading'
          style="position:absolute;z-index:1000;top:0px;left:0px;width:100%;height:100%;background:#DDDDDB;text-align:center;padding-top: 20%;">
         <h1><image src='${easyui.path()}/images/loading/loading11.gif'/><br/>
             <font color="#15428B"><g:message code="easyui.dialog.loading.title"/>......</font></h1>
     </div>
	<g:layoutBody />
	</body>
</g:else>    
</html>