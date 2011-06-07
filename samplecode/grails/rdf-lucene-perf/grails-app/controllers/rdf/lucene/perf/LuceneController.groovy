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

class LuceneController {

    static final String TYPE_FIELD = 'type'
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
        Query query

        if (params.type == 'Exact') {
            query = new PhraseQuery()
            query.add(new Term(LABEL_FIELD, params.query))
        } else {
            query = new TermQuery(new Term(LABEL_FIELD, params.query))
        }

        def s = new Date().time
        List results = executeQuery(query)
        def e = new Date().time

        render(view: 'index', model: [results: results, time: e - s])
    }

    public List executeQuery(Query query) {
        IndexSearcher searcher = luceneSearcherManager.get()
        ScoreDoc[] scoreDocs = searcher.search(query, 10).scoreDocs
        List results = []
        scoreDocs.each {
            Document doc = searcher.doc(it.doc)
            results << [uri: doc[SUBJECT_URI_FIELD], type: doc[TYPE_FIELD], label: doc[LABEL_FIELD]]
        }
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

            String queryLabelsAndTypes = """
               SELECT ?uri ?type ?label
               WHERE {
                   ?uri rdf:type ?type .
                   ?uri rdfs:label ?label .
               }
            """

            sparqlQueryService.executeForEach(repository, queryLabelsAndTypes) {
                def doc = new Document()

                doc.add(new Field("label", it.label.stringValue(), Field.Store.YES, Field.Index.ANALYZED))
                doc.add(new Field("uri", it.uri.stringValue(), Field.Store.YES, Field.Index.ANALYZED))
                doc.add(new Field("type", it.type.stringValue(), Field.Store.YES, Field.Index.ANALYZED))

                writer.addDocument(doc)
            }
        } finally {
            writer.close()  // Close index
        }

        def e = new Date().time

        render "Done building index in ${e - s}ms"
    }
}