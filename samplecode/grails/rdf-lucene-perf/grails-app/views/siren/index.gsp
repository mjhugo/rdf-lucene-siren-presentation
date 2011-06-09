<html>
<head>
    <title>SIREn Example</title>
    <meta name="layout" content="main"/>
</head>

<body>

<g:form action="query" method="GET">
    Query: <g:textField name="query" value="${params.query}" style="width: 400px;"/><br>
    Related SubjectURI: <g:textField name="relatedUri" value="${params.relatedUri}" style="width: 400px;"/><br>
    <g:submitButton value="Submit" name="Submit"/>
</g:form>
<p style="background-color: yellow; padding: 5px;">Query took <g:formatNumber number="${(time?:0)}" type="number"/>ms</p>
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
        <% def queryString = 'High affinity immunoglobulin epsilon receptor subunit alpha' %>
        <li><g:link action="query"
                    params="[query:queryString]">High affinity immunoglobulin epsilon receptor subunit alpha</g:link></li>
        <% queryString = 'Pyrin' %>
        <li><g:link action="query" params="[query:queryString]">Pyrin</g:link></li>
    </ul>
    <li>Partial Match</li>
    <ul>
        <li><g:link action="query" params="[query:'epsilon']">epsilon</g:link></li>
        <li><g:link action="query" params="[query:'abl1']">abl1</g:link></li>
        <li><g:link action="query" params="[query:'leukemia']">leukemia</g:link></li>
    </ul>
    <li>Related Queries</li>
    <ul>
        <li><g:link action="query"
                    params="[query:'',
                    relatedUri:'http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/ABL1']">Query for entities related to ABL1 (http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/ABL1)</g:link></li>
        <li><g:link action="query"
                    params="[query:'imatinib',
                    relatedUri:'http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/ABL1']">Query for entities with the text "imatnib" in the label related to Abl1 (http://www4.wiwiss.fu-berlin.de/diseasome/resource/genes/ABL1)</g:link></li>
    </ul>
</ul>
<g:link action="buildIndex">Rebuild index</g:link>

</body>
</html>
