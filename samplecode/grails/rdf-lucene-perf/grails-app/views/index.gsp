<html>
<head>
    <title>Rdf Lucene Example</title>
    <meta name="layout" content="main"/>
</head>

<body>

<h1>Performance Examples</h1>
<g:set var="connection" value="${grailsApplication.mainContext.repository.connection}"/>
<p># of Triples: <g:formatNumber number="${connection.size()}" type="number"/></p>
<%connection.close()%>
<p>Loaded datasets</p>
<ul>
    <li><g:link url="http://www4.wiwiss.fu-berlin.de/drugbank/">DrugBank</g:link></li>
    <li><g:link url="http://www4.wiwiss.fu-berlin.de/diseasome/">Diseasome</g:link></li>
    <li><g:link url="http://www4.wiwiss.fu-berlin.de/dailymed/">DailyMed</g:link></li>
    <li><g:link url="http://www4.wiwiss.fu-berlin.de/sider/">Sider</g:link></li>
    <li><g:link url="http://data.linkedct.org/">LinkedCT</g:link></li>
</ul>
</body>
</html>
