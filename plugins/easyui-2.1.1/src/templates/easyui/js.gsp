<% import grails.persistence.Event %>
<% import com.bjrxht.grails.annotation.Title %>
<%
	cp = domainClass.constrainedProperties
	excludedProps = Event.allEvents.toList() << 'version' << 'id'
	allowedNames = domainClass.persistentProperties*.name  << 'dateCreated' << 'lastUpdated'
	props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && !Collection.isAssignableFrom(it.type) }
	Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
    def  unthField=[];
    props.eachWithIndex { p, i ->
        if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){
            unthField << "${p.name}FileName";
        }
    }

%>
        var needInitLayout=true;
        var booleanValues=[
				{id:'1',text:'<g:message code="easyui.boolean.select.true"/>'},
				{id:'0',text:'<g:message code="easyui.boolean.select.false"/>'}
                           ];
        var lastIndex;
		\$(function(){
			\$('#${domainClass.propertyName}-table').datagrid({
				//列表th
				columns:[[
					{field:'checked',checkbox:true,align:'center',title:'<g:message code="easyui.table.th.check"/>'},
                    {field:'id',width:70,align:'center',sortable:true,title:'\${message(code: '${domainClass.propertyName}.id.label', default: 'id')}'},
<%props.eachWithIndex { p, i ->
    def clazzName=domainClass.getClazz()
    def ptitle= clazzName.getDeclaredField(p.name).getAnnotation(Title)?.zh_CN()?:p.name;
    def required="true"
    def type="validatebox"
    if(Number.class.isAssignableFrom(p.type) || (p.type.isPrimitive() && p.type != boolean.class)){
        type="numberspinner"
    }
    if(cp[p.name]?.isNullable()&& cp[p.name]?.isBlank()){
        required="false"
    }
    def editor="{type:'${type}',options:{required:${required}}}"
    if(cp[p.name]?.inList){
        type="combobox"
        editor="{type:'${type}',options:{required:${required},editable:false,valueField:'id',textField:'text',url:'\${request.contextPath}/${domainClass.propertyName}/inList?field=${p.name}'}}"
    }
    if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){
        //editor="<input type='file'   name='${p.name}' />"
    }
    if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class  || p.type == java.sql.Timestamp.class|| p.type == Calendar.class){
        if(p.type == Date.class || p.type == java.sql.Date.class ||p.type == Calendar.class){
            editor="{type:'datebox',options:{required:${required},editable:false}}"
        }else{
            editor="{type:'datetimebox',options:{required:${required},editable:false}}"
        }
    }
    if(p.type == String.class && cp[p.name].maxSize > 250){
        editor="{type:'validatebox',options:{required:${required}}}";
    }
    if (p.type == Boolean.class || p.type == boolean.class){
        editor="{type:'checkbox',options:{on:'true',off:'false'}}";
    }
    if (p.manyToOne || p.oneToOne){
        type="combobox"
        editor="{type:'${type}',options:{required:${required},editable:false,valueField:'id',textField:'text',url:'\${request.contextPath}/${domainClass.propertyName}/inList?field=${p.name}'}}"
    }
    if (!p.isAssociation()) {
        if(p.type != ([] as Byte[]).class && p.type != ([] as byte[]).class){
            if(!unthField.contains("${p.name}")){ //editor:'${editor}'
%>
    {field:'${p.name}',sortable:true,title:'${ptitle}',width:100,align:'center'},

<%}
}else{%>
    {field:'${p.name}FileName',sortable:false,title:'${ptitle}',formatter:${p.name}DownloadFormatter,width:100,align:'center'},
<%}
}else{%>
    {field:'${p.name}.id',sortable:true,title:'${ptitle}',formatter:${p.name}ShowFormatter,width:100,align:'center'},
<%}
}%>
                    {field:'rowOperator',title:'\${message(code: 'easyui.table.th.operator')}',width:200,align:'center', formatter:operatorFormatter}
				]],
				//添加工具条
				toolbar:[{
					text:'<g:message code="easyui.toolbar.insert.text"/>',
					iconCls:'icon-add',
					handler:function(){
					    initLayout();
						\$('#${domainClass.propertyName}-form').form('clear');
						\$('#insert-ok-button').show();
						\$('#edit-ok-button').hide();
						\$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
						\$('#${domainClass.propertyName}-insert').window('open');
					}
				},'-',
				{
					text:'<g:message code="easyui.toolbar.delete.text"/>',
					iconCls:'icon-remove',
					handler:function(){
						var deleteRows=\$('#${domainClass.propertyName}-table').datagrid('getSelections');
						if(deleteRows!=null && deleteRows.length>0){
							\$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message code="easyui.message.delete.confirm.body"/>', function(r){
								if (r){				
									var obj=new Object();
									var s = '';
									for(var i=0; i<deleteRows.length; i++){
										if (s != '') s += ',';
										s += deleteRows[i].id;
									}
									obj.fileIds=s;
									\$.post("\${request.contextPath}/${domainClass.propertyName}/ajaxDeletes",obj,
											function (data, textStatus){
												if(data.result){
													\$.messager.show({
														title:'<g:message code="easyui.message.delete.sucess.title"/>',
														msg:'<g:message code="easyui.message.delete.sucess.body"/>',
														timeout:4000,
														showType:'slide'
													});
													\$('#${domainClass.propertyName}-table').datagrid('reload');											
												}else{
													\$.messager.alert('<g:message code="easyui.message.delete.fault.title"/>',data.message,'error');
												}
											}, "json");
								}
							});						
						}
					}
				},'-',
				{
					text:'<g:message code="easyui.toolbar.query.text"/>',
					iconCls:'icon-search',
					handler:function(){										
						//alert(JSON.stringify(rows));
						 initLayout();
						\$('#${domainClass.propertyName}-search').window('open');
					}
				}],
				pagination:true,
				onSortColumn:function(sort, order){
					\$('#sort').attr("value", sort);
					\$('#order').attr("value", order);
				},
				onBeforeLoad:function(params){
					\$(this).datagrid('rejectChanges');
					params.max=\$('#max').val();
					params.offset=\$('#offset').val();
					params.order=\$('#order').val();
					params.sort=\$('#sort').val();
					params.searchField=\$('#searchField').val();
					params.searchValue=\$('#searchValue').val();
					//params.lang="zh_CN";					
				},
				onClickRow:function(rowIndex){
					if (lastIndex != rowIndex){
						\$('#${domainClass.propertyName}-table').datagrid('endEdit', lastIndex);
					}
				},
				onDblClickRow:function(rowIndex){
					if (lastIndex != rowIndex){
						\$('#${domainClass.propertyName}-table').datagrid('endEdit', lastIndex);
						\$('#${domainClass.propertyName}-table').datagrid('beginEdit', rowIndex);
					}
					lastIndex = rowIndex;
				},
				onLoadSuccess:function(data){
					var pager = \$('#${domainClass.propertyName}-table').datagrid('getPager');
					pager.pagination({total:data.total});
					\$('a.easyui-linkbutton').linkbutton({});
				},
				onAfterEdit:function(rowIndex, rowData, changes){
					var updateRows = \$('#${domainClass.propertyName}-table').datagrid('getChanges','updated');
					for(var i=0;i<updateRows.length;i++){
						\$.post("\${request.contextPath}/${domainClass.propertyName}/ajaxRowUpdate",updateRows[i],
								function (data, textStatus){
									if(data.result){
										\$.messager.show({
											title:'sucess',
											msg:'<g:message code="easyui.message.update.sucess.body1"/>'+data.id+'<g:message code="easyui.message.update.sucess.body2"/>',
											timeout:4000,
											showType:'slide'
										});
										\$('#${domainClass.propertyName}-table').datagrid('acceptChanges');
										\$('#${domainClass.propertyName}-table').datagrid('reload');
										//\$('a.easyui-linkbutton').linkbutton({});											
									}else{
										\$.messager.alert('fault',data.message,'error');
										allsuccess=false;
									}
								}, "json");
					}	

				},
				//添加表头右键
				onHeaderContextMenu: function(e, field){
					e.preventDefault();
					if (!\$('#tmenu').length){
						createColumnMenu();
					}
					\$('#tmenu').menu('show', {
						left:e.pageX,
						top:e.pageY
					});
				}
			});
			//分页
			var pager = \$('#${domainClass.propertyName}-table').datagrid('getPager');
			pager.pagination({
				total:0,
				pageSize:\${params.max},
				pageList:\${params.maxList},
				onSelectPage:function(pageNumber, pageSize){
					\$(this).pagination('loading');
					\$('#max').attr("value", pageSize);
					\$('#offset').attr("value", pageSize*pageNumber-pageSize);
					\$('#${domainClass.propertyName}-table').datagrid('reload');
					\$(this).pagination('loaded');
				}
			});
			\$('#${domainClass.propertyName}-insert').window({});
            \$('#${domainClass.propertyName}-search').window({});
		});
		//添加表头右键功能
		function createColumnMenu(){
			var tmenu = \$('<div id="tmenu" style="width:100px;"></div>').appendTo('body');
			var fields = \$('#${domainClass.propertyName}-table').datagrid('getColumnFields');
			for(var i=0; i<fields.length; i++){
				var menuOne=\$('<div iconCls="icon-ok"/>');
				menuOne.attr('id',fields[i]);
				menuOne.html(\$('#${domainClass.propertyName}-table').datagrid('getColumnOption',fields[i]).title);
				menuOne.appendTo(tmenu);
			}
			tmenu.menu({
				onClick: function(item){
					if (item.iconCls=='icon-ok'){
						\$('#${domainClass.propertyName}-table').datagrid('hideColumn', item.id);
						tmenu.menu('setIcon', {
							target: item.target,
							iconCls: 'icon-empty'
						});
					} else {
						\$('#${domainClass.propertyName}-table').datagrid('showColumn', item.id);
						tmenu.menu('setIcon', {
							target: item.target,
							iconCls: 'icon-ok'
						});
					}
				}
			});
		}
		function searchOk(){
			//if(\$('#searchValueInput').val()!=''){
				var pager = \$('#${domainClass.propertyName}-table').datagrid('getPager');
				pager.pagination({pageNumber:1});
				\$('#offset').attr("value",0);
				\$('#searchField').attr("value", \$('#searchFieldList').combobox('getValue'));
				\$('#searchValue').attr("value", \$('#searchValueInput').val());
				\$('#${domainClass.propertyName}-table').datagrid('reload');
				\$('#${domainClass.propertyName}-search').window('close');
			//}else{
			//}
		}
		function searchCancel(){
			\$('#${domainClass.propertyName}-search').window('close');
		}
		function insertOk(){
			\$('#${domainClass.propertyName}-form').form('submit',{
				url:'\${request.contextPath}/${domainClass.propertyName}/ajaxInsert',
				success:function(data){
					var result=\$.trim(data).split(':');
					if(result[0]=='true'){
						\$('#${domainClass.propertyName}-table').datagrid('reload');
						\$.messager.show({
							title:'<g:message code="easyui.message.insert.sucess.title"/>',
							msg:'<g:message code="easyui.message.insert.sucess.body"/>',
							timeout:4000,
							showType:'slide'
						});												
					}else{
						\$.messager.alert('<g:message code="easyui.message.insert.fault.title"/>',result[1],'error');
					}
				}
			});
			\$('#${domainClass.propertyName}-insert').window('close');
		}
		function editOk(){
			\$('#${domainClass.propertyName}-form').form('submit',{
				url:'\${request.contextPath}/${domainClass.propertyName}/ajaxUpdate',
				success:function(data){
					var result=\$.trim(data).split(':');
					if(result[0]=='true'){
						\$('#${domainClass.propertyName}-table').datagrid('reload');
						\$.messager.show({
							title:'<g:message code="easyui.message.update.sucess.title"/>',
							msg:'<g:message code="easyui.message.update.sucess.body1"/>'+result[2]+'<g:message code="easyui.message.update.sucess.body2"/>',
							timeout:4000,
							showType:'slide'
						});												
					}else{
						\$.messager.alert('<g:message code="easyui.message.insert.fault.title"/>',result[1],'error');
					}
				}
			});
			\$('#${domainClass.propertyName}-insert').window('close');
		}
		function insertCancel(){
			\$('#${domainClass.propertyName}-insert').window('close');
		}
		function operatorFormatter(value,rowData,rowIndex){
			var str='<span><a href="#"  iconCls="icon-ok"  class="easyui-linkbutton" onclick="editRowData(\\''+rowIndex+'\\');"><g:message code="easyui.table.tr.operator.edit"/></a>';
			str=str+'<a href="#" iconCls="icon-cancel"  class="easyui-linkbutton" onclick="deleteRowData(\\''+rowData.id+'\\');"><g:message code="easyui.table.tr.operator.delete"/></a></span>';
			return str;
		}
		function editRowData(rowIndex){
		    initLayout();
			\$('#${domainClass.propertyName}-form').form('clear');
			\$('#insert-ok-button').hide();
			\$('#edit-ok-button').show();
			\$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
			var allData=\$('#${domainClass.propertyName}-table').datagrid('getData');
			\$('#${domainClass.propertyName}-form').form('load',allData.rows[rowIndex]);
			<%props.eachWithIndex { p, i ->				
				if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == java.sql.Timestamp.class|| p.type == Calendar.class){
					if(p.name!='dateCreated' && p.name!='lastUpdated' ){
						def controllerStr='';
						if(p.type == Date.class || p.type == java.sql.Date.class || p.type == Calendar.class){
							controllerStr='datebox';
						}
						if(p.type == java.sql.Time.class || p.type == java.sql.Timestamp.class){
							controllerStr='datetimebox';
						}
				%>				
					\$('#${p.name}-insert').${controllerStr}('setValue',allData.rows[rowIndex].${p.name});
				<%}
				}
			}%>
			\$('#${domainClass.propertyName}-insert').window('open');
					
		}
		function deleteRowData(rowId){
			\$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message code="easyui.message.delete.confirm.body"/>', function(r){
				if (r){
						var obj=new Object();
						obj.fileIds=rowId;
						\$.post("\${request.contextPath}/${domainClass.propertyName}/ajaxDeletes",obj,
								function (data, textStatus){
									if(data.result){
										\$.messager.show({
											title:'<g:message code="easyui.message.delete.sucess.title"/>',
											msg:'<g:message code="easyui.message.delete.sucess.body"/>',
											timeout:4000,
											showType:'slide'
										});
										\$('#${domainClass.propertyName}-table').datagrid('reload');											
									}else{
										\$.messager.alert('<g:message code="easyui.message.delete.fault.title"/>',data.message,'error');
									}
								}, "json");
					}
			});	
		}
		<%
		props.eachWithIndex { p, i ->
			if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){
				unthField << "${p.name}FileName";
			%>
				function ${p.name}DownloadFormatter(value,rowData,rowIndex){
					var str='';
					if(value && value!='null'){
						str='<span><a href="#"  iconCls="icon-ok"  class="easyui-linkbutton"   onclick="${p.name}DownloadData(\\''+rowData.id+'\\',\\'${p.name}\\');">'+value+'</a></span>';
					}
					return str;
				}
				function ${p.name}DownloadData(id,value){
					location.href="\${request.contextPath}/${domainClass.propertyName}/"+value+"Download?id="+id;			
				}
		<%}
		   if(p.isAssociation()){%>
		       function  ${p.name}ShowFormatter(value,rowData,rowIndex){
					if(value && value!=''){
							return rowData['${p.name}.text'];
					}
			   }
			<%}	
		}%>

        function initLayout(){
            if(needInitLayout){
                \$('#insert-tabs').tabs({});
                //\$('#${domainClass.propertyName}-search').window({});
                \$('#${domainClass.propertyName}-search-layout').layout({});
                //\$('#${domainClass.propertyName}-insert').window({});
                \$('#${domainClass.propertyName}-insert-layout').layout({});
                 needInitLayout=false;
            }
        }