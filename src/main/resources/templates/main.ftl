<#import "parts/common.ftl" as c>

<@c.page>
<div class="form-row">
<div class="form-group col-md-6">
    <!--        action - адрес, на который направлется форма-->
    <form method="get" action="/main" class="form-inline">
        <!--    <input type="text" name="filter" value="${filter!}">-->
        <input type="text" name="filter" class="form-control" value="${filter?ifExists}" placeholder="Search by tag">
        <!--    <input type="text" name="filter" value='${filter!""}'>-->
        <button type="submit" class="btn btn-primary ml-2">Search</button>
    </form>
</div>
</div>
<!--include непосредственно вставляет код шаблона в этом месте, поэтому не нужен алиас-->
<#include "parts/messageEdit.ftl" />

<#include "parts/messageList.ftl" />

</@c.page>
