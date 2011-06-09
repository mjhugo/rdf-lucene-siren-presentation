package rdf.lucene.perf

class SparqlController {

    def repository
    def sparqlQueryService

    def index = {
        render(view: 'index')
    }

    def query = {
        String queryString
        if (params.type == 'Exact') {
            queryString = """
                SELECT ?uri ?type ?label
                ${params.from ? ('FROM <' + params.from + '>') : ''}
                WHERE {
                  ?uri rdfs:label ?label .
                  ?uri rdf:type ?type .
                  FILTER (?label = '${params.query}')
                } LIMIT 10
                """
        } else {
            queryString = """
                SELECT ?uri ?type ?label
                ${params.from ? ('FROM <' + params.from +'>') : ''}
                WHERE {
                  ?uri rdfs:label ?label .
                  ?uri rdf:type ?type .
                  FILTER regex(?label, '\\\\Q${params.query}\\\\E', 'i')
                } LIMIT 10
                """
        }

        def s = new Date().time
        List results = sparqlQueryService.executeQuery(queryString)
        def e = new Date().time

        render(view: 'index', model: [results: results, time: e - s])
    }


}
