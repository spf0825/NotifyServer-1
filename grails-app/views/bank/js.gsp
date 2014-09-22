var needInitLayout=true;
        var booleanValues=[
                {id:'1',text:'<g:message code="easyui.boolean.select.true"/>'},
				{id:'0',text:'<g:message code="easyui.boolean.select.false"/>'}
                           ];
        var lastIndex;
		$(function(){
			$('#bank-table').datagrid({
				//列表th
				columns:[[
					{field:'checked',checkbox:true,align:'center',title:'<g:message code="easyui.table.th.check"/>'},
                    {field:'id',width:70,align:'center',sortable:true,title:'${message(code: 'bank.id.label', default: 'id')}'},

    {field:'name',sortable:true,title:'name',width:100,align:'center'},


                    {field:'rowOperator',title:'${message(code: 'easyui.table.th.operator')}',width:200,align:'center', formatter:operatorFormatter}
				]],
				//添加工具条
				toolbar:[{
					text:'<g:message code="easyui.toolbar.insert.text"/>',
					iconCls:'icon-add',
					handler:function(){
					    initLayout();
						$('#bank-form').form('clear');
						$('#insert-ok-button').show();
						$('#edit-ok-button').hide();
						$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
						$('#bank-insert').window('open');
					}
				},'-',
				{
					text:'<g:message code="easyui.toolbar.delete.text"/>',
					iconCls:'icon-remove',
					handler:function(){
						var deleteRows=$('#bank-table').datagrid('getSelections');
						if(deleteRows!=null && deleteRows.length>0){
							$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message
        code="easyui.message.delete.confirm.body"/>', function(r){
								if (r){				
									var obj=new Object();
									var s = '';
									for(var i=0; i<deleteRows.length; i++){
										if (s != '') s += ',';
										s += deleteRows[i].id;
									}
									obj.fileIds=s;
									$.post("${request.contextPath}/bank/ajaxDeletes",obj,
											function (data, textStatus){
												if(data.result){
													$.messager.show({
														title:'<g:message code="easyui.message.delete.sucess.title"/>',
														msg:'<g:message code="easyui.message.delete.sucess.body"/>',
														timeout:4000,
														showType:'slide'
													});
													$('#bank-table').datagrid('reload');											
												}else{
													$.messager.alert('<g:message
        code="easyui.message.delete.fault.title"/>',data.message,'error');
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
						$('#bank-search').window('open');
					}
				}],
				pagination:true,
				onSortColumn:function(sort, order){
					$('#sort').attr("value", sort);
					$('#order').attr("value", order);
				},
				onBeforeLoad:function(params){
					$(this).datagrid('rejectChanges');
					params.max=$('#max').val();
					params.offset=$('#offset').val();
					params.order=$('#order').val();
					params.sort=$('#sort').val();
					params.searchField=$('#searchField').val();
					params.searchValue=$('#searchValue').val();
					//params.lang="zh_CN";					
				},
				onClickRow:function(rowIndex){
					if (lastIndex != rowIndex){
						$('#bank-table').datagrid('endEdit', lastIndex);
					}
				},
				onDblClickRow:function(rowIndex){
					if (lastIndex != rowIndex){
						$('#bank-table').datagrid('endEdit', lastIndex);
						$('#bank-table').datagrid('beginEdit', rowIndex);
					}
					lastIndex = rowIndex;
				},
				onLoadSuccess:function(data){
					var pager = $('#bank-table').datagrid('getPager');
					pager.pagination({total:data.total});
					$('a.easyui-linkbutton').linkbutton({});
				},
				onAfterEdit:function(rowIndex, rowData, changes){
					var updateRows = $('#bank-table').datagrid('getChanges','updated');
					for(var i=0;i<updateRows.length;i++){
						$.post("${request.contextPath}/bank/ajaxRowUpdate",updateRows[i],
								function (data, textStatus){
									if(data.result){
										$.messager.show({
											title:'sucess',
											msg:'<g:message
        code="easyui.message.update.sucess.body1"/>'+data.id+'<g:message code="easyui.message.update.sucess.body2"/>',
											timeout:4000,
											showType:'slide'
										});
										$('#bank-table').datagrid('acceptChanges');
										$('#bank-table').datagrid('reload');
										//$('a.easyui-linkbutton').linkbutton({});											
									}else{
										$.messager.alert('fault',data.message,'error');
										allsuccess=false;
									}
								}, "json");
					}	

				},
				//添加表头右键
				onHeaderContextMenu: function(e, field){
					e.preventDefault();
					if (!$('#tmenu').length){
						createColumnMenu();
					}
					$('#tmenu').menu('show', {
						left:e.pageX,
						top:e.pageY
					});
				}
			});
			//分页
			var pager = $('#bank-table').datagrid('getPager');
			pager.pagination({
				total:0,
				pageSize:${params.max},
				pageList:${params.maxList},
				onSelectPage:function(pageNumber, pageSize){
					$(this).pagination('loading');
					$('#max').attr("value", pageSize);
					$('#offset').attr("value", pageSize*pageNumber-pageSize);
					$('#bank-table').datagrid('reload');
					$(this).pagination('loaded');
				}
			});
			$('#bank-insert').window({});
            $('#bank-search').window({});
		});
		//添加表头右键功能
		function createColumnMenu(){
			var tmenu = $('<div id="tmenu" style="width:100px;"></div>').appendTo('body');
			var fields = $('#bank-table').datagrid('getColumnFields');
			for(var i=0; i<fields.length; i++){
				var menuOne=$('<div iconCls="icon-ok"/>');
				menuOne.attr('id',fields[i]);
				menuOne.html($('#bank-table').datagrid('getColumnOption',fields[i]).title);
				menuOne.appendTo(tmenu);
			}
			tmenu.menu({
				onClick: function(item){
					if (item.iconCls=='icon-ok'){
						$('#bank-table').datagrid('hideColumn', item.id);
						tmenu.menu('setIcon', {
							target: item.target,
							iconCls: 'icon-empty'
						});
					} else {
						$('#bank-table').datagrid('showColumn', item.id);
						tmenu.menu('setIcon', {
							target: item.target,
							iconCls: 'icon-ok'
						});
					}
				}
			});
		}
		function searchOk(){
			//if($('#searchValueInput').val()!=''){
				var pager = $('#bank-table').datagrid('getPager');
				pager.pagination({pageNumber:1});
				$('#offset').attr("value",0);
				$('#searchField').attr("value", $('#searchFieldList').combobox('getValue'));
				$('#searchValue').attr("value", $('#searchValueInput').val());
				$('#bank-table').datagrid('reload');
				$('#bank-search').window('close');
			//}else{
			//}
		}
		function searchCancel(){
			$('#bank-search').window('close');
		}
		function insertOk(){
			$('#bank-form').form('submit',{
				url:'${request.contextPath}/bank/ajaxInsert',
				success:function(data){
					var result=$.trim(data).split(':');
					if(result[0]=='true'){
						$('#bank-table').datagrid('reload');
						$.messager.show({
							title:'<g:message code="easyui.message.insert.sucess.title"/>',
							msg:'<g:message code="easyui.message.insert.sucess.body"/>',
							timeout:4000,
							showType:'slide'
						});												
					}else{
						$.messager.alert('<g:message code="easyui.message.insert.fault.title"/>',result[1],'error');
					}
				}
			});
			$('#bank-insert').window('close');
		}
		function editOk(){
			$('#bank-form').form('submit',{
				url:'${request.contextPath}/bank/ajaxUpdate',
				success:function(data){
					var result=$.trim(data).split(':');
					if(result[0]=='true'){
						$('#bank-table').datagrid('reload');
						$.messager.show({
							title:'<g:message code="easyui.message.update.sucess.title"/>',
							msg:'<g:message code="easyui.message.update.sucess.body1"/>'+result[2]+'<g:message
        code="easyui.message.update.sucess.body2"/>',
							timeout:4000,
							showType:'slide'
						});												
					}else{
						$.messager.alert('<g:message code="easyui.message.insert.fault.title"/>',result[1],'error');
					}
				}
			});
			$('#bank-insert').window('close');
		}
		function insertCancel(){
			$('#bank-insert').window('close');
		}
		function operatorFormatter(value,rowData,rowIndex){
			var str='<span><a href="#" iconCls="icon-ok" class="easyui-linkbutton" onclick="editRowData(\'' + rowIndex + '\');"><g:message code="easyui.table.tr.operator.edit"/></a>';
            str=str+'<a href="#" iconCls="icon-cancel" class="easyui-linkbutton" onclick="deleteRowData(\'' + rowData.id + '\');"><g:message code="easyui.table.tr.operator.delete"/></a></span>';
			return str;
		}
		function editRowData(rowIndex){
		    initLayout();
			$('#bank-form').form('clear');
			$('#insert-ok-button').hide();
			$('#edit-ok-button').show();
			$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
			var allData=$('#bank-table').datagrid('getData');
			$('#bank-form').form('load',allData.rows[rowIndex]);
			
			$('#bank-insert').window('open');
					
		}
		function deleteRowData(rowId){
			$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message
        code="easyui.message.delete.confirm.body"/>', function(r){
				if (r){
						var obj=new Object();
						obj.fileIds=rowId;
						$.post("${request.contextPath}/bank/ajaxDeletes",obj,
								function (data, textStatus){
									if(data.result){
										$.messager.show({
											title:'<g:message code="easyui.message.delete.sucess.title"/>',
											msg:'<g:message code="easyui.message.delete.sucess.body"/>',
											timeout:4000,
											showType:'slide'
										});
										$('#bank-table').datagrid('reload');											
									}else{
										$.messager.alert('<g:message code="easyui.message.delete.fault.title"/>',data.message,'error');
									}
								}, "json");
					}
			});	
		}
		

        function initLayout(){
            if(needInitLayout){
                $('#insert-tabs').tabs({});
                //$('#bank-search').window({});
                $('#bank-search-layout').layout({});
                //$('#bank-insert').window({});
                $('#bank-insert-layout').layout({});
                 needInitLayout=false;
            }
        }