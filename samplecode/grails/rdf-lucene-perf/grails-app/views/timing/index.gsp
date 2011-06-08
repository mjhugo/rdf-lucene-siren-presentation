<html>
<head>
    <title>Timing</title>
    <meta name="layout" content="main"/>
</head>

<body>

<p><g:link action="clear">reset times</g:link></p>
<P>All times in Milliseconds</P>
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
                    <g:if test="${session[sessionVariable]}">
                        <tr>
                            <td>Avg: <g:formatNumber
                                    number="${session[sessionVariable].sum() / session[sessionVariable].size()}"
                                    type="number"/></td>
                        </tr>
                    </g:if>
                    <g:each in="${session[sessionVariable]?.sort()?.reverse()}" status="i" var="timing">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td>${timing}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </td>
        </g:each>

    </tr>
</table>

</body>
</html>
