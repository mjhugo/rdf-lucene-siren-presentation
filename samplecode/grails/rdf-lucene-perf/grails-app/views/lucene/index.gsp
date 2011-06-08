<html>
<head>
    <title>Lucene Example</title>
    <meta name="layout" content="main"/>
</head>

<body>

<g:form action="query" method="GET">
    Query: <g:textField name="query" value="${params.query}" style="width: 400px;"/><br>
    <g:submitButton value="Submit" name="Submit"/>
</g:form>
<p>Query took <g:formatNumber number="${(time?:0)}" type="number"/>ms</p>
<g:if test="${results}">
    <div class="list">
        <table>
            <thead>
            <tr>
                <g:each in="${results[0]?.keySet()}" var="header">
                    <th>${header}</th>
                </g:each>
            </tr>
            </thead>
            <tbody>
            <g:each in="${results}" status="i" var="mapResult">
                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <g:each in="${mapResult.values()}" var="columnValue">
                        <td>${columnValue}</td>
                    </g:each>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</g:if>
<g:else>
    <p><strong>No results found</strong></p>
</g:else>

<p>Sample Queries</p>
<ul>
    <li>Partial Match</li>
    <ul>
        <li><g:link action="query" params="[query:'epsilon receptor']">epsilon receptor</g:link></li>
        <li><g:link action="query" params="[query:'abl1']">abl1</g:link></li>
    </ul>
</ul>
<g:link action="buildIndex">Rebuild index</g:link>

</body>
</html>
