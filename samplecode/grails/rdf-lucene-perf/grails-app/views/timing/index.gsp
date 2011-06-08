<html>
<head>
    <title>Timing</title>
    <meta name="layout" content="main"/>
</head>

<body>
<table>
    <tr>
        <g:each in="${['sparql', 'lucene', 'siren']}" var="sessionVariable">
            <td valign="top ">
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
                    </tbody>
                </table>
            </td>
        </g:each>

    </tr>
</table>

</body>
</html>
