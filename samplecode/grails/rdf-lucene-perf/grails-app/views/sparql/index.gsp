<html>
<head>
    <title>Sparql Example</title>
    <meta name="layout" content="main"/>
</head>

<body>

<g:form action="query" method="GET">
    Query: <g:textField name="query" value="${params.query}" style="width: 400px;"/><br>
    From Context: <g:textField name="from" value="${params.from}" style="width: 400px;"/><br>
    Type of match: <g:select name="type" from="['Exact', 'Partial']" value="${params.type}"/><br>

    <g:submitButton value="Submit" name="Submit"/>
</g:form>
<g:if test="${time}">
    <p style="background-color: yellow; padding: 5px;">Query took <g:formatNumber number="${time}" type="number"/>ms</p>
</g:if>
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
    <li>Exact Match From Context</li>
    <ul>
        <li><g:link action="query"
                    params="[query:'High affinity immunoglobulin epsilon receptor subunit alpha', type:'Exact', from:'http://rdf.entagen.com/drugbank']">High affinity immunoglobulin epsilon receptor subunit alpha</g:link></li>
        <li><g:link action="query"
                    params="[query:'Proto-oncogene tyrosine-protein kinase ABL1', type:'Exact', from:'http://rdf.entagen.com/drugbank']">Proto-oncogene tyrosine-protein kinase ABL1</g:link></li>
        <li><g:link action="query" params="[query:'Pyrin', type:'Exact', from:'http://rdf.entagen.com/drugbank']">Pyrin</g:link></li>
    </ul>
    <li>Exact Match</li>
    <ul>
        <li><g:link action="query"
                    params="[query:'High affinity immunoglobulin epsilon receptor subunit alpha', type:'Exact']">High affinity immunoglobulin epsilon receptor subunit alpha</g:link></li>
        <li><g:link action="query"
                    params="[query:'Proto-oncogene tyrosine-protein kinase ABL1', type:'Exact']">Proto-oncogene tyrosine-protein kinase ABL1</g:link></li>
        <li><g:link action="query" params="[query:'Pyrin', type:'Exact']">Pyrin</g:link></li>
    </ul>
    <li>Partial Match</li>
    <ul>
        <li><g:link action="query" params="[query:'epsilon receptor', type:'Partial']">epsilon receptor</g:link></li>
        <li><g:link action="query" params="[query:'ABL1', type:'Partial']">Abl1</g:link></li>
    </ul>
</ul>
</body>
</html>
