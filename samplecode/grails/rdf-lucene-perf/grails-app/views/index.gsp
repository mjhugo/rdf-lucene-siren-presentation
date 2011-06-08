<html>
<head>
    <title>Rdf Lucene Example</title>
    <meta name="layout" content="main"/>
</head>

<body>

<h1>RDF Lucene Performance Examples</h1>
<g:set var="connection" value="${grailsApplication.mainContext.repository.connection}"/>
<p># of Triples: <g:formatNumber number="${connection.size()}" type="number"/></p>
<%connection.close()%>
</body>
</html>
