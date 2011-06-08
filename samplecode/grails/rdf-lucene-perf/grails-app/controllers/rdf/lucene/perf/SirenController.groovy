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
import org.sindice.siren.search.SirenTupleClause
import org.sindice.siren.search.SirenTupleQuery
import org.apache.lucene.index.Term
import org.sindice.siren.search.SirenTermQuery
import org.openrdf.model.vocabulary.RDFS
import org.sindice.siren.search.SirenPrimitiveQuery
import org.sindice.siren.search.SirenPhraseQuery

class SirenController {
    static final String TRIPLES_FIELD = 'triples'
    static final String SUBJECT_URI_FIELD = 'subjecturi'

    Integer SUBJECT_CELL = 0
    Integer PREDICATE_CELL = 1
    Integer OBJECT_CELL = 2

    public static final String sirenIndexDir = 'target/siren-index'

    def sparqlQueryService
    def repository
    def sirenSearcherManager

    def index = { }

    def query = {
        SirenCellQuery predicateCellQuery = new SirenCellQuery(
                new SirenTermQuery(new Term(TRIPLES_FIELD, RDFS.LABEL.stringValue())));
        predicateCellQuery.constraint = PREDICATE_CELL

        SirenPrimitiveQuery objectQuery
        if (params.query.indexOf(' ') > 0) {
            objectQuery = buildPhraseQuery()
        } else {
            objectQuery = new SirenTermQuery(new Term(TRIPLES_FIELD, params.query.toLowerCase()))

        }
        SirenCellQuery objectCellQuery = new SirenCellQuery(objectQuery);
        objectCellQuery.constraint = OBJECT_CELL

        SirenTupleQuery query = new SirenTupleQuery()
        query.add(predicateCellQuery, SirenTupleClause.Occur.MUST)
        query.add(objectCellQuery, SirenTupleClause.Occur.MUST)

        def s = new Date().time
        List results = executeQuery(query)
        def e = new Date().time

        render(view: 'index', model: [results: results, time: e - s])
    }

    SirenPrimitiveQuery buildPhraseQuery() {
        def query = new SirenPhraseQuery()
        params.query.split(' ').each {
            query.add(new Term(TRIPLES_FIELD, it.toLowerCase()))
        }
        return query
    }

    public List executeQuery(Query query) {
        IndexSearcher searcher = sirenSearcherManager.get()
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

                String subjectUri = it.uri.stringValue()
                doc.add(new Field(SUBJECT_URI_FIELD, subjectUri,
                        Field.Store.YES, Field.Index.ANALYZED))

                StringWriter triplesStringWriter = new StringWriter()
                NTriplesWriter nTriplesWriter = new NTriplesWriter(triplesStringWriter)
                connection.exportStatements(new URIImpl(subjectUri),
                        null, null, false, nTriplesWriter)

                doc.add(new Field(TRIPLES_FIELD, triplesStringWriter.toString(),
                        Field.Store.NO, Field.Index.ANALYZED))

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
