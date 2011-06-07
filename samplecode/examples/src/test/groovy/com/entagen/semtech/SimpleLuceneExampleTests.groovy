package com.entagen.semtech

import org.junit.BeforeClass
import org.junit.Test

class SimpleLuceneExampleTests {

    static SesameSparqlExample sparqlExample

    @BeforeClass
    static void init() {
        sparqlExample = new SesameSparqlExample()
    }

    @Test
    void simpleSparqlQuery_exactMatchOnLabel() {
        //drugbank:targets/17 rdfs:label "Proto-oncogene tyrosine-protein kinase ABL1"

        String query = """
            SELECT ?uri ?label WHERE {
              ?uri rdfs:label ?label . ?uri rdf:type drugbank:targets .
              FILTER (?label = 'Proto-oncogene tyrosine-protein kinase ABL1')
            }
        """

        List results = sparqlExample.executeQuery(query)
        assert 1 == results.size()

        results.each {
            println it
        }
    }

    @Test
    void simpleSparqlQueryRegex() {
        //drugbank:targets/17 rdfs:label "Proto-oncogene tyrosine-protein kinase ABL1"

        String query = """
            SELECT ?uri ?label WHERE {
              ?uri rdfs:label ?label . ?uri rdf:type drugbank:targets .
              FILTER regex(?label, '\\\\QABL1\\\\E')
            }
        """

        List results = sparqlExample.executeQuery(query)
        assert 1 == results.size()

        results.each {
            println it
        }
    }

    @Test
    void simpleSparqlQueryCaseInsensitiveRegEx() {
        //drugbank:targets/17 rdfs:label "Proto-oncogene tyrosine-protein kinase ABL1"

        String query = """
            SELECT ?uri ?label WHERE {
              ?uri rdfs:label ?label . ?uri rdf:type drugbank:targets .
              FILTER regex(?label, '\\\\QABL1\\\\E', 'i')
            } LIMIT 1
        """

        List results = sparqlExample.executeQuery(query)
        assert 1 == results.size()

        results.each {
            println it
        }
    }

}
