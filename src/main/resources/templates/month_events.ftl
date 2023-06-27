<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Предстоящие события</title>
    <link href="/static/style/report.css" rel="stylesheet">
</head>
<body>
<h2>График на ${report.month}</h2>
<div class="table-container">
    <div class="event-table">
        <#list report.columns as item>
            <div class="date-column">
                <div class="date-title">
                    ${item.name}
                </div>
                <#list item.records as record>
                    <#if record.header == true>
                        <div class="event-header">${record.title}</div>
                    <#else>
                       <div class="event-title">${record.title}</div>
                    </#if>
                </#list>
            </div>
        </#list>
    </div>
</div>
</body>
</html>
