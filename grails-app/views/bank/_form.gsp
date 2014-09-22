<%@ page import="com.bjrxht.notify.Bank" %>



<div class="fieldcontain ${hasErrors(bean: bankInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="bank.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" maxlength="200" required="" value="${bankInstance?.name}"/>
</div>

