
<%@ page import="com.bjrxht.notify.Strategy" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'strategy.label', default: 'Strategy')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-strategy" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-strategy" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list strategy">
			
				<g:if test="${strategyInstance?.account}">
				<li class="fieldcontain">
					<span id="account-label" class="property-label"><g:message code="strategy.account.label" default="Account" /></span>
					
						<span class="property-value" aria-labelledby="account-label"><g:link controller="account" action="show" id="${strategyInstance?.account?.id}">${strategyInstance?.account?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${strategyInstance?.type}">
				<li class="fieldcontain">
					<span id="type-label" class="property-label"><g:message code="strategy.type.label" default="Type" /></span>
					
						<span class="property-value" aria-labelledby="type-label"><g:fieldValue bean="${strategyInstance}" field="type"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${strategyInstance?.max}">
				<li class="fieldcontain">
					<span id="max-label" class="property-label"><g:message code="strategy.max.label" default="Max" /></span>
					
						<span class="property-value" aria-labelledby="max-label"><g:fieldValue bean="${strategyInstance}" field="max"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${strategyInstance?.min}">
				<li class="fieldcontain">
					<span id="min-label" class="property-label"><g:message code="strategy.min.label" default="Min" /></span>
					
						<span class="property-value" aria-labelledby="min-label"><g:fieldValue bean="${strategyInstance}" field="min"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${strategyInstance?.id}" />
					<g:link class="edit" action="edit" id="${strategyInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
