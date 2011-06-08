package rdf.lucene.perf

import org.apache.lucene.document.Field
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.analysis.standard.StandardAnalyzer

import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.search.IndexSearcher
import org.sindice.siren.search.SirenCellQuery
import org.openrdf.repository.RepositoryConnection
import org.openrdf.rio.ntriples.NTriplesWriter
import org.openrdf.model.impl.URIImpl
import org.sindice.siren.analysis.TupleAnalyzer
import org.apache.lucene.util.Version

class SirenController {
    static final String TRIPLES_FIELD = 'triples'
    static final String SUBJECT_URI_FIELD = 'subjecturi'

    public static final String sirenIndexDir = 'target/siren-index'

    def sparqlQueryService
    def repository
    def sirenSearcherManager

    def index = { }

    def query = {

        SirenCellQuery query = new SirenCellQuery()

        def s = new Date().time
        List results = executeQuery(query)
        def e = new Date().time

        render(view: 'index', model: [results: results, time: e - s])
    }


    public List executeQuery(Query query) {
        IndexSearcher searcher = luceneSearcherManager.get()
        ScoreDoc[] scoreDocs = searcher.search(query, 10).scoreDocs
        List results = []
        def connection = repository.connection
        scoreDocs.each {
            Document doc = searcher.doc(it.doc)
            String uri = doc[SUBJECT_URI_FIELD]
            Map labelAndType = sparqlQueryService.getLabelAndType(uri, connection)
            results << [uri: uri, type: labelAndType.type, label: labelAndType.label]
        }
        connection.close()
        return results
    }

    def buildIndex = {
        File indexDir = new File(sirenIndexDir);
        indexDir.delete()

        def s = new Date().time
        IndexWriter writer = new IndexWriter(org.apache.lucene.store.FSDirectory.open(indexDir),
                new TupleAnalyzer(new StandardAnalyzer(Version.LUCENE_CURRENT)),
                true, IndexWriter.MaxFieldLength.UNLIMITED)

        RepositoryConnection connection = repository.connection
        try {

            String subjectUris = """
               SELECT distinct ?uri
               WHERE {
                   ?uri ?p ?o .
               }
            """

            sparqlQueryService.executeForEach(repository, subjectUris) {
                def doc = new Document()

                doc.add(new Field(SUBJECT_URI_FIELD, it.uri.stringValue(), Field.Store.YES, Field.Index.ANALYZED))

                StringWriter triplesStringWriter = new StringWriter()
                NTriplesWriter nTriplesWriter = new NTriplesWriter(triplesStringWriter)
                connection.exportStatements(new URIImpl(it.uri.stringValue()), null, null, false, nTriplesWriter)

                doc.add(new Field(TRIPLES_FIELD, triplesStringWriter.toString(), Field.Store.NO, Field.Index.ANALYZED))

                writer.addDocument(doc)
            }
        } finally {
            writer.close()
            connection.close()
        }

        def e = new Date().time
        sirenSearcherManager.maybeReopen()
        render "Done building index in ${e - s}ms"
    }
}
