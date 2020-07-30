<#import "/parts/common.ftl" as c>
<#import "/parts/login.ftl" as l>

<@c.page>
<div class="mb-1">Add new user</div>
<!--${message!" " }-->
<!--Если у вас перестала работать страница регистрации, то добавьте ${message!" " }. -->
<!--Восклицательный знак - это дефолт значение, на которое будет ссылаться ftl, -->
<!--если не задан параметр message. Иначе будет ошибка.-->
<!--${message!}-->
${message?ifExists}
<@l.login "/registration" true />
</@c.page>