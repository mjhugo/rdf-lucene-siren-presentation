package rdf.lucene.perf

class TimingController {

    def index = { }

    def clear = {
        ['siren','lucene','sparql'].each{
            session[it] = []
        }
        redirect action:'index'
    }

}
