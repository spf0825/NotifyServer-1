<%@ page import="com.bjrxht.notify.Strategy" %>



<div class="fieldcontain ${hasErrors(bean: strategyInstance, field: 'account', 'error')} required">
	<label for="account">
		<g:message code="strategy.account.label" default="Account" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="account" name="account.id" from="${com.bjrxht.notify.Account.list()}" optionKey="id" required="" value="${strategyInstance?.account?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: strategyInstance, field: 'type', 'error')} ">
	<label for="type">
		<g:message code="strategy.type.label" default="Type" />
		
	</label>
	<g:select name="type" from="${strategyInstance.constraints.type.inList}" value="${strategyInstance?.type}" valueMessagePrefix="strategy.type" noSelection="['': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: strategyInstance, field: 'max', 'error')} ">
	<label for="max">
		<g:message code="strategy.max.label" default="Max" />
		
	</label>
	<g:textField name="max" value="${strategyInstance?.max}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: strategyInstance, field: 'min', 'error')} ">
	<label for="min">
		<g:message code="strategy.min.label" default="Min" />
		
	</label>
	<g:textField name="min" value="${strategyInstance?.min}"/>
</div>

