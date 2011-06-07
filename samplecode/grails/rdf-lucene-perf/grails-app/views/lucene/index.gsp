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
    <li>Exact Match</li>
    <ul>
        <li>High affinity immunoglobulin epsilon receptor subunit alpha</li>
        <li>Proto-oncogene tyrosine-protein kinase ABL1</li>
        <li>Pyrin</li>
    </ul>
    <li>Partial Match</li>
    <ul>
        <li>epsilon receptor</li>
        <li>ABL1</li>
    </ul>
</ul>
</body>
</html>
