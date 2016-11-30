package tags

import geb.MyGebTests

class TagRemoteDomainSpec extends MyGebTests {

    void "remote domain a simple domain class with @Resource"() {
        when:
        go '/tagRemoteDomain'

        then:
        waitFor {
            $('#insert').text() == 'Created new book'
            $('#list').text() == '1 books in list.' || '0 books in list.'//This is 0 in IE because GET's are cached
            $('#get').text() == 'Got same book by id.'
            $('#update').text() == 'Updated book to title: New title'
            $('#delete').text() == 'Book deleted!'
        }
    }
}
