package rdf.lucene.perf

class TimingFilters {

    def filters = {
        all(controller: '*', action: '*') {
            before = {

            }
            after = {model ->
                if (model.time != null) {
                    if (request.requestURI.indexOf('sparql') > 1) {
                        addTimingToSession('sparql', model.time, session)
                    } else if (request.requestURI.indexOf('lucene') > 1) {
                        addTimingToSession('lucene', model.time, session)
                    } else if (request.requestURI.indexOf('siren') > 1) {
                        addTimingToSession('siren', model.time, session)
                    }
                }
            }
            afterView = {

            }
        }
    }

    void addTimingToSession(String key, def time, session) {
        if (session[key]) {
            session[key] << time
        } else {
            session[key] = [time]
        }
    }

}
