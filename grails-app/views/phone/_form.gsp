<%@ page import="com.bjrxht.notify.Phone" %>



<div class="fieldcontain ${hasErrors(bean: phoneInstance, field: 'number', 'error')} required">
	<label for="number">
		<g:message code="phone.number.label" default="Number" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="number" required="" value="${phoneInstance?.number}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: phoneInstance, field: 'tag', 'error')} ">
	<label for="tag">
		<g:message code="phone.tag.label" default="Tag" />
		
	</label>
    <!--
	<g:textField name="tag" maxlength="100" value="${phoneInstance?.tag}"/>
-->
    ${phoneInstance?.tag}
</div>

<div class="fieldcontain ${hasErrors(bean: phoneInstance, field: 'accounts', 'error')} ">
	<label for="accounts">
		<g:message code="phone.accounts.label" default="Accounts" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${phoneInstance?.accounts?}" var="a">
    <li><g:link controller="account" action="show" id="${a.id}">${a?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="account" action="create" params="['phone.id': phoneInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'account.label', default: 'Account')])}</g:link>
</li>
</ul>

</div>

