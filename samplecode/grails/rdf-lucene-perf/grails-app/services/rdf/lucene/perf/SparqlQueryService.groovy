package rdf.lucene.perf

import org.openrdf.query.BindingSet
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.repository.RepositoryConnection
import org.openrdf.query.TupleQueryResult
import org.openrdf.query.TupleQueryResultHandler
import org.openrdf.repository.Repository

class SparqlQueryService {

    static transactional = false
    def repository

    public List executeQuery(String queryString) {
        def startTime = new Date().time
        String defaultPrefixes = """
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
           """
        queryString = defaultPrefixes + queryString

        log.debug "executing query [${queryString}]"

        List results = []
        TupleQueryResult result
        RepositoryConnection connection = repository.connection
        try {
            TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            result = tupleQuery.evaluate();
            List<String> bindingNames = result.bindingNames;

            while (result.hasNext()) {
                BindingSet bindingSet = result.next();

                Map tupleResult = [:]
                bindingNames.each {
                    def value = bindingSet.getValue(it)
                    tupleResult[it] = value?.stringValue()
                }

                log.trace tupleResult?.toString()
                results << tupleResult

            }
        } catch (Exception e) {
            log.error("Exception performing query ${queryString}", e)
            throw e
        }
        finally {
            result?.close()
            connection.close()
        }
        log.debug "returning ${results.size()} results for query"

        def endTime = new Date().time
        log.info("SparqlQuery execution time: ${(endTime - startTime) / 1000}s")

        return results
    }

    void executeForEach(Repository sourceRepo, String queryString, Closure closure) {
        String defaultPrefixes = """
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
             """
        queryString = defaultPrefixes + queryString

        log.trace("executing query [${queryString}]")

        TupleQueryResultHandler handler = new ClosureTupleQueryResultHandler(closure)
        RepositoryConnection sourceConn = sourceRepo.connection
        try {
            TupleQuery tupleQuery = sourceConn.prepareTupleQuery(QueryLanguage.SPARQL, queryString)
            tupleQuery.evaluate(handler)
        } catch (Exception e) {
            log.error("Exception performing query ${queryString}", e)
            throw e
        }
        finally {
            sourceConn.close()
        }
    }

    Map getLabelAndType(String subjectUri, RepositoryConnection connection) {
        String query = """
            SELECT ?label ?type WHERE {
                <${subjectUri}> rdf:type ?type .
                <${subjectUri}> rdfs:label ?label
            }
        """
        executeQuery(query).first()
    }

}
