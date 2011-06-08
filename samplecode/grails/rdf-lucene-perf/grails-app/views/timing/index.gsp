<html>
<head>
    <title>Timing</title>
    <meta name="layout" content="main"/>
</head>

<body>
<P>All times in Milliseconds</P>
<p><g:link action="clear">reset times</g:link></p>
<table>
    <tr>
        <g:each in="${['sparql', 'lucene', 'siren']}" var="sessionVariable">
            <td valign="top">
                <table>
                    <thead>
                    <tr>
                        <th>${sessionVariable} Time</th>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${session[sessionVariable]}" status="i" var="timing">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td>${timing}</td>
                        </tr>
                    </g:each>
                    <g:if test="${session[sessionVariable]}">
                        <tr>
                            <td>Avg: <g:formatNumber
                                    number="${session[sessionVariable].sum() / session[sessionVariable].size()}"
                                    type="number"/></td>
                        </tr>
                    </g:if>
                    </tbody>
                </table>
            </td>
        </g:each>

    </tr>
</table>

</body>
</html>
