package org.openmrs.module.visits.domain.criteria;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.module.visits.BaseTest;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class QuerySplitterTest extends BaseTest {

    private static final String GIVEN_NAME = "Another";
    private static final String MIDDLE_NAME = "Sick";
    private static final String FAMILY_NAME = "Person";
    private static final int QUERY_SIZE = 3;
    private static final int GIVEN_NAME_INDEX = 0;
    private static final int MIDDLE_NAME_INDEX = 1;
    private static final int FAMILY_NAME_INDEX = 2;

    @Test
    public void shouldParseQueryWithManyWhiteCharacters() {
        // Given
        final String searchTerm = "Another   Sick  Person";
        QuerySplitter splitter = new QuerySplitter(searchTerm);

        // When
        String[] actual = splitter.splitQuery();

        // Then
        assertNotNull(actual);
        assertEquals(actual.length, QUERY_SIZE);
        assertThat(actual[GIVEN_NAME_INDEX], equalTo(GIVEN_NAME));
        assertThat(actual[MIDDLE_NAME_INDEX], equalTo(MIDDLE_NAME));
        assertThat(actual[FAMILY_NAME_INDEX], equalTo(FAMILY_NAME));
    }

    @Test
    public void shouldParseQueryWithManyWhiteCharactersAndAtTheEnd() {
        // Given
        final String searchTerm = "Another   Sick  Person   \t";
        QuerySplitter splitter = new QuerySplitter(searchTerm);

        // When
        String[] actual = splitter.splitQuery();

        // Then
        assertNotNull(actual);
        assertEquals(actual.length, QUERY_SIZE);
        assertThat(actual[GIVEN_NAME_INDEX], equalTo(GIVEN_NAME));
        assertThat(actual[MIDDLE_NAME_INDEX], equalTo(MIDDLE_NAME));
        assertThat(actual[FAMILY_NAME_INDEX], equalTo(FAMILY_NAME));
    }
}
