<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Предстоящие события</title>
    <link href="/static/style/report.css" rel="stylesheet">
</head>
<body>
<h2>График на текущий месяц</h2>
<div class="table-container">
    <div class="event-table">
        <#list report as date, events>
            <div class="date-column">
                <div class="date-title">
                    ${date}
                </div>
                <#list events as event>
                    <#if event.header == true>
                        <div class="event-header">${event.title}</div>
                    <#else>
                       <div class="event-title">${event.title}</div>
                    </#if>
                </#list>
            </div>
        </#list>
    </div>

</div>
</body>
</html>
