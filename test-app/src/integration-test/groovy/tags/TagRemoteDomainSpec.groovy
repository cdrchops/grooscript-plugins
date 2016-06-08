package tags

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import grails.transaction.Rollback

@Integration
@Rollback
class TagRemoteDomainSpec extends GebSpec {

    void "remote domain a simple domain class with @Resource"() {
        when:
        go '/tagRemoteDomain'

        then:
        waitFor {
            $('#insert').text() == 'Created new book'
            $('#list').text() == '1 books in list.'
            $('#get').text() == 'Got same book by id.'
            $('#update').text() == 'Updated book to title: New title'
            $('#delete').text() == 'Book deleted!'
        }
    }
}
