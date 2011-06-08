<!DOCTYPE html>
<html>
<head>
    <title><g:layoutTitle default="Grails"/></title>
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
    <style>
    .even {
        background-color: #cccccc;
    }

    </style>
    <g:layoutHead/>
    <g:javascript library="application"/>
</head>

<body>
<div id="spinner" class="spinner" style="display:none;">
    <img src="${resource(dir: 'images', file: 'spinner.gif')}"
         alt="${message(code: 'spinner.alt', default: 'Loading...')}"/>
</div>
<g:layoutBody/>
<p><g:link uri="/">Home</g:link> |
    <g:link controller="sparql">Sparql</g:link> |
    <g:link controller="lucene">Lucene</g:link> |
    <g:link controller="siren">Siren</g:link> |
    <g:link controller="timing">Timing</g:link></p>
</body>
</html>