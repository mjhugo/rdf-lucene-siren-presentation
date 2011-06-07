package rdf.lucene.perf

import org.openrdf.query.TupleQueryResult
import org.openrdf.repository.RepositoryConnection
import org.openrdf.query.TupleQuery
import org.openrdf.query.BindingSet
import org.openrdf.query.QueryLanguage

class SparqlController {

    def repository

    def index = { }

    public List executeQuery(String queryString) {
        def startTime = new Date().time
        String defaultPrefixes = """
                PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                PREFIX sider: <http://www4.wiwiss.fu-berlin.de/diseasome/resource/sider/>
                PREFIX dailymed: <http://www4.wiwiss.fu-berlin.de/diseasome/resource/dailymed/>
                PREFIX diseasome: <http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/>
                PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/>
                PREFIX dbpedia: <http://dbpedia.org/ontology/>
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

}
