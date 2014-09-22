<% import grails.persistence.Event %>
<% import com.bjrxht.grails.annotation.Title %>
<%=packageName%>
<%
	cp = domainClass.constrainedProperties
	excludedProps = Event.allEvents.toList() << 'version' << 'id'
	allowedNames = domainClass.persistentProperties*.name  << 'dateCreated' << 'lastUpdated'<<'hadLoaded' //<<'soruceTable'<<'sourceId'
	props = domainClass.properties.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && !Collection.isAssignableFrom(it.type) }
	Collections.sort(props, comparator.constructors[0].newInstance([domainClass] as Object[]))
	def  unthField=[];
	props.eachWithIndex { p, i ->
		if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){
			unthField << "${p.name}FileName";
		}
	}	
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="easyui" />
<%
    def annotation=domainClass.getClazz().getAnnotation(Title);
    def entityName= annotation?.zh_CN()
    %>
        <title><g:message code="default.list.label" args="['${entityName}']" /></title>
		<script type="text/javascript" src="\${request.contextPath}/${domainClass.propertyName}/js"></script>    
    </head>
    <body>
    <div id="hiddenDiv" style="display:none;">
    	<form id="paginationForm">
	    	<input type="text" id="max" name="max" value="\${params.max}" />
	    	<input type="text" id="offset" name="offset" value="\${params.offset}" />
	    	<input type="text" id="sort" name="sort" value="\${params.sort}" />
	    	<input type="text" id="order" name="order" value="\${params.order}" />
	    	<input type="text" id="searchField" name="searchField" value="\${params.searchField}" />
	    	<input type="text" id="searchValue" name="searchValue" value="\${params.searchValue}" />
    	</form>
    </div>
	<table id="${domainClass.propertyName}-table" style="width:auto;height:auto;"  collapsible="true"
			title="<g:message code="easyui.table.title" args="['${entityName}']"/>" iconCls="icon-edit"
			 singleSelect="false"  striped="true" rownumbers="true"
			idField="id" url="\${request.contextPath}/${domainClass.propertyName}/json">
   </table>
	
	<div id="${domainClass.propertyName}-search"   maximizable="false" minimizable="false"  closed="true" modal="true" shadow="false" title="<g:message code="easyui.window.query.title"/>" iconCls="icon-save" style="width:500px;height:200px;padding:5px;background: #fafafa;">
		<div id="${domainClass.propertyName}-search-layout" fit="true">
			<div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
				<g:message code="easyui.window.query.field.text"/>
				<select id="searchFieldList" class="easyui-combobox" name="searchFieldList" style="width:100px;" required="true">
				<%props.eachWithIndex { p, i ->
                    def clazzName=domainClass.getClazz()
                    def ptitle= clazzName.getDeclaredField(p.name).getAnnotation(Title)?.zh_CN()?:p.name;
					if (!p.isAssociation()) {
						if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class||p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class || p.type == java.sql.Timestamp.class || p.type == Calendar.class){%>
						
					   <%}else{%>
							<option value="${p.name}">${ptitle}</option>
						<%}
					}	
				}%>
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
	<div id="${domainClass.propertyName}-insert"   maximizable="false" minimizable="false"   closed="true" modal="true" shadow="false" title="<g:message code="easyui.window.insert.title"/>" iconCls="icon-save" style="width:500px;height:500px;padding:5px;background: #fafafa;">
		<div id="${domainClass.propertyName}-insert-layout" fit="true">
			<div region="center" border="false" style="height: 70%;padding:10px;background:#fff;border:1px solid #ccc;">
				<form id="${domainClass.propertyName}-form" action="ajaxInsert" method="post" enctype="multipart/form-data">
				<input type="hidden" name="id" value=""/>
				<div id="insert-tabs" class="easyui-tabs"  style="width:auto;height:auto;">
					<div title="<g:message code="easyui.window.insert.first.tab.title"/>" style="padding:20px;">							
					    	<table>
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
									def editor="<input class='easyui-${type}' type='text' required='${required}' name='${p.name}'/>"
									if(cp[p.name]?.inList){
										type="combobox"
										editor="<select class='easyui-${type}' editable='false' valueField='id' textField='text' url='\${request.contextPath}/${domainClass.propertyName}/inList?field=${p.name}' name='${p.name}'/>"
									}
									if(p.type == ([] as Byte[]).class || p.type == ([] as byte[]).class){
										editor="<input type='file'   name='${p.name}' />"
									}
									if(p.type == Date.class || p.type == java.sql.Date.class || p.type == java.sql.Time.class  || p.type == java.sql.Timestamp.class|| p.type == Calendar.class){
										if(p.type == Date.class || p.type == java.sql.Date.class ||p.type == Calendar.class){
											editor="<input class='easyui-datebox' id='${p.name}-insert' name='${p.name}' editable='false' required='${required}'  style='width:150px' />"
										}else{
											editor="<input class='easyui-datetimebox' showSeconds='true' id='${p.name}-insert' name='${p.name}' editable='false' required='${required}'  style='width:150px' />"
										}
									}
									if(p.type == String.class && cp[p.name].maxSize > 250){
										 editor="<textarea class='easyui-validatebox'  name='${p.name}'  required='${required}' style='height:100px;'></textarea>";
									}
									if (p.type == Boolean.class || p.type == boolean.class){
										editor="""
											<select class='easyui-combobox' name='${p.name}'  required="${required}" editable="false" >
												<option value="1"><g:message code="easyui.boolean.select.true"/></option>
												<option value="0"><g:message code="easyui.boolean.select.false"/></option>
											</select>
										"""
									}
									if (!p.isAssociation()) {
										if(p.name!='dateCreated' && p.name!='lastUpdated' && !unthField.contains("${p.name}")){%>
					    					<tr><td>${ptitle}:</td><td>${editor}</td></tr>
								   		<%}
									}else{
								   		if (p.manyToOne || p.oneToOne){
									   		type="combobox"
											editor="<select class='easyui-${type}' editable='false' valueField='id' textField='text' url='\${request.contextPath}/${domainClass.propertyName}/inList?field=${p.name}' name='${p.name}.id'/>"
								   		}%>
								   		<tr><td>${ptitle}:</td><td>${editor}</td></tr>
								   	<%}%>
								   	
								<%}%>
					    	</table>
					    	
					</div>
					<%domainClass.getPersistentProperties().toList().each{p->
						if(p.isAssociation() && (p.oneToMany || p.manyToMany)){%>
							<div title="${p.name}" closable="true" style="padding:20px;" cache="false" >
								<g:set var="${p.name}" value="\${${p.referencedDomainClass?.fullName}.list()}" />
								<g:each var="obj" in="\${${p.name}}">
								 <input type="checkbox" value="\${obj.id}" name="checkbox_${p.name}"/>\${obj.toString()}<br/>
								</g:each>
							</div>	
						<%}
					}%>

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
    </body>
</html>
