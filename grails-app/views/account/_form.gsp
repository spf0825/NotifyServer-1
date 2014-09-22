<%@ page import="com.bjrxht.notify.Account" %>



<div class="fieldcontain ${hasErrors(bean: accountInstance, field: 'number', 'error')} required">
	<label for="number">
		<g:message code="account.number.label" default="Number" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="number" maxlength="200" required="" value="${accountInstance?.number}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: accountInstance, field: 'bank', 'error')} required">
	<label for="bank">
		<g:message code="account.bank.label" default="Bank" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="bank" name="bank.id" from="${com.bjrxht.notify.Bank.list()}" optionKey="id" required="" value="${accountInstance?.bank?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: accountInstance, field: 'phone', 'error')} ">
	<label for="phone">
		<g:message code="account.phone.label" default="Phone" />
		
	</label>
	<g:select id="phone" name="phone.id" from="${com.bjrxht.notify.Phone.list()}" optionKey="id" value="${accountInstance?.phone?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

