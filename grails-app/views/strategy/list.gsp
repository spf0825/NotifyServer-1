
<%@ page import="com.bjrxht.notify.Strategy" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'strategy.label', default: 'Strategy')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-strategy" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-strategy" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<th><g:message code="strategy.account.label" default="Account" /></th>
					
						<g:sortableColumn property="type" title="${message(code: 'strategy.type.label', default: 'Type')}" />
					
						<g:sortableColumn property="max" title="${message(code: 'strategy.max.label', default: 'Max')}" />
					
						<g:sortableColumn property="min" title="${message(code: 'strategy.min.label', default: 'Min')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${strategyInstanceList}" status="i" var="strategyInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${strategyInstance.id}">${fieldValue(bean: strategyInstance, field: "account")}</g:link></td>
					
						<td>${fieldValue(bean: strategyInstance, field: "type")}</td>
					
						<td>${fieldValue(bean: strategyInstance, field: "max")}</td>
					
						<td>${fieldValue(bean: strategyInstance, field: "min")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${strategyInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
