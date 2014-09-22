


        var needInitLayout=true;
        var booleanValues=[
				{id:'1',text:'<g:message code="easyui.boolean.select.true"/>'},
				{id:'0',text:'<g:message code="easyui.boolean.select.false"/>'}
                           ];
        var lastIndex;
		$(function(){
			$('#info-table').datagrid({
				//列表th
				columns:[[
					{field:'checked',checkbox:true,align:'center',title:'<g:message code="easyui.table.th.check"/>'},
                    //{field:'id',width:70,align:'center',sortable:true,title:'${message(code: 'info.id.label', default: 'id')}'},
                    //{field:'uuid',width:170,align:'center',sortable:false,title:'${message(code: 'info.id.label', default: 'uuid')}'},
    {field:'account.id',sortable:true,title:'账户',formatter:accountShowFormatter,width:100,align:'center'},

    {field:'money',sortable:true,title:'支付金额',width:100,align:'center'},


    {field:'payee',sortable:true,title:'收款人',width:100,align:'center'},


    {field:'result',sortable:true,title:'确认结果',width:100,align:'center'},
     // {field:'remark',sortable:true,title:'备注',width:100,align:'center'},
          {field:'trCode',sortable:true,title:'trCode',width:100,align:'center'},
                {field:'seqNo',sortable:true,title:'seqNo',width:100,align:'center'},
                      {field:'chnNo',sortable:true,title:'chnNo',width:100,align:'center'},
    {field:'dateCreated',sortable:true,title:'操作时间',width:100,align:'center'},


  //  {field:'lastUpdated',sortable:true,title:'修改时间',width:100,align:'center'},


                    {field:'rowOperator',title:'${message(code: 'easyui.table.th.operator')}',width:200,align:'center', formatter:operatorFormatter}
				]],
				//添加工具条
				toolbar:[{
					text:'<g:message code="easyui.toolbar.insert.text"/>',
					iconCls:'icon-add',
					handler:function(){
					    initLayout();
						//$('#info-form').form('clear');
						$('#insert-ok-button').show();
						$('#edit-ok-button').hide();
						$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
						$('#info-insert').window('open');
					}
				},'-',
				{
					text:'<g:message code="easyui.toolbar.delete.text"/>',
					iconCls:'icon-remove',
					handler:function(){
						var deleteRows=$('#info-table').datagrid('getSelections');
						if(deleteRows!=null && deleteRows.length>0){
							$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message code="easyui.message.delete.confirm.body"/>', function(r){
								if (r){				
									var obj=new Object();
									var s = '';
									for(var i=0; i<deleteRows.length; i++){
										if (s != '') s += ',';
										s += deleteRows[i].id;
									}
									obj.fileIds=s;
									$.post("${request.contextPath}/info/ajaxDeletes",obj,
											function (data, textStatus){
												if(data.result){
													$.messager.show({
														title:'<g:message code="easyui.message.delete.sucess.title"/>',
														msg:'<g:message code="easyui.message.delete.sucess.body"/>',
														timeout:4000,
														showType:'slide'
													});
													$('#info-table').datagrid('reload');											
												}else{
													$.messager.alert('<g:message code="easyui.message.delete.fault.title"/>',data.message,'error');
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
						$('#info-search').window('open');
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
						$('#info-table').datagrid('endEdit', lastIndex);
					}
				},
				onDblClickRow:function(rowIndex){
					if (lastIndex != rowIndex){
						$('#info-table').datagrid('endEdit', lastIndex);
						$('#info-table').datagrid('beginEdit', rowIndex);
					}
					lastIndex = rowIndex;
				},
				onLoadSuccess:function(data){
					var pager = $('#info-table').datagrid('getPager');
					pager.pagination({total:data.total});
					$('a.easyui-linkbutton').linkbutton({});
				},
				onAfterEdit:function(rowIndex, rowData, changes){
					var updateRows = $('#info-table').datagrid('getChanges','updated');
					for(var i=0;i<updateRows.length;i++){
						$.post("${request.contextPath}/info/ajaxRowUpdate",updateRows[i],
								function (data, textStatus){
									if(data.result){
										$.messager.show({
											title:'sucess',
											msg:'<g:message code="easyui.message.update.sucess.body1"/>'+data.id+'<g:message code="easyui.message.update.sucess.body2"/>',
											timeout:4000,
											showType:'slide'
										});
										$('#info-table').datagrid('acceptChanges');
										$('#info-table').datagrid('reload');
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
			var pager = $('#info-table').datagrid('getPager');
			pager.pagination({
				total:0,
				pageSize:${params.max},
				pageList:${params.maxList},
				onSelectPage:function(pageNumber, pageSize){
					$(this).pagination('loading');
					$('#max').attr("value", pageSize);
					$('#offset').attr("value", pageSize*pageNumber-pageSize);
					$('#info-table').datagrid('reload');
					$(this).pagination('loaded');
				}
			});
			$('#info-insert').window({});
            $('#info-search').window({});
		});
		//添加表头右键功能
		function createColumnMenu(){
			var tmenu = $('<div id="tmenu" style="width:100px;"></div>').appendTo('body');
			var fields = $('#info-table').datagrid('getColumnFields');
			for(var i=0; i<fields.length; i++){
				var menuOne=$('<div iconCls="icon-ok"/>');
				menuOne.attr('id',fields[i]);
				menuOne.html($('#info-table').datagrid('getColumnOption',fields[i]).title);
				menuOne.appendTo(tmenu);
			}
			tmenu.menu({
				onClick: function(item){
					if (item.iconCls=='icon-ok'){
						$('#info-table').datagrid('hideColumn', item.id);
						tmenu.menu('setIcon', {
							target: item.target,
							iconCls: 'icon-empty'
						});
					} else {
						$('#info-table').datagrid('showColumn', item.id);
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
				var pager = $('#info-table').datagrid('getPager');
				pager.pagination({pageNumber:1});
				$('#offset').attr("value",0);
				$('#searchField').attr("value", $('#searchFieldList').combobox('getValue'));
				$('#searchValue').attr("value", $('#searchValueInput').val());
				$('#info-table').datagrid('reload');
				$('#info-search').window('close');
			//}else{
			//}
		}
		function searchCancel(){
			$('#info-search').window('close');
		}
		function insertOk(){
			$('#info-form').form('submit',{
				url:'${request.contextPath}/info/ajaxInsert',
				success:function(data){
					var result=$.trim(data).split(':');
					if(result[0]=='true'){
						$('#info-table').datagrid('reload');
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
			$('#info-insert').window('close');
		}
		function editOk(){
			$('#info-form').form('submit',{
				url:'${request.contextPath}/info/ajaxUpdate',
				success:function(data){
					var result=$.trim(data).split(':');
					if(result[0]=='true'){
						$('#info-table').datagrid('reload');
						$.messager.show({
							title:'<g:message code="easyui.message.update.sucess.title"/>',
							msg:'<g:message code="easyui.message.update.sucess.body1"/>'+result[2]+'<g:message code="easyui.message.update.sucess.body2"/>',
							timeout:4000,
							showType:'slide'
						});												
					}else{
						$.messager.alert('<g:message code="easyui.message.insert.fault.title"/>',result[1],'error');
					}
				}
			});
			$('#info-insert').window('close');
		}
		function insertCancel(){
			$('#info-insert').window('close');
		}
		function operatorFormatter(value,rowData,rowIndex){
			var str='<span><a href="#"  iconCls="icon-ok"  class="easyui-linkbutton" onclick="editRowData(\''+rowIndex+'\');"><g:message code="easyui.table.tr.operator.edit"/></a>';
			str=str+'<a href="#" iconCls="icon-cancel"  class="easyui-linkbutton" onclick="deleteRowData(\''+rowData.id+'\');"><g:message code="easyui.table.tr.operator.delete"/></a></span>';
			return str;
		}
		function editRowData(rowIndex){
		    initLayout();
			$('#info-form').form('clear');
			$('#insert-ok-button').hide();
			$('#edit-ok-button').show();
			$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
			var allData=$('#info-table').datagrid('getData');
			$('#info-form').form('load',allData.rows[rowIndex]);
			
			$('#info-insert').window('open');
					
		}
		function deleteRowData(rowId){
			$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message code="easyui.message.delete.confirm.body"/>', function(r){
				if (r){
						var obj=new Object();
						obj.fileIds=rowId;
						$.post("${request.contextPath}/info/ajaxDeletes",obj,
								function (data, textStatus){
									if(data.result){
										$.messager.show({
											title:'<g:message code="easyui.message.delete.sucess.title"/>',
											msg:'<g:message code="easyui.message.delete.sucess.body"/>',
											timeout:4000,
											showType:'slide'
										});
										$('#info-table').datagrid('reload');											
									}else{
										$.messager.alert('<g:message code="easyui.message.delete.fault.title"/>',data.message,'error');
									}
								}, "json");
					}
			});	
		}
		
		       function  accountShowFormatter(value,rowData,rowIndex){
					if(value && value!=''){
							return rowData['account.text'];
					}
			   }
			

        function initLayout(){
            if(needInitLayout){
                $('#insert-tabs').tabs({});
                //$('#info-search').window({});
                $('#info-search-layout').layout({});
                //$('#info-insert').window({});
                $('#info-insert-layout').layout({});
                 needInitLayout=false;
            }
        }