import org.openrdf.model.impl.URIImpl
import org.openrdf.rio.RDFFormat
import org.openrdf.repository.RepositoryConnection

class BootStrap {

    def repository

    def init = { servletContext ->
        RepositoryConnection connection = repository.connection
        def size = connection.size()
        println "Initial repository size: ${size} triples"
        if (!size) {
            addFile('./data/diseasome_dump.nt', 'http://rdf.entagen.com/diseasome', connection)
            addFile('./data/dailymed_dump.nt', 'http://rdf.entagen.com/dailymed', connection)
            addFile('./data/drugbank_dump_clean.nt', 'http://rdf.entagen.com/drugbank', connection)
            addFile('./data/sider_dump.nt', 'http://rdf.entagen.com/sider', connection)
        }
        println "Repository initialized with ${connection.size()} triples"


    }
    def destroy = {
    }

    void addFile(String file, String contextUri, RepositoryConnection connection) {
        println("adding file ${file} to repository with context ${contextUri}")
        connection.add(new File(file),
                contextUri, RDFFormat.NTRIPLES,
                new URIImpl(contextUri))
    }

}
