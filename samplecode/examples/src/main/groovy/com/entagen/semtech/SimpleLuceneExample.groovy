package com.entagen.semtech

import org.apache.log4j.Logger
import org.openrdf.model.impl.URIImpl
import org.openrdf.query.BindingSet
import org.openrdf.query.QueryLanguage
import org.openrdf.query.TupleQuery
import org.openrdf.query.TupleQueryResult
import org.openrdf.repository.RepositoryConnection
import org.openrdf.repository.sail.SailRepository
import org.openrdf.rio.RDFFormat
import org.openrdf.sail.nativerdf.NativeStore

class SimpleLuceneExample {

    Logger logger = Logger.getLogger(SimpleLuceneExample.class);

    NativeStore fileStore
    SailRepository repository

    SimpleLuceneExample() {
        fileStore = new NativeStore(new File('sesame-example'), "spoc,posc")
        repository = new SailRepository(fileStore)
        repository.initialize()

        RepositoryConnection connection = repository.connection
        def size = connection.size()
        logger.info "Initial repository size: ${size} triples"
        if (!size) {
            addFile('/diseasome_dump.nt', 'http://rdf.entagen.com/diseasome', connection)
            addFile('/dailymed_dump.nt', 'http://rdf.entagen.com/dailymed', connection)
            addFile('/drugbank_dump_clean.nt', 'http://rdf.entagen.com/drugbank', connection)
            addFile('/sider_dump.nt', 'http://rdf.entagen.com/sider', connection)
        }
        logger.info "Repository initialized with ${connection.size()} triples"
    }

    void addFile(String file, String contextUri, RepositoryConnection connection) {
        logger.info("adding file ${file} to repository with context ${contextUri}")
        connection.add(getClass().getResource(file),
                contextUri, RDFFormat.NTRIPLES,
                new URIImpl(contextUri))
    }

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

        logger.debug "executing query [${queryString}]"

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

                logger.trace tupleResult?.toString()
                results << tupleResult

            }
        } catch (Exception e) {
            logger.error("Exception performing query ${queryString}", e)
            throw e
        }
        finally {
            result?.close()
            connection.close()
        }
        logger.debug "returning ${results.size()} results for query"

        def endTime = new Date().time
        logger.info("SparqlQuery execution time: ${(endTime - startTime) / 1000}s")

        return results
    }


}
