


        var needInitLayout=true;
        var booleanValues=[
				{id:'1',text:'<g:message code="easyui.boolean.select.true"/>'},
				{id:'0',text:'<g:message code="easyui.boolean.select.false"/>'}
                           ];
        var lastIndex;
		$(function(){
			$('#strategy-table').datagrid({
				//列表th
				columns:[[
					{field:'checked',checkbox:true,align:'center',title:'<g:message code="easyui.table.th.check"/>'},
                //    {field:'id',width:70,align:'center',sortable:true,title:'${message(code: 'strategy.id.label', default: 'id')}'},

    {field:'account.id',sortable:true,title:'账户',formatter:accountShowFormatter,width:15,align:'center'},

    {field:'type',sortable:true,title:'策略类型',width:10,align:'center'},


    {field:'max',sortable:true,title:'区间金额（最大值）',width:10,align:'center'},


    {field:'min',sortable:true,title:'区间金额（最小值）',width:10,align:'center'},
    /*
    {field:'min1',sortable:true,title:'min1',width:100,align:'center'},
    {field:'min2',sortable:true,title:'min2',width:100,align:'center'},
    {field:'phone1',sortable:true,title:'phone1',width:100,align:'center'},
    {field:'phone2',sortable:true,title:'phone2',width:100,align:'center'},
    */


                    {field:'rowOperator',title:'${message(code: 'easyui.table.th.operator')}',width:15,align:'center', formatter:operatorFormatter}
				]],
				//添加工具条
				toolbar:[{
					text:'<g:message code="easyui.toolbar.insert.text"/>',
					iconCls:'icon-add',
					handler:function(){
					    initLayout();
						$('#strategy-form').form('clear');
						$('#insert-ok-button').show();
						$('#edit-ok-button').hide();
						$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
						$('#strategy-insert').window('open');
					}
				},'-',
				{
					text:'<g:message code="easyui.toolbar.delete.text"/>',
					iconCls:'icon-remove',
					handler:function(){
						var deleteRows=$('#strategy-table').datagrid('getSelections');
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
									$.post("${request.contextPath}/strategy/ajaxDeletes",obj,
											function (data, textStatus){
												if(data.result){
													$.messager.show({
														title:'<g:message code="easyui.message.delete.sucess.title"/>',
														msg:'<g:message code="easyui.message.delete.sucess.body"/>',
														timeout:4000,
														showType:'slide'
													});
													$('#strategy-table').datagrid('reload');											
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
						$('#strategy-search').window('open');
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
						$('#strategy-table').datagrid('endEdit', lastIndex);
					}
				},
				onDblClickRow:function(rowIndex){
					if (lastIndex != rowIndex){
						$('#strategy-table').datagrid('endEdit', lastIndex);
						$('#strategy-table').datagrid('beginEdit', rowIndex);
					}
					lastIndex = rowIndex;
				},
				onLoadSuccess:function(data){
					var pager = $('#strategy-table').datagrid('getPager');
					pager.pagination({total:data.total});
					$('a.easyui-linkbutton').linkbutton({});
				},
				onAfterEdit:function(rowIndex, rowData, changes){
					var updateRows = $('#strategy-table').datagrid('getChanges','updated');
					for(var i=0;i<updateRows.length;i++){
						$.post("${request.contextPath}/strategy/ajaxRowUpdate",updateRows[i],
								function (data, textStatus){
									if(data.result){
										$.messager.show({
											title:'sucess',
											msg:'<g:message code="easyui.message.update.sucess.body1"/>'+data.id+'<g:message code="easyui.message.update.sucess.body2"/>',
											timeout:4000,
											showType:'slide'
										});
										$('#strategy-table').datagrid('acceptChanges');
										$('#strategy-table').datagrid('reload');
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
			var pager = $('#strategy-table').datagrid('getPager');
			pager.pagination({
				total:0,
				pageSize:${params.max},
				pageList:${params.maxList},
				onSelectPage:function(pageNumber, pageSize){
					$(this).pagination('loading');
					$('#max').attr("value", pageSize);
					$('#offset').attr("value", pageSize*pageNumber-pageSize);
					$('#strategy-table').datagrid('reload');
					$(this).pagination('loaded');
				}
			});
			$('#strategy-insert').window({});
            $('#strategy-search').window({});
		});
		//添加表头右键功能
		function createColumnMenu(){
			var tmenu = $('<div id="tmenu" style="width:100px;"></div>').appendTo('body');
			var fields = $('#strategy-table').datagrid('getColumnFields');
			for(var i=0; i<fields.length; i++){
				var menuOne=$('<div iconCls="icon-ok"/>');
				menuOne.attr('id',fields[i]);
				menuOne.html($('#strategy-table').datagrid('getColumnOption',fields[i]).title);
				menuOne.appendTo(tmenu);
			}
			tmenu.menu({
				onClick: function(item){
					if (item.iconCls=='icon-ok'){
						$('#strategy-table').datagrid('hideColumn', item.id);
						tmenu.menu('setIcon', {
							target: item.target,
							iconCls: 'icon-empty'
						});
					} else {
						$('#strategy-table').datagrid('showColumn', item.id);
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
				var pager = $('#strategy-table').datagrid('getPager');
				pager.pagination({pageNumber:1});
				$('#offset').attr("value",0);
				$('#searchField').attr("value", $('#searchFieldList').combobox('getValue'));
				$('#searchValue').attr("value", $('#searchValueInput').val());
				$('#strategy-table').datagrid('reload');
				$('#strategy-search').window('close');
			//}else{
			//}
		}
		function searchCancel(){
			$('#strategy-search').window('close');
		}
		function insertOk(){

			$('#strategy-form').form('submit',{
				url:'${request.contextPath}/strategy/ajaxInsert',
				success:function(data){
					var result=$.trim(data).split(':');
					if(result[0]=='true'){
						$('#strategy-table').datagrid('reload');
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
			$('#strategy-insert').window('close');
		}
		function editOk(){
			$('#strategy-form').form('submit',{
				url:'${request.contextPath}/strategy/ajaxUpdate',
				success:function(data){
					var result=$.trim(data).split(':');
					if(result[0]=='true'){
						$('#strategy-table').datagrid('reload');
						$.messager.show({
							title:'<g:message code="easyui.message.update.sucess.title"/>',
							msg:'<g:message code="easyui.message.update.sucess.body1"/>'+result[2]+'<g:message code="easyui.message.update.sucess.body2"/>',
							timeout:4000,
							showType:'slide'
						});												
					}else{
					    $('#strategy-table').datagrid('reload');
						$.messager.alert('<g:message code="easyui.message.insert.fault.title"/>',result[1],'error');
					}
				}
			});
			$('#strategy-insert').window('close');
		}
		function insertCancel(){
			$('#strategy-insert').window('close');
		}
		function operatorFormatter(value,rowData,rowIndex){
			var str='<span><a href="#"  iconCls="icon-ok"  class="easyui-linkbutton" onclick="editRowData(\''+rowIndex+'\');"><g:message code="easyui.table.tr.operator.edit"/></a>';
			str=str+'<a href="#" iconCls="icon-cancel"  class="easyui-linkbutton" onclick="deleteRowData(\''+rowData.id+'\');"><g:message code="easyui.table.tr.operator.delete"/></a></span>';
			return str;
		}
		function editRowData(rowIndex){
		    initLayout();
			$('#strategy-form').form('clear');
			$('#insert-ok-button').hide();
			$('#edit-ok-button').show();
			$('#insert-tabs').tabs('select','<g:message code="easyui.window.insert.first.tab.title"/>');
			var allData=$('#strategy-table').datagrid('getData');
			$('#strategy-form').form('load',allData.rows[rowIndex]);
            tractices();
			$('#strategy-insert').window('open');
					
		}
		function deleteRowData(rowId){
			$.messager.confirm('<g:message code="easyui.message.delete.confirm.title"/>', '<g:message code="easyui.message.delete.confirm.body"/>', function(r){
				if (r){
						var obj=new Object();
						obj.fileIds=rowId;
						$.post("${request.contextPath}/strategy/ajaxDeletes",obj,
								function (data, textStatus){
									if(data.result){
										$.messager.show({
											title:'<g:message code="easyui.message.delete.sucess.title"/>',
											msg:'<g:message code="easyui.message.delete.sucess.body"/>',
											timeout:4000,
											showType:'slide'
										});
										$('#strategy-table').datagrid('reload');											
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
            function getTypeValue(obj){
                if($(obj).val()=='complex'){
                    $('#tactics').css('display','inline');
                     $('#tactics1').css('display','none');
                }else if($(obj).val()=='between'){
                    $('#tactics1').css('display','inline');
                     $('#tactics').css('display','none');
                }else{
                    $("#tactics :input").each(function(){
                      $(this).val('');
                     });
                    $("#tactics1 :input").each(function(){
                      $(this).val('');
                     });
                    $('#tactics').css('display','none');
                    $('#tactics1').css('display','none');
                }
            }
            function tractices(){
                if($("select[name='type']").val()=='complex'){
                   $('#tactics').css('display','inline');
                   $('#tactics1').css('display','none');
                }else if($("select[name='type']").val()=='between'){
                   $('#tactics1').css('display','inline');
                   $('#tactics').css('display','none');
                }
            }
        function initLayout(){
            if(needInitLayout){
                $('#insert-tabs').tabs({});
                //$('#strategy-search').window({});
                $('#strategy-search-layout').layout({});
                //$('#strategy-insert').window({});
                $('#strategy-insert-layout').layout({});
                 needInitLayout=false;
            }
        }