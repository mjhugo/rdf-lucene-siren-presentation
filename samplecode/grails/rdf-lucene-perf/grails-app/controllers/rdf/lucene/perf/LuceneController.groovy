package rdf.lucene.perf

import org.apache.lucene.search.Query
import org.apache.lucene.search.PhraseQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.TermQuery
import org.apache.lucene.document.Field
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.util.Version

class LuceneController {

    static final String LABEL_FIELD = 'label'
    static final String SUBJECT_URI_FIELD = 'subjecturi'

    public static final String luceneIndexDir = 'target/lucene-index'

    def sparqlQueryService
    def repository
    def luceneSearcherManager

    def index = {
        render(view: 'index')
    }

    def query = {
        Query query = new QueryParser(
                Version.LUCENE_CURRENT,
                LABEL_FIELD,
                new StandardAnalyzer(Version.LUCENE_CURRENT)).parse(params.query);

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
        File indexDir = new File(luceneIndexDir);
        indexDir.delete()

        def s = new Date().time
        IndexWriter writer = new IndexWriter(org.apache.lucene.store.FSDirectory.open(indexDir),
                new StandardAnalyzer(org.apache.lucene.util.Version.LUCENE_CURRENT),
                true, IndexWriter.MaxFieldLength.UNLIMITED)

        try {

            String queryLabels = """
               SELECT ?uri ?label
               WHERE {
                   ?uri rdfs:label ?label .
               }
            """

            sparqlQueryService.executeForEach(repository, queryLabels) {
                def doc = new Document()
                String uri = it.uri.stringValue()
                String label = it.label.stringValue()

                doc.add(new Field(SUBJECT_URI_FIELD, uri,
                        Field.Store.YES, Field.Index.ANALYZED))
                doc.add(new Field(LABEL_FIELD, label,
                        Field.Store.NO, Field.Index.ANALYZED))

                writer.addDocument(doc)
            }
        } finally {
            writer.close()  // Close index
        }

        def e = new Date().time
        luceneSearcherManager.maybeReopen()
        render "Done building index in ${e - s}ms"
    }
}